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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.gwt.flux.AbstractStore;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Agreement;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.sample.todo.client.actions.ListTodos;
import org.jboss.gwt.flux.sample.todo.client.actions.RemoveTodo;
import org.jboss.gwt.flux.sample.todo.client.actions.SaveTodo;
import org.jboss.gwt.flux.sample.todo.client.actions.TodoActions;
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

        dispatcher.register(TodoStore.class, new Callback() {
            @Override
            public Agreement voteFor(final Action action) {
                if (action instanceof TodoActions) {
                    return new Agreement(true);
                }
                return Agreement.NONE;
            }

            @Override
            public void execute(final Action action, final Dispatcher.Channel channel) {
                process(action, channel);
            }
        });
    }

    private void process(final Action action, final Dispatcher.Channel channel) {
        if (action instanceof ListTodos) {
            todoService.list(new TodoCallback<Collection<Todo>>() {
                @Override
                public void onSuccess(final Collection<Todo> result) {
                    todos.clear();
                    todos.addAll(result);
                    channel.ack();
                    fireChanged();
                }
            });
        } else if (action instanceof SaveTodo) {
            todoService.save((Todo) action.getPayload(), new TodoCallback<Void>() {
                @Override
                public void onSuccess(final Void result) {
                    process(new ListTodos(), channel);
                }
            });
        } else if (action instanceof RemoveTodo) {
            todoService.delete((Todo) action.getPayload(), new TodoCallback<Void>() {
                @Override
                public void onSuccess(final Void result) {
                    process(new ListTodos(), channel);
                }
            });
        }
    }

    public List<Todo> getTodos() {
        return todos;
    }
}
