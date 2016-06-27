package org.jboss.gwt.circuit;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Interface meant to be implemented by stores in order to participate in change events.
 */
public interface PropagatesChange {

    interface ChangeHandler {

        void onChange(Action action);
    }

    /**
     * Registers a {@link ChangeHandler} to be notified when the store was modified.
     */
    HandlerRegistration addChangeHandler(ChangeHandler handler);

    /**
     * Registers a {@link ChangeHandler} to be notified only when the store was
     * modified by the specified action type.
     */
    HandlerRegistration addChangeHandler(Class<? extends Action> actionType, ChangeHandler handler);

    /**
     * Registers a {@link ChangeHandler} to be notified only when the store was
     * modified by the specified action instance.
     */
    HandlerRegistration addChangeHandler(Action action, ChangeHandler handler);
}
