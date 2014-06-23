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
package org.jboss.gwt.circuit.processor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ReceiveInfo {

    private final String method;
    private final String payload;
    private final String action;
    private final Set<String> dependencies;

    public ReceiveInfo(final String method, final String payload) {
        this.method = method;
        this.payload = payload;
        this.action = GenerationUtil.actionImplementation(payload);
        this.dependencies = new HashSet<>();
    }

    public void addDependency(String storeClassName) {
        dependencies.add(storeClassName);
    }

    public String getMethod() {
        return method;
    }

    public String getPayload() {
        return payload;
    }

    public String getAction() {
        return action;
    }

    public boolean hasDependencies() {
        return !dependencies.isEmpty();
    }

    public String getDependencies() {
        StringBuilder csv = new StringBuilder();
        for (Iterator<String> iterator = dependencies.iterator(); iterator.hasNext(); ) {
            String dependency = iterator.next();
            csv.append(dependency);
            if (iterator.hasNext()) {
                csv.append(", ");
            }
        }
        return csv.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ReceiveInfo)) { return false; }

        ReceiveInfo that = (ReceiveInfo) o;

        if (!method.equals(that.method)) { return false; }
        if (!payload.equals(that.payload)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + payload.hashCode();
        return result;
    }
}
