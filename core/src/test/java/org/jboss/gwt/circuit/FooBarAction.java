package org.jboss.gwt.circuit;

class FooBarAction implements Action {

    int payload;

    FooBarAction(int payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FooBarAction)) return false;

        FooBarAction that = (FooBarAction) o;

        if (payload != that.payload) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return payload;
    }

    @Override
    public String toString() {
        return "FooBarAction(" + payload + ")";
    }
}
