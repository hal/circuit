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
package org.jboss.gwt.flux;

import static org.junit.Assert.assertTrue;

import org.jboss.gwt.flux.impl.DefaulDispatcherImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Harald Pehl
 */
public class DispatcherTest {

    private Dispatcher dispatcher;
    private DeploymentStore deploymentStore;
    private HostStore hostStore;

    @Before
    public void setUp() {
        dispatcher = new DefaulDispatcherImpl();
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
}
