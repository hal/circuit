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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.sample.todo.client.TodoStore;
import org.jboss.gwt.flux.sample.todo.client.actions.TodoAction;
import org.jboss.gwt.flux.sample.todo.resources.TodoResources;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

import static org.jboss.gwt.flux.sample.todo.client.actions.TodoActions.ADD;

public class TodoView extends Composite {

    private final Dispatcher dispatcher;
    private final TodoStore store;
    private final TodoResources resources;
    private final Todo todo;
    private final DeckPanel root;
    private final Label label;

    public TodoView(final Dispatcher dispatcher, final TodoStore store, final TodoResources resources, final Todo todo) {
        this.dispatcher = dispatcher;
        this.store = store;
        this.resources = resources;
        this.todo = todo;

        this.root = new DeckPanel();
        this.label = new Label(todo.getName());
        root.add(label);

        readonly();
        initWidget(root);
        setStyleName(resources.css().todoView());


    }

    public void readonly() {
        root.showWidget(0);
    }
}
