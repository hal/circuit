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
package org.jboss.gwt.circuit.sample.todo.client.actions;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.meta.ActionType;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

@ActionType
public class RemoveTodo implements Action<Todo> {

    private final Todo todo;

    public RemoveTodo(final Todo todo) {this.todo = todo;}

    @Override
    public Todo getPayload() {
        return todo;
    }

    @Deprecated
    public Todo getTodo() {
        return todo;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof RemoveTodo)) { return false; }

        RemoveTodo that = (RemoveTodo) o;

        if (!todo.equals(that.todo)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return todo.hashCode();
    }

    @Override
    public String toString() {
        return "RemoveTodo(" + todo + ")";
    }
}
