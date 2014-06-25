package org.jboss.gwt.circuit.test;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.Store;

/**
 * @author Heiko Braun
 * @date 23/06/14
 */
public class FooStore {

    private Dispatcher dispatcher;

    public FooStore(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        this.dispatcher.register(FooStore.class, new Store.Callback() {
            @Override
            public Agreement voteFor(Action action) {
                return vote(action);
            }

            @Override
            public void complete(Action action, Dispatcher.Channel channel) {
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

}
