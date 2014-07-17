package org.jboss.gwt.circuit.sample.todo.client.actions;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

/**
 * @author Heiko Braun
 * @date 25/06/14
 */
public class SelectUser implements Action<String> {

    private String user;

    public SelectUser(String user) {
        if(null==user)
            user = Todo.USER_ANY;
        this.user = user;
    }

    @Override
    public String getPayload() {
        return user;
    }
}
