package org.jboss.gwt.circuit;

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

