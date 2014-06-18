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
public class DeploymentStore implements Store {

    public final Map<String, Deployment> deployments;

    public DeploymentStore(final Dispatcher dispatcher) {
        deployments = new HashMap<>();

        dispatcher.register(DeploymentStore.class, new Callback() {
            @Override
            public Agreement voteFor(final Action action) {
                Agreement agreement;
                switch (action.getType()) {
                    case Actions.DEPLOY:
                    case Actions.UNDEPLOY:
                    case Actions.STOP_SERVER:
                        agreement = new Agreement(true);
                        break;

                    default:
                        agreement = Agreement.NONE;

                }
                return agreement;
            }

            @Override
            public void execute(final Action action, final Dispatcher.Channel channel) {
                switch (action.getType()) {
                    case Actions.DEPLOY: {
                        String[] payload = action.getPayload();
                        String deploymentName = payload[0];
                        String serverInstance = payload[1];
                        if (!deployments.keySet().contains(deploymentName)) {
                            Deployment dpl = new Deployment(deploymentName);
                            deployments.put(deploymentName, dpl);
                        }

                        deployments.get(deploymentName).deployedAt.add(serverInstance);
                        break;
                    }

                    case Actions.UNDEPLOY: {
                        String[] payload = action.getPayload();
                        String deploymentName = payload[0];
                        String serverInstance = payload[1];
                        deployments.get(deploymentName).deployedAt.remove(serverInstance);
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
