package org.jboss.gwt.circuit;

public interface PropagatesChange {

    /**
     * Stores emit change events to all registered change handlers when their internal state was modified.
     */
    public interface Handler {

        /**
         * Triggered by stores when their internal state was modified.
         *
         * @param source     The store class. Must not be {@code null}
         * @param actionType The action which triggered the change. Can be {@code null} if the change was not triggered
         *                   by an action but some external event.
         */
        void onChange(Class<?> source, Class<?> actionType);
    }

    /**
     * Registers a {@link org.jboss.gwt.circuit.PropagatesChange.Handler}
     */
    void addChangeHandler(Handler handler);
}
