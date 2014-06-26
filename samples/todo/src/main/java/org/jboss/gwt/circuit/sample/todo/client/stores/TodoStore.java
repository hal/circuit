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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.gwt.circuit.AbstractStore;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.meta.*;
import org.jboss.gwt.circuit.meta.Process;
import org.jboss.gwt.circuit.sample.todo.client.TodoServiceAsync;
import org.jboss.gwt.circuit.sample.todo.client.actions.ListTodos;
import org.jboss.gwt.circuit.sample.todo.client.actions.RemoveTodo;
import org.jboss.gwt.circuit.sample.todo.client.actions.RemoveUser;
import org.jboss.gwt.circuit.sample.todo.client.actions.ResolveTodo;
import org.jboss.gwt.circuit.sample.todo.client.actions.SaveTodo;
import org.jboss.gwt.circuit.sample.todo.client.actions.SelectTodo;
import org.jboss.gwt.circuit.sample.todo.client.actions.SelectUser;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

@Store
@ApplicationScoped
@SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
public class TodoStore extends AbstractStore {

    private String selectedUser;
    private Todo selectedTodo;

    abstract class TodoCallback<T> implements AsyncCallback<T> {

        private final Dispatcher.Channel channel;

        public TodoCallback(final Dispatcher.Channel channel) {
            this.channel = channel;
        }

        @Override
        public void onFailure(final Throwable caught) {
            channel.nack(caught);
        }
    }

    private final List<Todo> todos;
    private final TodoServiceAsync todoService;
    private final EventBus eventBus;

    @Inject
    public TodoStore(final TodoServiceAsync todoService, EventBus eventBus) {
        this.todos = new LinkedList<>();
        this.todoService = todoService;
        this.eventBus = eventBus;
    }

    @Process(actionType = SelectUser.class, dependencies = {UserStore.class})
    public void onSelectUser(String user, final Dispatcher.Channel channel) {

        if(user.equals(Todo.USER_ANY))
            this.selectedUser = null;
        else
            this.selectedUser = user;

        // reset selection
        selectedTodo=null;

        channel.ack();
        fireChanged(TodoStore.class);
    }

    @Process(actionType = RemoveUser.class, dependencies = {UserStore.class})
    public void onRemoveUser(String user, final Dispatcher.Channel channel) {

        todoService.removeForUser(user, new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(Void v) {
                onList(channel);
            }
        });
    }

    @Process(actionType = ListTodos.class)
    public void onList(final Dispatcher.Channel channel) {
        todoService.list(new TodoCallback<Collection<Todo>>(channel) {
            @Override
            public void onSuccess(final Collection<Todo> result) {
                todos.clear();
                todos.addAll(result);
                channel.ack();
                fireChanged(TodoStore.class);
            }
        });
    }

    @Process(actionType = ResolveTodo.class)
    public void onResolve(Todo todo, final Dispatcher.Channel channel) {
        todoService.save(todo, new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(final Void result) {
                onList(channel);
            }
        });
    }

    @Process(actionType = SelectTodo.class)
    public void onSelect(final Todo todo, final Dispatcher.Channel channel) {
        this.selectedTodo = todo;
        channel.ack();
        fireChanged(TodoStore.class);
    }

    @Process(actionType = SaveTodo.class)
    public void onStore(final Todo todo, final Dispatcher.Channel channel) {

        String assignee = (selectedUser!=null) ? selectedUser : Todo.USER_ANY;
        todo.setUser(assignee);

        todoService.save(todo, new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(final Void result) {
                onList(channel);
            }
        });
    }

    @Process(actionType = RemoveTodo.class)
    public void onRemove(final Todo todo, final Dispatcher.Channel channel) {

        todoService.delete(todo, new TodoCallback<Void>(channel) {
            @Override
            public void onSuccess(final Void result) {

                if(todo.equals(selectedTodo))
                    selectedTodo = null;

                onList(channel);
            }

        });
    }

    public List<Todo> getTodos() {

        List<Todo> filtered = new ArrayList<>();
        // apply selectedUser
        for(Todo todo : this.todos)
        {
            if(selectedUser ==null || selectedUser.equals(todo.getUser()))
                filtered.add(todo);

        }

        return filtered;
    }

    public Todo getSelectedTodo() {
        return selectedTodo;
    }
}
