package org.jboss.gwt.circuit.sample.todo.client.actions;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

public class SelectUser implements Action {

    private final String user;

    public SelectUser(String user) {
        if (null == user) {
            user = Todo.USER_ANY;
        }
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
