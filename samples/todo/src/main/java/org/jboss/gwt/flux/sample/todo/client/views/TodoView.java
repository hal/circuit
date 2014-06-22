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

import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.gwt.flux.sample.todo.client.TodoDispatcher;
import org.jboss.gwt.flux.sample.todo.client.actions.RemoveTodo;
import org.jboss.gwt.flux.sample.todo.client.actions.SaveTodo;
import org.jboss.gwt.flux.sample.todo.client.stores.TodoStore;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

@SuppressWarnings("UnusedDeclaration")
@Templated("View.html#todoContainer")
public class TodoView extends Composite {

    private Todo todo;

    @Inject TodoStore store;
    @Inject TodoDispatcher dispatcher;
    @DataField Element check = DOM.createSpan();
    @Inject @DataField InlineLabel name;
    @DataField Element remove = DOM.createElement("i");

    void refresh(Todo todo) {
        this.todo = todo;
        name.setText(todo.getName());
        if (todo.isDone()) {
            check.removeClassName("todo-check");
            check.addClassName("todo-uncheck");
            name.addStyleName("todo-done");
        } else {
            check.removeClassName("todo-uncheck");
            check.addClassName("todo-check");
            name.removeStyleName("todo-done");
        }
    }

    @EventHandler("check")
    public void onCheck(ClickEvent event) {
        todo.setDone(true);
        dispatcher.dispatch(new SaveTodo(todo));
    }

    @EventHandler("remove")
    public void onRemove(ClickEvent event) {
        dispatcher.dispatch(new RemoveTodo(todo));
    }
}
