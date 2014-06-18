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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.impl.DAGDispatcher;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Harald Pehl
 */
public class DomainTest {

    private Dispatcher dispatcher;
    private DeploymentStore deploymentStore;
    private HostStore hostStore;

    @Before
    public void setUp() {
        dispatcher = new DAGDispatcher();

        deploymentStore = new DeploymentStore(dispatcher);
        hostStore = new HostStore(dispatcher);

        // Domain setup
        dispatcher.dispatch(new Action(Actions.START_SERVER, "server-1"));
        dispatcher.dispatch(new Action(Actions.START_SERVER, "server-2"));
    }

    @Test
    public void domainSetup() {
        assertTrue(hostStore.serverInstances.size() == 2);
        assertTrue(hostStore.serverInstances.containsKey("server-1"));
        assertTrue(hostStore.serverInstances.containsKey("server-2"));
    }

    @Test
    public void deployment() {
        dispatcher.dispatch(new Action(Actions.DEPLOY, new String[]{"foo.war", "server-1"}));
        dispatcher.dispatch(new Action(Actions.DEPLOY, new String[]{"bar.war", "server-1"}));

        assertTrue(deploymentStore.deployments.size() == 2);
        assertTrue(deploymentStore.deployments.containsKey("foo.war"));
    }

    @Test
    public void dependencies() {
        final Map<Action, List<Class<?>>> executionOrder = new HashMap<>();

        DAGDispatcher.DAGDiagnostics diagnostics = new DAGDispatcher.DAGDiagnostics() {
            @Override
            public void onDispatch(final Action a) {

            }

            @Override
            public void onLock() {

            }

            @Override
            public void onExecute(final Class<?> s, final Action a) {
                List<Class<?>> stores = executionOrder.get(a);
                if (stores == null) {
                    stores = new LinkedList<>();
                    executionOrder.put(a, stores);
                }
                stores.add(s);
            }

            @Override
            public void onAck(final Class<?> s, final Action a) {

            }

            @Override
            public void onNack(final Class<?> s, final Action a, final Throwable t) {

            }

            @Override
            public void onUnlock() {

            }
        };
        dispatcher.addDiagnostics(diagnostics);

        dispatcher.dispatch(new Action(Actions.DEPLOY, new String[]{"foo.war", "server-1"}));
        Action stopAction = new Action(Actions.STOP_SERVER, "server-1");
        dispatcher.dispatch(stopAction);

        List<Class<?>> stores = executionOrder.get(stopAction);
        assertEquals(2, stores.size());
        assertEquals(DeploymentStore.class, stores.get(0));
        assertEquals(HostStore.class, stores.get(1));
    }
}
