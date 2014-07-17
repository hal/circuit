package org.jboss.gwt.circuit.dag;

/**
 * @author Heiko Braun
 */
public class CycleDetected extends RuntimeException {

    public CycleDetected(String message) {
        super(message);
    }
}
