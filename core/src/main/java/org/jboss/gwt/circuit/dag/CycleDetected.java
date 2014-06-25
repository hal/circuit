package org.jboss.gwt.circuit.dag;

public class CycleDetected extends RuntimeException {

    public CycleDetected(String message) {
        super(message);
    }
}
