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
package org.jboss.gwt.circuit.sample.todo.client.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.gwt.circuit.ChangeManagement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.dag.DAGDispatcher;
import org.jboss.gwt.circuit.sample.todo.client.TodoService;
import org.jboss.gwt.circuit.sample.todo.client.TodoServiceAsync;
import org.jboss.gwt.circuit.sample.todo.resources.TodoResources;

@ApplicationScoped
@SuppressWarnings("UnusedDeclaration")
public class Producer {

    @Produces
    @ApplicationScoped
    TodoServiceAsync produceTodoService() {
        return GWT.create(TodoService.class);
    }

    @Produces
    @ApplicationScoped
    public TodoResources produceTodoResources() {
        return GWT.create(TodoResources.class);
    }

    @Produces
    @ApplicationScoped
    public EventBus produceEventBus() {
        return new SimpleEventBus();
    }

    @Produces
    @ApplicationScoped
    public Dispatcher produceDispatcher() {
        return new DAGDispatcher();
    }

    @Produces
    @ApplicationScoped
    public ChangeManagement produceChangeManagement(EventBus eventBus) {
        return new ChangeManagement(eventBus);
    }
}

