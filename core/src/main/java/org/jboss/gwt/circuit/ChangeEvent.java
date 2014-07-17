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
package org.jboss.gwt.circuit;

public class ChangeEvent {

    private final Class<?> store;
    private final Class<?> actionType;

    ChangeEvent(final Class<?> store, final Class<?> actionType) {
        this.store = store;
        this.actionType = actionType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ChangeEvent)) { return false; }

        ChangeEvent that = (ChangeEvent) o;

        if (!actionType.equals(that.actionType)) { return false; }
        if (!store.equals(that.store)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = store.hashCode();
        result = 31 * result + actionType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChangedEvent(" + store + ", " + actionType + ')';
    }
}
