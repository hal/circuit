package org.jboss.gwt.circuit.sample.todo.client.actions;

import org.jboss.gwt.circuit.Action;

public class RemoveUser implements Action {

    private final String user;

    public RemoveUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
