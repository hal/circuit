package org.jboss.gwt.flux.sample.todo.client.actions;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

/**
 * @author Heiko Braun
 * @date 16/06/14
 */
public final class TodoAction implements Action<TodoActions, Todo> {

    private TodoActions type;
    private Todo payload;

    public TodoAction(TodoActions type) {
        this.type = type;
    }

    public TodoAction(TodoActions type, Todo payload) {
        this.type = type;
        this.payload = payload;
    }

    @Override
    public TodoActions getType() {
        return type;
    }

    @Override
    public Todo getPayload() {
        return payload;
    }
}
