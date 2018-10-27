package org.tweetwallfx.filterchain;

/**
 * A Step in the filter chain checking for something specific.
 *
 * @param <T> the type of the object to check
 */
public interface FilterStep<T> {

    /**
     * Checks the given object and returns a {@link Result} of the evaluation.
     *
     * @param t the object to check
     *
     * @return the result of the evaluation
     */
    Result check(final T t);

    /**
     * The result of a {@link FilterStep} processing an object.
     */
    enum Result {

        /**
         * No definite answer was determined for the processed object.
         *
         * Other {@link FilterStep}s will have to be checked.
         */
        NOTHING_DEFINITE(false, true),
        /**
         * The processed object was accepted by the {@link FilterStep}.
         *
         * No further {@link FilterStep}s will have to be checked.
         */
        ACCEPTED(true, true),
        /**
         * The processed object was rejected by the {@link FilterStep}.
         *
         * No further {@link FilterStep}s will have to be checked.
         */
        REJECTED(true, false);

        private final boolean terminal;
        private final boolean accepted;

        Result(final boolean terminal, final boolean accepted) {
            this.terminal = terminal;
            this.accepted = accepted;
        }

        /**
         * Returns a boolean indicating if the evaluated object was accepted by
         * the {@link FilterStep} evaluation. Only applicable for terminal
         * {@link Result}s.
         *
         * @return a boolean indicating if the evaluated object was accepted by
         * the {@link FilterStep} evaluation
         */
        public boolean isAccepted() {
            return accepted;
        }

        /**
         * Returns a boolean flag indicating if this {@link Result} is a
         * terminal result.
         *
         * @return a boolean flag indicating if this {@link Result} is a
         * terminal result
         */
        public boolean isTerminal() {
            return terminal;
        }
    }

    /**
     * A Factory creating a {@link FilterStep}.
     */
    interface Factory {

        /**
         * Returns the class of the domain object of the FilterStep this factory
         * will create.
         *
         * @return the class of the domain object of the FilterStep this factory
         * will create
         */
        Class<?> getDomainObjectClass();

        /**
         * Returns the class of the FilterStep this factory will create.
         *
         * @return the class of the FilterStep this factory will create
         */
        Class<? extends FilterStep<?>> getFilterStepClass();

        /**
         * Creates a FilterStep.
         *
         * @param filterStepDefinition the settings object for which the
         * FilterStep is to be created
         *
         * @return the created FilterStep
         */
        FilterStep<?> create(final FilterChainSettings.FilterStepDefinition filterStepDefinition);
    }
}
