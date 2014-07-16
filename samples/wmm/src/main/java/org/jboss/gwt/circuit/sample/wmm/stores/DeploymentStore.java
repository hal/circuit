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
package org.jboss.gwt.circuit.sample.wmm.stores;

import java.util.Iterator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.StoreCallback;
import org.jboss.gwt.circuit.sample.wmm.actions.DeployAction;
import org.jboss.gwt.circuit.sample.wmm.actions.Deployment;
import org.jboss.gwt.circuit.sample.wmm.actions.StopServerAction;
import org.jboss.gwt.circuit.sample.wmm.actions.UndeployAction;

public class DeploymentStore {

    // deployment -> server instances
    private final Multimap<String, String> deployments;

    public DeploymentStore(final Dispatcher dispatcher) {
        deployments = HashMultimap.create();

        dispatcher.register(DeploymentStore.class, new StoreCallback() {
            @Override
            public Agreement voteFor(final Action action) {
                Agreement agreement = Agreement.NONE;
                if (action instanceof DeployAction || action instanceof UndeployAction || action instanceof StopServerAction) {
                    agreement = new Agreement(true);
                }
                return agreement;
            }

            @Override
            public void complete(final Action action, final Dispatcher.Channel channel) {
                if (action instanceof StopServerAction) {
                    String serverToStop = ((StopServerAction) action).getPayload();
                    for (Iterator<String> iterator = deployments.values().iterator(); iterator.hasNext(); ) {
                        String server = iterator.next();
                        if (server.equals(serverToStop)) {
                            iterator.remove();
                        }
                    }
                }
                else if (action instanceof DeployAction) {
                    DeployAction deployAction = (DeployAction) action;
                    Deployment deployment = deployAction.getPayload();
                    deployments.put(deployment.getName(), deployment.getServer());
                } else if (action instanceof UndeployAction) {
                    UndeployAction undeployAction = (UndeployAction) action;
                    Deployment deployment = undeployAction.getPayload();
                    deployments.remove(deployment.getName(), deployment.getServer());
                }
                channel.ack();
            }
        });
    }

    public Multimap<String, String> getDeployments() {
        return deployments;
    }
}
