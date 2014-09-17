package org.jboss.gwt.circuit.sample.todo.client.actions;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

public class ResolveTodo implements Action {

    private final Todo todo;

    public ResolveTodo(Todo todo) {
        this.todo = todo;
    }

    public Todo getTodo() {
        return todo;
    }
}
