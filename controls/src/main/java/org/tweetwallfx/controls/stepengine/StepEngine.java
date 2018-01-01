/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tweetwallfx.controls.stepengine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.TweetwallSettings;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;

/**
 * @author Jörg Michelberger
 */
public final class StepEngine {

    private static final Logger STARTUP_LOGGER = LogManager.getLogger("org.tweetwallfx.startup");
    private static final Logger LOG = LogManager.getLogger(StepEngine.class);
    private volatile boolean terminated = false;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final StepIterator stepIterator;
    private final MachineContext context = new MachineContext();

    public StepEngine() {
        STARTUP_LOGGER.info("create StepIterator");
        stepIterator = StepIterator.create();
        initDataProviders();
        //initialize every step with context
        stepIterator.applyWith(step -> step.initStep(context));
    }

    public MachineContext getContext() {
        return context;
    }

    private void initDataProviders() {
        final Set<Class<? extends DataProvider>> requiredDataProviders = stepIterator.getRequiredDataProviders();
        STARTUP_LOGGER.info("init DataProviders");

        final String searchText = Configuration.getInstance().getConfigTyped(TweetwallSettings.CONFIG_KEY, TweetwallSettings.class).getQuery();
        STARTUP_LOGGER.info("query: " + searchText);

        STARTUP_LOGGER.info("create DataProviders");
        final List<DataProvider> providers = StreamSupport.stream(ServiceLoader.load(DataProvider.Factory.class).spliterator(), false)
                .filter(factory -> requiredDataProviders.contains(factory.getDataProviderClass()))
                .map(DataProvider.Factory::create)
                .peek(dataProvider -> LOG.info("created " + dataProvider))
                .collect(Collectors.toList());

        requiredDataProviders.stream()
                .filter(rdpc -> !providers.stream().anyMatch(rdpc::isInstance))
                .findAny()
                .ifPresent(rdpc -> {
                    throw new IllegalStateException("DataProvider '" + rdpc.getCanonicalName() + "' is required but no DataProvider.Factory was found creating it!");
                });

        final List<DataProvider.NewTweetAware> newTweetAwareProviders = providers.stream()
                .filter(DataProvider.NewTweetAware.class::isInstance)
                .map(DataProvider.NewTweetAware.class::cast)
                .collect(Collectors.toList());
        final List<DataProvider.HistoryAware> historyAwareProviders = providers.stream()
                .filter(DataProvider.HistoryAware.class::isInstance)
                .map(DataProvider.HistoryAware.class::cast)
                .collect(Collectors.toList());

        if (!newTweetAwareProviders.isEmpty()) {
            STARTUP_LOGGER.info("create TweetStream");
            final TweetFilterQuery query = new TweetFilterQuery()
                    .track(Pattern.compile(" [oO][rR] ").splitAsStream(searchText).toArray(n -> new String[n]));
            final TweetStream tweetStream = Tweeter.getInstance().createTweetStream(query);
            
            newTweetAwareProviders.forEach(ntadp -> tweetStream.onTweet(ntadp::processNewTweet));
        }

        if (!historyAwareProviders.isEmpty()) {
            Tweeter.getInstance()
                    .searchPaged(new TweetQuery().query(searchText).count(100), 20)
                    .forEach(tweet -> historyAwareProviders.stream().forEach(hap -> hap.processHistoryTweet(tweet)));
        }

        STARTUP_LOGGER.info("initDataProviders done");
        providers.forEach(context::addDataProvider);
    }

    public final class MachineContext {

        private final Map<String, Object> properties = new HashMap<>();
        private final List<DataProvider> dataProviders = new ArrayList<>();

        public Object get(final String key) {
            return properties.get(key);
        }

        public Object put(final String key, final Object value) {
            return properties.put(key, value);
        }

        public void proceed() {
            StepEngine.this.proceed();
        }

        public void addDataProvider(final DataProvider dataProvider) {
            dataProviders.add(Objects.requireNonNull(
                    dataProvider,
                    "Parameter dataProvider must not be null!"));
        }

        @SuppressWarnings("unchecked")
        public <T extends DataProvider> T getDataProvider(final Class<T> klazz) {
            return dataProviders
                    .stream()
                    .filter(klazz::isInstance)
                    .map(klazz::cast)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("A DataProvider of type '" + klazz.getName() + "' has not been registered."));
        }
    }

    public void go() {
        process();
    }

    void proceed() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    void process() {
        while (!terminated) {
            LOG.info("process to next step ");

            long start = System.currentTimeMillis();

            Step step = stepIterator.next();
            while (step.shouldSkip(context)) {
                LOG.info("Skip step: {}", step.getClass().getSimpleName());
                step = stepIterator.next();
            }
            final Step stepToExecute = step;
            Duration duration = step.preferredStepDuration(context);
            LOG.info("call {}.doStep()", stepToExecute.getClass().getSimpleName());
            lock.lock();
            try {
                if (stepToExecute.requiresPlatformThread()) {
                    Platform.runLater(() -> stepToExecute.doStep(context));
                } else {
                    stepToExecute.doStep(context);
                }
                long stop = System.currentTimeMillis();
                long doStateDuration = stop - start;
                long delay = duration.toMillis() - doStateDuration;
                if (delay > 0) {
                    try {
                        LOG.info("sleep({}ms) for step {}", delay, step.getClass().getSimpleName());
                        Thread.sleep(delay);
                    } catch (InterruptedException ex) {
                        LOG.error("Sleeping for {} interrupted!", delay, ex);
                    }
                }
                LOG.info("wait for step {} to finish!", step.getClass().getSimpleName());
                condition.await();
            } catch (InterruptedException ex) {
                LOG.error("Waiting interrupted!", ex);
            } finally {
                lock.unlock();
            }
        }
    }
}
