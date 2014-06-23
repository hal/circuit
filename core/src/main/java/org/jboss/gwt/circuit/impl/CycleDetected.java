package org.jboss.gwt.circuit.impl;

/**
 * @author Heiko Braun
 * @date 23/06/14
 */
public class CycleDetected extends RuntimeException {

    public CycleDetected(String message) {
        super(message);
    }
}
