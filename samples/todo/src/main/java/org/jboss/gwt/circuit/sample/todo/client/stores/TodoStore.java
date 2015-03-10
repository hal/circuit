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
package org.jboss.gwt.circuit.sample.todo.client.stores;

import org.jboss.gwt.circuit.ChangeSupport;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.meta.Process;
import org.jboss.gwt.circuit.meta.Store;
import org.jboss.gwt.circuit.sample.todo.client.TodoServiceAsync;
import org.jboss.gwt.circuit.sample.todo.client.actions.*;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Store
@ApplicationScoped
public class TodoStore extends ChangeSupport {

    private Todo selectedTodo;
    private final List<Todo> todos;
    private final TodoServiceAsync todoService;
    private final UserStore userStore;

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    public TodoStore(final TodoServiceAsync todoService, UserStore userStore) {
        this.todos = new LinkedList<>();
        this.todoService = todoService;
        this.userStore = userStore;
    }


    // ------------------------------------------------------ user actions

    @Process(actionType = SelectUser.class, dependencies = UserStore.class)
    public void onSelectUser(SelectUser action, final Dispatcher.Channel channel) {
        // reset selection
        selectedTodo = null;

        channel.ack();
    }

    @Process(actionType = RemoveUser.class, dependencies = UserStore.class)
    public void onRemoveUser(RemoveUser action, final Dispatcher.Channel channel) {

        todoService.removeForUser(action.getUser(), new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(Void v) {
                onList(channel);
            }
        });
    }


    // ------------------------------------------------------ _todo_ actions

    @Process(actionType = ListTodos.class)
    public void onList(final Dispatcher.Channel channel) {
        todoService.list(new TodoCallback<Collection<Todo>>(channel) {
            @Override
            public void onSuccess(final Collection<Todo> result) {
                todos.clear();
                todos.addAll(result);

                channel.ack();
            }
        });
    }

    @Process(actionType = ResolveTodo.class)
    public void onResolve(ResolveTodo action, final Dispatcher.Channel channel) {
        todoService.save(action.getTodo(), new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(final Void result) {
                onList(channel);
            }
        });
    }

    @Process(actionType = SelectTodo.class)
    public void onSelect(final SelectTodo action, final Dispatcher.Channel channel) {
        this.selectedTodo = action.getTodo();

        channel.ack();
    }

    @Process(actionType = SaveTodo.class)
    public void onStore(final SaveTodo action, final Dispatcher.Channel channel) {

        String assignee = userStore.getSelectedUser() != null ? userStore.getSelectedUser() : Todo.USER_ANY;
        action.getTodo().setUser(assignee);

        todoService.save(action.getTodo(), new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(final Void result) {
                onList(channel);
            }
        });
    }

    @Process(actionType = RemoveTodo.class)
    public void onRemove(final RemoveTodo action, final Dispatcher.Channel channel) {

        todoService.delete(action.getTodo(), new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(final Void result) {
                if (action.getTodo().equals(selectedTodo)) { selectedTodo = null; }
                onList(channel);
            }
        });
    }


    // ------------------------------------------------------ state

    public List<Todo> getTodos() {

        List<Todo> filtered = new ArrayList<>();
        // apply selectedUser
        String selectedUser = userStore.getSelectedUser();
        for (Todo todo : this.todos) {
            if (selectedUser == null || selectedUser.equals(todo.getUser())) { filtered.add(todo); }

        }

        return filtered;
    }

    public Todo getSelectedTodo() {
        return selectedTodo;
    }
}
