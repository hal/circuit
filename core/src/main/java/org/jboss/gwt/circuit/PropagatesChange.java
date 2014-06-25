package org.jboss.gwt.circuit;

/**
 * @author Heiko Braun
 * @date 25/06/14
 */
public interface PropagatesChange {

    /**
     * Stores emit change events to all registered change handlers when their internal state or is modified.
     */
    public interface Handler {
        void onChange(Class<?> source);
    }

    /**
     * Registers a {@link org.jboss.gwt.circuit.PropagatesChange.Handler}
     */
    void addChangeHandler(Handler handler);
}
