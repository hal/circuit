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
package org.jboss.gwt.circuit.sample.todo.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.gwt.circuit.sample.todo.client.TodoService;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

public class TodoServiceImpl extends RemoteServiceServlet implements TodoService {

    private static final int TIMEOUT = 1000;
    private final Random random;
    private final Map<String, Todo> todos;

    public TodoServiceImpl() {
        random = new Random();
        todos = new LinkedHashMap<>();
        addInitialTodos();
    }

    private void addInitialTodos() {
        Todo[] todos = new Todo[]{
                new Todo("Buy milk", "Peter"), new Todo("Invent the next big thing"), new Todo("Relax!","Mary")
        };
        for (Todo todo : todos) {
            this.todos.put(todo.getId(), todo);
        }
    }

    @Override
    public Collection<Todo> list() {
        oneMomentPlease();
        return new ArrayList<>(todos.values());
    }

    @Override
    public void save(final Todo todo) {
        oneMomentPlease();
        Todo existingTodo = todos.get(todo.getId());
        if (existingTodo != null) {
            existingTodo.setName(todo.getName());
            existingTodo.setDone(todo.isDone());
        } else {
            todos.put(todo.getId(), todo);
        }
    }

    @Override
    public void delete(final Todo todo) {
        oneMomentPlease();
        todos.remove(todo.getId());
    }

    @Override
    public void removeForUser(String user) {
        Map<String, Todo> filtered = new HashMap<>();

        for(String key : this.todos.keySet())
        {
            Todo todo = this.todos.get(key);
            if(!user.equals(todo.getUser()))
                filtered.put(key, todo);
        }
        this.todos.clear();
        this.todos.putAll(filtered);
    }

    private void oneMomentPlease() {
        long timeout = 100l + random.nextInt(TIMEOUT);
        try {
            Thread.sleep(timeout);
        } catch (Throwable t) {
            // noop
        }
    }
}
