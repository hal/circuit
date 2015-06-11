package org.jboss.gwt.circuit;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Interface meant to be implemented by stores in order to handle nacked actions.
 */
public interface ErrorHandling {

    interface Handler {

        void onError(Action action);
    }

    /**
     * Registers a {@link ErrorHandling.Handler} to be notified only when the store was
     * modified by the specified action type.
     */
    HandlerRegistration addChangeHandler(Class<? extends Action> actionType, Handler handler);

    /**
     * Registers a {@link ErrorHandling.Handler} to be notified only when the store was
     * modified by the specified action instance.
     */
    HandlerRegistration addChangeHandler(Action action, Handler handler);
}
