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
package org.jboss.gwt.flux.domain;

import java.util.HashMap;
import java.util.Map;

import com.google.web.bindery.event.shared.HandlerRegistration;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Agreement;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;
import org.jboss.gwt.flux.StoreChangedEvent;

/**
 * @author Harald Pehl
 */
public class HostStore implements Store {

    public final Map<String, ServerInstance> serverInstances = new HashMap<>();

    public HostStore(final Dispatcher dispatcher) {

        dispatcher.register(HostStore.class, new Callback() {
            @Override
            public Agreement voteFor(final Action action) {
                Agreement agreement;
                switch (action.getType()) {
                    case Actions.START_SERVER:
                        agreement = new Agreement(true);
                        break;

                    case Actions.STOP_SERVER:
                        agreement = new Agreement(true, DeploymentStore.class);
                        break;

                    default:
                        agreement = Agreement.NONE;
                }
                return agreement;
            }

            @Override
            public void execute(final Action action, final Dispatcher.Channel channel) {
                switch (action.getType()) {
                    case Actions.START_SERVER: {
                        String serverName = action.getPayload();
                        serverInstances.put(serverName, new ServerInstance(serverName));
                        break;
                    }

                    case Actions.STOP_SERVER: {
                        String serverName = action.getPayload();
                        serverInstances.remove(serverName);
                        break;
                    }
                }
                channel.ack();
            }
        });
    }

    @Override
    public HandlerRegistration addChangedHandler(final StoreChangedEvent.StoreChangedHandler handler) {
        return null;
    }
}
