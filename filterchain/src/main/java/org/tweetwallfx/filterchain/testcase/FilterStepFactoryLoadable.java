package org.tweetwallfx.filterchain.testcase;

import java.util.ServiceLoader;
import org.tweetwallfx.filterchain.FilterStep;
import org.tweetwallfx.util.testcase.RunnableTestCase;

/**
 * Testcase checking that all registered {@link FilterStep.Factory} instances
 * are loadable.
 */
public class FilterStepFactoryLoadable implements RunnableTestCase {

    @Override
    public void execute() throws Exception {
        for (final FilterStep.Factory o : ServiceLoader.load(FilterStep.Factory.class)) {
            System.out.println("loaded " + o.getClass());
        }
    }
}
