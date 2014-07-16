package org.jboss.gwt.circuit;

/**
 * @author Heiko Braun
 * @date 23/06/14
 */
public class FooStore {

    public FooStore(Dispatcher dispatcher) {
        dispatcher.register(FooStore.class, new StoreCallback() {
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
