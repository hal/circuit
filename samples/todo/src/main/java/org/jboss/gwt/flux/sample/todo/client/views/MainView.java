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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.StoreChangedEvent;
import org.jboss.gwt.flux.sample.todo.client.TodoStore;
import org.jboss.gwt.flux.sample.todo.client.actions.TodoAction;
import org.jboss.gwt.flux.sample.todo.resources.TodoResources;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

import java.util.List;

import static org.jboss.gwt.flux.sample.todo.client.actions.TodoActions.ADD;

@SuppressWarnings("Convert2Lambda")
public class MainView extends Composite {

    private final FlowPanel root;
    private final Dispatcher dispatcher;
    private final TodoStore store;
    private final TodoResources resources;

    private VerticalPanel todoPanel;

    @Inject
    public MainView(final Dispatcher dispatcher, final TodoStore store, final TodoResources resources) {
        this.dispatcher = dispatcher;
        this.store = store;
        this.resources = resources;

        this.todoPanel = new VerticalPanel();
        this.root = new FlowPanel();

        root.add(new Button("Add TODO", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dispatcher.dispatch(new TodoAction(ADD, new Todo("Time is "+ System.currentTimeMillis())));
            }
        }));

        root.add(todoPanel);

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
        todoPanel.clear();
        for (Todo todo : todos) {
            todoPanel.add(new TodoView(dispatcher, store, resources, todo));
        }
    }
}
