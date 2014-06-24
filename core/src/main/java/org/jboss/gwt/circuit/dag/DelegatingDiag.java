package org.jboss.gwt.circuit.dag;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Dispatcher;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 23/06/14
 */
public class DelegatingDiag implements DAGDispatcher.Diagnostics {

    private final List<DAGDispatcher.Diagnostics> diagnostics = new LinkedList<>();

    @Override
    public void onDispatch(final Action a) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onDispatch(a); }
    }

    @Override
    public void onLock() {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onLock(); }
    }

    @Override
    public void onExecute(final Class<?> s, final Action a) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onExecute(s, a); }
    }

    @Override
    public void onAck(final Class<?> s, final Action a) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onAck(s, a); }
    }

    @Override
    public void onNack(final Class<?> s, final Action a, final Throwable t) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onNack(s, a, t); }
    }

    @Override
    public void onUnlock() {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onUnlock(); }
    }

    void add(final Dispatcher.Diagnostics d) {
        if (!(d instanceof DAGDispatcher.Diagnostics)) {
            throw new IllegalArgumentException("Diagnostics must be of type " + DAGDispatcher.Diagnostics.class);
        }
        this.diagnostics.add((DAGDispatcher.Diagnostics) d);
    }

    void remove(Dispatcher.Diagnostics d) {
        this.diagnostics.remove(d);
    }
}
