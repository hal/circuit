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

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.StoreChangedEvent;
import org.jboss.gwt.flux.sample.todo.client.TodoStore;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

@Templated
@Page(role = DefaultPage.class)
public class MainView extends Composite {

    @Inject Dispatcher dispatcher;
    @Inject TodoStore store;
    @Inject @DataField FlowPanel todoContainer;
    @Inject Instance<TodoView> todoItem;

    @PostConstruct
    public void init() {
        store.addChangedHandler(new StoreChangedEvent.StoreChangedHandler() {
            @Override
            public void onChange(final StoreChangedEvent event) {
                showTodos(store.getTodos());
            }
        });
    }

    void showTodos(final List<Todo> todos) {
        todoContainer.clear();
        for (Todo todo : todos) {
            TodoView todoView = todoItem.get();
            todoView.refresh(todo);
            todoContainer.add(todoView);
        }
    }
}
