package org.jboss.gwt.circuit.sample.todo.client.actions;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

/**
 * @author Heiko Braun
 * @date 25/06/14
 */
public class ResolveTodo implements Action<Todo> {
    Todo todo;

    public ResolveTodo(Todo todo) {
        this.todo = todo;
    }

    @Override
    public Todo getPayload() {
        return todo;
    }
}
