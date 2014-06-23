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
package org.jboss.gwt.flux.processor;

public class ActionInfo {
    private final String payload;
    private final String action;

    public ActionInfo(final String payload, final String action) {
        this.payload = payload;
        this.action = action;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ActionInfo)) { return false; }

        ActionInfo that = (ActionInfo) o;

        if (!action.equals(that.action)) { return false; }
        if (!payload.equals(that.payload)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = payload.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }

    public String getPayload() {
        return payload;
    }

    public String getAction() {
        return action;
    }
}
