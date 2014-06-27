package org.jboss.gwt.circuit.sample.todo.client.stores;

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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.gwt.circuit.ChangeSupport;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.meta.Process;
import org.jboss.gwt.circuit.meta.Store;
import org.jboss.gwt.circuit.sample.todo.client.actions.AddUser;
import org.jboss.gwt.circuit.sample.todo.client.actions.LoadUsers;
import org.jboss.gwt.circuit.sample.todo.client.actions.RemoveUser;
import org.jboss.gwt.circuit.sample.todo.client.actions.SelectUser;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

@Store
@ApplicationScoped
@SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
public class UserStore extends ChangeSupport {

    private final List<String> users;
    private String selectedUser;

    @Inject
    private Dispatcher dispatcher;

    public UserStore() {
        this.users = new LinkedList<>();
    }

    @Process(actionType = LoadUsers.class)
    public void onLoad(final Dispatcher.Channel channel) {

        this.users.add(Todo.USER_ANY);
        this.users.add("Peter");
        this.users.add("Paul");
        this.users.add("Mary");

        channel.ack();
        fireChanged(UserStore.class);
    }


    @Process(actionType = SelectUser.class)
    public void onSelect(String user, final Dispatcher.Channel channel) {
        this.selectedUser = user;
        channel.ack();
        fireChanged(UserStore.class);
    }

    @Process(actionType = AddUser.class)
    public void onAdd(String user, final Dispatcher.Channel channel) {
        if(!users.contains(user))
            this.users.add(user);
        channel.ack();
        fireChanged(UserStore.class);
    }

    @Process(actionType = RemoveUser.class)
    public void onRemove(String user, final Dispatcher.Channel channel) {

        if(Todo.USER_ANY.equals(user))
            return;

        this.users.remove(user);

        // update selection if necessary
        if(user.equals(selectedUser))
            dispatcher.dispatch(new SelectUser(Todo.USER_ANY));

        channel.ack();
        fireChanged(UserStore.class);
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public List<String> getUsers() {
        return Collections.unmodifiableList(users);
    }
}
