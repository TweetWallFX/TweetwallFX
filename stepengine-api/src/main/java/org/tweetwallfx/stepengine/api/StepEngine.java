/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2019 TweetWallFX
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
package org.tweetwallfx.stepengine.api;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.TweetwallSettings;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;

/**
 * @author Jörg Michelberger
 */
public final class StepEngine {

    private static final Logger LOGGER = LogManager.getLogger("org.tweetwallfx.startup");
    private static final Logger LOG = LogManager.getLogger(StepEngine.class);
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("StepEngine");
    private volatile boolean terminated = false;
    private final Phaser asyncProceed = new Phaser(2);
    private final StepIterator stepIterator;
    private final MachineContext context = new MachineContext();
    private final ExecutorService engineExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(THREAD_GROUP, r, "engine");
        t.setDaemon(true);
        return t;
    });
    private final ScheduledExecutorService scheduleExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(THREAD_GROUP, r, "schedule");
        t.setDaemon(true);
        return t;
    });

    public StepEngine() {
        LOGGER.info("create StepIterator");
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
        LOGGER.info("init DataProviders");

        final String searchText = Configuration.getInstance().getConfigTyped(TweetwallSettings.CONFIG_KEY, TweetwallSettings.class).getQuery();
        LOGGER.info("query: " + searchText);

        LOGGER.info("create DataProviders");
        final Map<String, StepEngineSettings.DataProviderSetting> dataProviderSettings = Configuration.getInstance()
                .getConfigTyped(StepEngineSettings.CONFIG_KEY, StepEngineSettings.class)
                .getDataProviderSettings()
                .stream()
                .collect(Collectors.toMap(
                        StepEngineSettings.DataProviderSetting::getDataProviderClassName,
                        Function.identity(),
                        (dps1, dps2) -> {
                            throw new IllegalArgumentException("At most one DataProviderSetting entry may exist for a DataProvider type (uncompliant DataProvider type: '" + dps1.getDataProviderClassName() + "').");
                        }));
        final List<DataProvider> providers = StreamSupport.stream(ServiceLoader.load(DataProvider.Factory.class).spliterator(), false)
                .filter(factory -> requiredDataProviders.contains(factory.getDataProviderClass()))
                .map(dpf
                        -> dpf.create(dataProviderSettings.getOrDefault(
                        dpf.getDataProviderClass().getName(),
                        new StepEngineSettings.DataProviderSetting())))
                .peek(dataProvider -> LOG.info("created " + dataProvider))
                .collect(Collectors.toList());

        requiredDataProviders.stream()
                .filter(rdpc -> providers.stream().noneMatch(rdpc::isInstance))
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
        providers.stream()
                .filter(DataProvider.Scheduled.class::isInstance)
                .map(DataProvider.Scheduled.class::cast)
                .forEach(this::initScheduledDataProvider);

        if (!newTweetAwareProviders.isEmpty()) {
            LOGGER.info("create TweetStream");
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

        LOGGER.info("initDataProviders done");
        providers.forEach(context::addDataProvider);
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    private void initScheduledDataProvider(final DataProvider.Scheduled scheduled) {
        LOGGER.info("initializing Scheduled: {}", scheduled);
        final DataProvider.ScheduledConfig sc = scheduled.getScheduleConfig();

        try {
            if (DataProvider.ScheduleType.FIXED_DELAY == sc.getScheduleType()) {
                scheduleExecutor.scheduleAtFixedRate(scheduled, sc.getInitialDelay(), sc.getScheduleDuration(), TimeUnit.SECONDS);
            } else {
                scheduleExecutor.scheduleWithFixedDelay(scheduled, sc.getInitialDelay(), sc.getScheduleDuration(), TimeUnit.SECONDS);
            }
        } catch (final RuntimeException re) {
            LOGGER.fatal("failed to initializing Scheduled: {}", scheduled, re);
            throw re;
        }
    }

    public final class MachineContext {

        private final Map<String, Object> properties = new ConcurrentHashMap<>();
        private final ObservableList<DataProvider> dataProviders = FXCollections.<DataProvider>observableArrayList();
        private final FilteredList<DataProvider> filteredDataProviders = dataProviders.filtered(null);

        public Object get(final String key) {
            return properties.get(key);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(final String key, final Class<T> clazz) {
            return (T) properties.get(key);
        }

        public Object put(final String key, final Object value) {
            return properties.put(key, value);
        }

        public void proceed() {
            LOG.info("Proceed called");
            asyncProceed.arrive();
        }

        void addDataProvider(final DataProvider dataProvider) {
            dataProviders.add(Objects.requireNonNull(
                    dataProvider,
                    "Parameter dataProvider must not be null!"));
        }

        @SuppressWarnings("unchecked")
        public <T extends DataProvider> T getDataProvider(final Class<T> klazz) {
            return filteredDataProviders
                    .stream()
                    .filter(klazz::isInstance)
                    .map(klazz::cast)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("A DataProvider of type '" + klazz.getName() + "' is currently not available."));
        }

        private void restrictAvailableDataProviders(final Collection<Class<? extends DataProvider>> dataProviderClasses) {
            LOG.info("restricting available DataProviders to {}", dataProviderClasses);
            filteredDataProviders.setPredicate(d -> dataProviderClasses.contains(d.getClass()));
            filteredDataProviders.forEach(dp -> LOG.info("DataProvider available after restriction: {}", dp));
        }
    }

    public void go() {
        engineExecutor.execute(this::process);
    }

    private void process() {
        while (!terminated) {
            LOG.info("process to next step ");

            final long start = System.currentTimeMillis();

            Step step = stepIterator.next();
            context.restrictAvailableDataProviders(stepIterator.getRequiredDataProviders(step));
            while (step.shouldSkip(context)) {
                LOG.info("Skip step: {}", step.getClass().getSimpleName());
                step = stepIterator.next();
                context.restrictAvailableDataProviders(stepIterator.getRequiredDataProviders(step));
            }
            final Step stepToExecute = step;
            final Duration duration = step.preferredStepDuration(context);

            LOG.info("call {}.doStep()", stepToExecute.getClass().getSimpleName());

            if (stepToExecute.requiresPlatformThread()) {
                Platform.runLater(() -> {
                    try {
                        stepToExecute.doStep(context);
                    } catch (RuntimeException | Error e) {
                        LOG.fatal("StepExecution has terminal failure {} ", stepToExecute.getClass().getSimpleName());
                        LOG.fatal("caused by", e);
                        // enforce that animation continues
                        context.proceed();
                    }
                });
            } else {
                    try {
                        stepToExecute.doStep(context);
                    } catch (RuntimeException | Error e) {
                        LOG.fatal("StepExecution has terminal failure {} ", stepToExecute.getClass().getSimpleName());
                        LOG.fatal("caused by", e);
                        // enforce that animation continues
                        context.proceed();
                    }
            }

            final long stop = System.currentTimeMillis();
            final long doStateDuration = stop - start;
            final long delay = duration.toMillis() - doStateDuration;
            if (delay > 0) {
                try {
                    LOG.info("sleep({} ms) for step {}", delay, step.getClass().getSimpleName());
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    LOG.error("Sleeping for {} interrupted!", delay, ex);
                    Thread.currentThread().interrupt();
                }
            }
            LOG.info("waiting (possible) for step to call proceed {}", step.getClass().getSimpleName());
            try {
                // wait for proceed being called
                asyncProceed.awaitAdvanceInterruptibly(asyncProceed.arrive(), 60, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                LOG.error("Await proceed interrupted", ex);
            } catch (TimeoutException ex) {
                LOG.error("Await proceed timed out", ex);
            }
        }
    }
}
