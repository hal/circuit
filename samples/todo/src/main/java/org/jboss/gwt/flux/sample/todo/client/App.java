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
package org.jboss.gwt.flux.sample.todo.client;

import static org.jboss.gwt.flux.sample.todo.client.actions.TodoActions.LIST;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.sample.todo.client.views.MainView;
import org.jboss.gwt.flux.sample.todo.client.views.QueueInfoView;
import org.jboss.gwt.flux.sample.todo.resources.TodoResources;

@EntryPoint
@SuppressWarnings("UnusedDeclaration")
public class App {

    @Inject TodoResources resources;
    @Inject MainView mainView;
    @Inject QueueInfoView queueInfoView;
    @Inject Dispatcher dispatcher;

    @AfterInitialization
    public void init() {
        resources.css().ensureInjected();
        RootPanel.get().add(mainView);
        RootPanel.get().add(queueInfoView);
        dispatcher.dispatch(new Action(LIST));
    }
}
