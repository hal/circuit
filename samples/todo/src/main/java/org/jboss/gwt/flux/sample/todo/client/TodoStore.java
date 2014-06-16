/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.gwt.flux.sample.todo.client;

import static org.jboss.gwt.flux.sample.todo.client.actions.TodoActions.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.gwt.flux.AbstractStore;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.sample.todo.client.actions.TodoAction;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

@ApplicationScoped
public class TodoStore extends AbstractStore {

    abstract class TodoCallback<T> implements AsyncCallback<T> {

        @Override
        public void onFailure(final Throwable caught) {
            // TODO Error handling
        }
    }


    private final List<Todo> todos;
    private final TodoServiceAsync todoService;

    @Inject
    public TodoStore(final Dispatcher dispatcher, final TodoServiceAsync todoService) {
        this.todos = new LinkedList<>();
        this.todoService = todoService;

        dispatcher.register(new Callback<TodoAction>() {
            @Override
            public void execute(final TodoAction action, final Dispatcher.Context context) {
                process(action, context);
            }
        }, SAVE, LIST, REMOVE);
    }

    private void process(final TodoAction action, final Dispatcher.Context context) {
        switch (action.getType()) {
            case LIST:
                todoService.list(new TodoCallback<Collection<Todo>>() {
                    @Override
                    public void onSuccess(final Collection<Todo> result) {
                        todos.clear();
                        todos.addAll(result);
                        context.yield();
                        fireChanged();
                    }
                });
                break;
            case SAVE:
                todoService.save(action.getPayload(), new TodoCallback<Void>() {
                    @Override
                    public void onSuccess(final Void result) {
                        process(new TodoAction(LIST), context);
                    }
                });
                break;
            case REMOVE:
                todoService.delete(action.getPayload(), new TodoCallback<Void>() {
                    @Override
                    public void onSuccess(final Void result) {
                        process(new TodoAction(LIST), context);
                    }
                });
                break;
        }
    }

    public List<Todo> getTodos() {
        return todos;
    }
}
