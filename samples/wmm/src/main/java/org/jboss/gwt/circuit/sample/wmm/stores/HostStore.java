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

import java.util.HashSet;
import java.util.Set;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.StoreCallback;
import org.jboss.gwt.circuit.sample.wmm.actions.StartServerAction;
import org.jboss.gwt.circuit.sample.wmm.actions.StopServerAction;

public class HostStore {

    private final Set<String> runningServers = new HashSet<>();

    public HostStore(final Dispatcher dispatcher) {

        dispatcher.register(HostStore.class, new StoreCallback() {
            @Override
            public Agreement voteFor(final Action action) {
                Agreement agreement = Agreement.NONE;
                if (action instanceof StartServerAction) {
                    agreement = new Agreement(true);
                } else if (action instanceof StopServerAction) {
                    agreement = new Agreement(true, DeploymentStore.class);
                }
                return agreement;
            }

            @Override
            public void execute(final Action action, final Dispatcher.Channel channel) {
                if (action instanceof StartServerAction) {
                    StartServerAction start = (StartServerAction) action;
                    runningServers.add(start.getPayload());

                } else if (action instanceof StopServerAction) {
                    StopServerAction stop = (StopServerAction) action;
                    runningServers.remove(stop.getPayload());
                }
                channel.ack();
            }
        });
    }

    public Set<String> getRunningServers() {
        return runningServers;
    }
}
