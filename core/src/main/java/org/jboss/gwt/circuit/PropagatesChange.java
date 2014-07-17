package org.jboss.gwt.circuit;

import com.google.web.bindery.event.shared.HandlerRegistration;

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
    HandlerRegistration addChangeHandler(Handler handler);

    /**
     * Registers a {@link PropagatesChange.Handler} to be notified only when the store was
     * modified by the specified action type.
     */
    HandlerRegistration addChangeHandler(Class<?> actionType, Handler handler);
}
