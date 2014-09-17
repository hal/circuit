package org.jboss.gwt.circuit;

import org.jboss.gwt.circuit.dag.DAGDispatcher;

import java.util.LinkedList;
import java.util.List;

public class TestDiagnostics implements DAGDispatcher.Diagnostics {

    private boolean locked;
    private int numDispatched;
    private int numExecuted;
    private int numAcked;
    private int numNackedByReason;
    private int numNackedByThrowable;

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
        numNackedByReason = 0;
        numNackedByThrowable = 0;
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

    public int getNumNackedByReason() {
        return numNackedByReason;
    }

    public int getNumNackedByThrowable() {
        return numNackedByThrowable;
    }

    @Override
    public void onDispatch(Action action) {
        numDispatched++;
    }

    @Override
    public void onLock() {
        locked = true;
    }

    @Override
    public void onExecute(Class<?> store, Action action) {
        numExecuted++;
        executionOrder.add(store);
    }

    @Override
    public void onAck(Class<?> store, Action action) {
        numAcked++;
    }

    @Override
    public void onNack(Class<?> store, Action action, String reason) {
        numNackedByReason++;
    }

    @Override
    public void onNack(Class<?> store, Action action, Throwable throwable) {
        numNackedByThrowable++;
    }

    @Override
    public void onUnlock() {
        locked = false;
    }
}