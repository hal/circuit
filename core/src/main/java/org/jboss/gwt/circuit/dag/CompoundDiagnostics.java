package org.jboss.gwt.circuit.dag;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Dispatcher;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 */
public class CompoundDiagnostics implements DAGDispatcher.Diagnostics {

    private final List<DAGDispatcher.Diagnostics> diagnostics = new LinkedList<>();

    @Override
    public void onDispatch(final Action action) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onDispatch(action); }
    }

    @Override
    public void onLock() {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onLock(); }
    }

    @Override
    public void onExecute(final Class<?> store, final Action action) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onExecute(store, action); }
    }

    @Override
    public void onAck(final Class<?> store, final Action action) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onAck(store, action); }
    }

    @Override
    public void onNack(Class<?> store, Action action, String reason) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onNack(store, action, reason); }
    }

    @Override
    public void onNack(final Class<?> store, final Action action, final Throwable throwable) {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onNack(store, action, throwable); }
    }

    @Override
    public void onUnlock() {
        for (DAGDispatcher.Diagnostics d : diagnostics) { d.onUnlock(); }
    }

    void add(final Dispatcher.Diagnostics diagnostics) {
        if (!(diagnostics instanceof DAGDispatcher.Diagnostics)) {
            throw new IllegalArgumentException("Diagnostics must be of type " + DAGDispatcher.Diagnostics.class);
        }
        this.diagnostics.add((DAGDispatcher.Diagnostics) diagnostics);
    }

    void remove(Dispatcher.Diagnostics diagnostics) {
        this.diagnostics.remove(diagnostics);
    }
}
