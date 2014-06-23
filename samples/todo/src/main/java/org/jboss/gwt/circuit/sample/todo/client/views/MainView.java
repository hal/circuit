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
package org.jboss.gwt.circuit.sample.todo.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.gwt.circuit.StoreChangedEvent;
import org.jboss.gwt.circuit.sample.todo.client.TodoDispatcher;
import org.jboss.gwt.circuit.sample.todo.client.actions.SaveTodo;
import org.jboss.gwt.circuit.sample.todo.client.stores.TodoStore;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@Templated("View.html#main")
public class MainView extends Composite {

    @Inject TodoStore store;
    @Inject TodoDispatcher dispatcher;
    @Inject EventBus eventBus;

    @Inject @DataField FlowPanel todosContainer;
    @Inject Instance<TodoView> todoViewFactory;
    @Inject @DataField InlineHyperlink add;


    @PostConstruct
    public void init() {
        eventBus.addHandler(StoreChangedEvent.TYPE, new StoreChangedEvent.StoreChangedHandler() {
            @Override
            public void onChange(final StoreChangedEvent event) {
                showTodos(store.getTodos());
            }
        });
    }

    void showTodos(final List<Todo> todos) {
        todosContainer.clear();
        for (Todo todo : todos) {
            TodoView todoView = todoViewFactory.get();
            todoView.refresh(todo);
            todosContainer.add(todoView);
        }
    }

    @EventHandler("add")
    public void onAdd(ClickEvent event) {
        dispatcher.dispatch(new SaveTodo(new Todo("New todo @ " + System.currentTimeMillis())));
    }
}
