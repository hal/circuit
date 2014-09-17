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

import java.util.*;

public class ProcessInfo {

    private final String method;
    private final String actionType;
    private final List<String> payload;
    private Set<String> dependencies;

    public ProcessInfo(final String method, String actionType) {
        this.method = method;
        this.actionType = actionType;
        this.payload = new ArrayList<>();
        this.dependencies = new HashSet<>();
    }

    public boolean isSingleArg() {
        return payload.isEmpty();
    }

    public String getMethod() {
        return method;
    }

    public String getActionType() {
        return actionType;
    }

    public void addPayload(String name) {
        payload.add(name);
    }

    public List<String> getPayload() {
        return payload;
    }

    public void addDependency(String storeClassName) {
        dependencies.add(storeClassName);
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
        if (!(o instanceof ProcessInfo)) { return false; }

        ProcessInfo that = (ProcessInfo) o;

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
