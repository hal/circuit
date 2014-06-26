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
package org.jboss.gwt.circuit.sample.todo.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.sample.todo.client.actions.ListTodos;
import org.jboss.gwt.circuit.sample.todo.client.actions.LoadUsers;
import org.jboss.gwt.circuit.sample.todo.client.views.DiagnosticsView;
import org.jboss.gwt.circuit.sample.todo.client.views.TodoView;
import org.jboss.gwt.circuit.sample.todo.client.views.UserView;
import org.jboss.gwt.circuit.sample.todo.resources.TodoResources;

import javax.inject.Inject;

@EntryPoint
@SuppressWarnings("UnusedDeclaration")
public class App {

    @Inject
    Dispatcher dispatcher;

    @Inject
    TodoResources resources;

    @Inject
    TodoView todoView;

    @Inject
    UserView userView;

    @Inject
    DiagnosticsView diagnosticsView;

    @AfterInitialization
    public void init() {
        resources.css().ensureInjected();
        dispatcher.addDiagnostics(diagnosticsView);

        DockLayoutPanel layout = new DockLayoutPanel(Style.Unit.PX);
        layout.getElement().setAttribute("style", "width:100%; height:100%");

        TabPanel tabs = new TabPanel();
        tabs.getElement().setId("app-container");
        tabs.getElement().setAttribute("style", "margin:30px;width:100%; height:100%");

        tabs.add(todoView, "Todo List");
        tabs.add(userView, "User Management");

        tabs.selectTab(0);

        layout.addSouth(diagnosticsView, 50);
        layout.add(tabs);

        RootPanel.get().add(layout);

        // application init
        dispatcher.dispatch(new LoadUsers());
        dispatcher.dispatch(new ListTodos());
    }
}
