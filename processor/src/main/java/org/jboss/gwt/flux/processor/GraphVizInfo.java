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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphVizInfo {
    private final String payload;
    private final Set<String> stores;
    private final List<String[]> dependencies;

    public GraphVizInfo(final String payload) {
        this.payload = payload;
        this.stores = new HashSet<>();
        this.dependencies = new ArrayList<>();
    }

    public void addStore(final String store) {
        this.stores.add(store);
    }

    public void addDependency(final String source, final String sink) {
        this.dependencies.add(new String[]{source, sink});
    }

    public String getPayload() {
        return payload;
    }

    public Set<String> getStores() {
        return stores;
    }

    public List<String[]> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof GraphVizInfo)) { return false; }

        GraphVizInfo that = (GraphVizInfo) o;

        if (!payload.equals(that.payload)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return payload.hashCode();
    }
}
