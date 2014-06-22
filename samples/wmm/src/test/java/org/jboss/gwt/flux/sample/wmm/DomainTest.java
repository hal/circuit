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
package org.jboss.gwt.flux.sample.wmm;

import static org.junit.Assert.*;

import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.dag.DAGDispatcher;
import org.jboss.gwt.flux.sample.wmm.actions.DeployAction;
import org.jboss.gwt.flux.sample.wmm.actions.Deployment;
import org.jboss.gwt.flux.sample.wmm.actions.StartServerAction;
import org.jboss.gwt.flux.sample.wmm.actions.StopServerAction;
import org.jboss.gwt.flux.sample.wmm.actions.UndeployAction;
import org.jboss.gwt.flux.sample.wmm.stores.DeploymentStore;
import org.jboss.gwt.flux.sample.wmm.stores.HostStore;
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
    }

    @Test
    public void startServer() {
        dispatchStartServers();

        assertEquals(2, hostStore.getRunningServers().size());
        assertTrue(hostStore.getRunningServers().contains("server-1"));
        assertTrue(hostStore.getRunningServers().contains("server-2"));
    }

    @Test
    public void deploy() {
        dispatchStartServers();
        dispatchDeploy();

        assertEquals(3, deploymentStore.getDeployments().size());
        assertEquals(1, deploymentStore.getDeployments().get("foo.jar").size());
        assertEquals(2, deploymentStore.getDeployments().get("bar.jar").size());

        assertEquals("server-1", deploymentStore.getDeployments().get("foo.jar").iterator().next());
        assertTrue(deploymentStore.getDeployments().get("bar.jar").contains("server-1"));
        assertTrue(deploymentStore.getDeployments().get("bar.jar").contains("server-2"));
    }

    @Test
    public void undeploy() {
        dispatchStartServers();
        dispatchDeploy();
        dispatcher.dispatch(new UndeployAction(new Deployment("foo.jar", "server-1")));

        assertEquals(2, deploymentStore.getDeployments().size());
        assertFalse(deploymentStore.getDeployments().containsKey("foo.jar"));
        assertEquals(2, deploymentStore.getDeployments().get("bar.jar").size());

        assertTrue(deploymentStore.getDeployments().get("bar.jar").contains("server-1"));
        assertTrue(deploymentStore.getDeployments().get("bar.jar").contains("server-2"));
    }

    @Test
    public void stopServer() {
        dispatchStartServers();
        dispatchDeploy();
        dispatcher.dispatch(new StopServerAction("server-1"));

        assertEquals(1, hostStore.getRunningServers().size());
        assertFalse(hostStore.getRunningServers().contains("server-1"));
        assertTrue(hostStore.getRunningServers().contains("server-2"));

        assertEquals(1, deploymentStore.getDeployments().size());
        assertFalse(deploymentStore.getDeployments().containsKey("foo.jar"));
        assertEquals(1, deploymentStore.getDeployments().get("bar.jar").size());
        assertTrue(deploymentStore.getDeployments().get("bar.jar").contains("server-2"));
    }

    private void dispatchStartServers() {
        dispatcher.dispatch(new StartServerAction("server-1"));
        dispatcher.dispatch(new StartServerAction("server-2"));
    }

    private void dispatchDeploy() {
        dispatcher.dispatch(new DeployAction(new Deployment("foo.jar", "server-1")));
        dispatcher.dispatch(new DeployAction(new Deployment("bar.jar", "server-1")));
        dispatcher.dispatch(new DeployAction(new Deployment("bar.jar", "server-2")));
    }
}
