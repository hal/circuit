package org.jboss.gwt.circuit.test;

import org.jboss.gwt.circuit.Action;

class FooBarAction implements Action<Integer> {
    Integer payload;

    FooBarAction(Integer payload) {
        this.payload = payload;
    }

    @Override
    public Integer getPayload() {
        return payload;
    }
}

