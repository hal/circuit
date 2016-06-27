package org.jboss.gwt.circuit;

/**
 * Sample store used for tests.
 */
public class BarStore extends ChangeSupport {

    public BarStore(Dispatcher dispatcher) {
        dispatcher.register(BarStore.class, new StoreCallback() {
            @Override
            public Agreement voteFor(Action action) {
                return vote(action);
            }

            @Override
            public void complete(Action action, Dispatcher.Channel channel) {
                process(action, channel);
            }

            @Override
            public void signalChange(final Action action) {
                fireChange(action);
            }

            @Override
            public void signalError(final Action action, final Throwable throwable) {
                fireError(action, throwable);
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
