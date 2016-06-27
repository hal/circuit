package org.jboss.gwt.circuit;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Interface meant to be implemented by stores in order to handle nacked actions.
 */
public interface PropagatesError {

    interface ErrorHandler {

        void onError(Action action, Throwable throwable);
    }

    /**
     * Registers a {@link ErrorHandler} to be notified when the store nacked an arbitrary action.
     */
    HandlerRegistration addErrorHandler(ErrorHandler handler);

    /**
     * Registers a {@link ErrorHandler} to be notified only when the store nacked the specified action type.
     */
    HandlerRegistration addErrorHandler(Class<? extends Action> actionType, ErrorHandler handler);

    /**
     * Registers a {@link ErrorHandler} to be notified only when the store nacked the specified action
     * instance.
     */
    HandlerRegistration addErrorHandler(Action action, ErrorHandler handler);
}
