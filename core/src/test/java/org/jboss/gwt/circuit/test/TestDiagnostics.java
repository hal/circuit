package org.jboss.gwt.circuit.test;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Store;
import org.jboss.gwt.circuit.impl.DAGDispatcher;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 23/06/14
 */
public class TestDiagnostics implements DAGDispatcher.Diagnostics {

    private boolean locked;
    private int numDispatched;
    private int numExecuted;
    private int numAcked;
    private int numNacked;

    private List<Class<?>> executionOrder;

    public List<Class<?>> getExecutionOrder() {
        return executionOrder;
    }

    public TestDiagnostics() {
        reset();
    }

    public boolean isLocked() {
        return locked;
    }

    public void reset() {
        executionOrder = new LinkedList<>();
        locked = false;
        numDispatched = 0;
        numExecuted = 0;
        numAcked = 0;
        numNacked = 0;
    }

    public int getNumDispatched() {
        return numDispatched;
    }

    public int getNumExecuted() {
        return numExecuted;
    }

    public int getNumAcked() {
        return numAcked;
    }

    public int getNumNacked() {
        return numNacked;
    }

    // -----------------------

    @Override
    public void onDispatch(Action a) {
        numDispatched++;
    }

    @Override
    public void onLock() {
        locked = true;
    }

    @Override
    public void onExecute(Class<?> s, Action a) {
        numExecuted++;
        executionOrder.add(s);
    }

    @Override
    public void onAck(Class<?> s, Action a) {
        numAcked++;
    }

    @Override
    public void onNack(Class<?> s, Action a, Throwable t) {
        numNacked++;
    }

    @Override
    public void onUnlock() {
        locked = false;
    }
}