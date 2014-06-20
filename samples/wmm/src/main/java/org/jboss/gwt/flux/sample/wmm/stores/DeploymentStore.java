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
package org.jboss.gwt.flux.sample.wmm.stores;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.gwt.flux.AbstractStore;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Agreement;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.sample.wmm.actions.DeployAction;
import org.jboss.gwt.flux.sample.wmm.actions.DeploymentAction;
import org.jboss.gwt.flux.sample.wmm.actions.StopServerAction;
import org.jboss.gwt.flux.sample.wmm.actions.UndeployAction;

public class DeploymentStore extends AbstractStore {

    // deployment -> server instances
    public final Map<String, List<String>> deployments;

    public DeploymentStore(final Dispatcher dispatcher) {
        deployments = new HashMap<>();

        dispatcher.register(DeploymentStore.class, new Callback() {
            @Override
            public Agreement voteFor(final Action action) {
                Agreement agreement = Agreement.NONE;
                if (action instanceof DeploymentAction || action instanceof StopServerAction) {
                    agreement = new Agreement(true);
                }
                return agreement;
            }

            @Override
            public void execute(final Action action, final Dispatcher.Channel channel) {
                if (action instanceof DeployAction) {
                    DeployAction deployAction = (DeployAction) action;
                    List<String> servers = deployments.get(deployAction.getDeployment());
                    if (servers == null) {
                        servers = new LinkedList<>();
                        deployments.put(deployAction.getDeployment(), servers);
                    }
                    servers.add(deployAction.getServer());
                } else if (action instanceof UndeployAction) {
                    UndeployAction undeployAction = (UndeployAction) action;
                    List<String> servers = deployments.get(undeployAction.getDeployment());
                    servers.remove(undeployAction.getServer());
                }
                channel.ack();
            }
        });
    }
}
