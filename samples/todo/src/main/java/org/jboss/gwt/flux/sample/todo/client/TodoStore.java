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

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.gwt.flux.AbstractStore;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.sample.todo.client.actions.TodoActions;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.jboss.gwt.flux.sample.todo.client.actions.TodoActions.LIST;

@SuppressWarnings("Convert2Lambda")
public class TodoStore extends AbstractStore {

    abstract class TodoCallback<T> implements AsyncCallback<T> {

        @Override
        public void onFailure(final Throwable caught) {
            // noop
        }
    }


    private final List<Todo> todos;
    private final TodoServiceAsync todoService;

    @Inject
    public TodoStore(final Dispatcher dispatcher, final TodoServiceAsync todoService) {
        this.todos = new LinkedList<>();
        this.todoService = todoService;

        dispatcher.register(
                new Callback<TodoActions, Todo>() {

                    @Override
                    public TodoActions[] getTypes() {
                        return TodoActions.values();
                    }

                    @Override
                    public void execute(final Action<TodoActions, Todo> action, final Dispatcher.Context context) {

                        // artifical delay
                        Timer t = new Timer() {
                            @Override
                            public void run() {
                                process(action.getType(), action.getPayload(), context);
                            }
                        };

                        t.schedule((Random.nextInt( 3 ) + 1)*1000);
                    }
                });
    }

    private void process(final TodoActions type, final Todo payload, final Dispatcher.Context context) {

        switch (type) {

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

            case ADD:
                todoService.save(payload, new TodoCallback<Void>() {
                    @Override
                    public void onSuccess(final Void result) {
                        process(LIST, null, context);
                    }
                });
                break;
            case REMOVE:
                todoService.delete(payload, new TodoCallback<Void>() {
                    @Override
                    public void onSuccess(final Void result) {
                        process(LIST, null, context);
                    }
                });
                break;

        }
    }

    public List<Todo> getTodos() {
        return todos;
    }
}
