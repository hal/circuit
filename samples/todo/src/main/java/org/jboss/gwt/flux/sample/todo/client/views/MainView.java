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
package org.jboss.gwt.flux.sample.todo.client.views;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.StoreChangedEvent;
import org.jboss.gwt.flux.sample.todo.client.TodoStore;
import org.jboss.gwt.flux.sample.todo.resources.TodoResources;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

@SuppressWarnings("Convert2Lambda")
public class MainView extends Composite {

    private final FlowPanel root;
    private final Dispatcher dispatcher;
    private final TodoStore store;
    private final TodoResources resources;

    @Inject
    public MainView(final Dispatcher dispatcher, final TodoStore store, final TodoResources resources) {
        this.dispatcher = dispatcher;
        this.store = store;
        this.resources = resources;

        this.root = new FlowPanel();
        initWidget(root);
        setStyleName(resources.css().mainView());

        store.addChangedHandler(new StoreChangedEvent.StoreChangedHandler() {
            @Override
            public void onChange(final StoreChangedEvent event) {
                showTodos(store.getTodos());
            }
        });
    }

    private void showTodos(final List<Todo> todos) {
        root.clear();
        for (Todo todo : todos) {
            root.add(new TodoView(dispatcher, store, resources, todo));
        }
    }
}
