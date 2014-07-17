package org.jboss.gwt.circuit;

/**
 * Interface meant to be implemented by stores in order to participate in change events.
 */
public interface PropagatesChange {

    public interface Handler {

        void onChanged(Class<?> actionType);
    }

    /**
     * Registers a {@link PropagatesChange.Handler} to be notified when the store was modified.
     */
    void addChangedHandler(Handler handler);

    /**
     * Registers a {@link PropagatesChange.Handler} to be notified only when the store was
     * modified by the specified action type.
     */
    void addChangedHandler(Class<?> actionType, Handler handler);
}
