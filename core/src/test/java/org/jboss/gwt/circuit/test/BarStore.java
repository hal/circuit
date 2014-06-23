package org.jboss.gwt.circuit.test;

import com.google.web.bindery.event.shared.HandlerRegistration;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.Store;
import org.jboss.gwt.circuit.StoreChangedEvent;

/**
 * @author Heiko Braun
 * @date 23/06/14
 */
public class BarStore implements Store {

    private Dispatcher dispatcher;

    public BarStore(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        this.dispatcher.register(BarStore.class, new Callback() {
            @Override
            public Agreement voteFor(Action action) {
                return vote(action);
            }

            @Override
            public void execute(Action action, Dispatcher.Channel channel) {
                process(action, channel);
            }
        });
    }

    protected Agreement vote(Action action) {
        return Agreement.ANY;
    }

    protected void process(Action action, Dispatcher.Channel channel) {
        channel.ack();
    }

    @Override
    public HandlerRegistration addChangedHandler(StoreChangedEvent.StoreChangedHandler handler) {
        return null;
    }
}
