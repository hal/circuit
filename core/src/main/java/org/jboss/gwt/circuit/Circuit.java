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

import com.google.web.bindery.event.shared.EventBus;
import org.jboss.gwt.circuit.dag.DAGDispatcher;

/**
 * Entrypoint to the Circuit framework.
 * TODO How to add / register stores and callbacks?
 */
public class Circuit {

    public static final class Builder {

        private Dispatcher dispatcher;
        private ChangeManagement changeManagement;

        private Builder() {
            dispatcher = new DAGDispatcher();
            changeManagement = new ChangeManagement();
        }

        public Builder setDispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public Builder setEventBus(EventBus eventBus) {
            changeManagement = new ChangeManagement(eventBus);
            return this;
        }

        public Circuit build() {
            return new Circuit(this);
        }
    }


    /**
     * @return A builder that can be used to create a Circuit instance
     */
    public static Builder builder() {
        return new Builder();
    }


    private Dispatcher dispatcher;
    private ChangeManagement changeManagement;

    private Circuit(Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.changeManagement = builder.changeManagement;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public ChangeManagement getChangeManagement() {
        return changeManagement;
    }

    public void start() {
        // TODO wiring
    }

    public void stop() {
        // TODO un-wiring?
    }
}
