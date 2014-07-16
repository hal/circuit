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
 * but WITHOUT ANY WARRANTY; without even the Adapteried warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.gwt.circuit.sample.wardrobe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.gwt.circuit.ChangeManagement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.dag.DAGDispatcher;
import org.jboss.gwt.circuit.sample.wardrobe.actions.Dress;
import org.jboss.gwt.circuit.sample.wardrobe.actions.Undress;
import org.jboss.gwt.circuit.sample.wardrobe.stores.CoatStore;
import org.jboss.gwt.circuit.sample.wardrobe.stores.CoatStoreAdapter;
import org.jboss.gwt.circuit.sample.wardrobe.stores.PulloverStore;
import org.jboss.gwt.circuit.sample.wardrobe.stores.PulloverStoreAdapter;
import org.jboss.gwt.circuit.sample.wardrobe.stores.ShoesStore;
import org.jboss.gwt.circuit.sample.wardrobe.stores.ShoesStoreAdapter;
import org.jboss.gwt.circuit.sample.wardrobe.stores.SocksStore;
import org.jboss.gwt.circuit.sample.wardrobe.stores.SocksStoreAdapter;
import org.jboss.gwt.circuit.sample.wardrobe.stores.TrousersStore;
import org.jboss.gwt.circuit.sample.wardrobe.stores.TrousersStoreAdapter;
import org.jboss.gwt.circuit.sample.wardrobe.stores.UndershirtStore;
import org.jboss.gwt.circuit.sample.wardrobe.stores.UndershirtStoreAdapter;
import org.jboss.gwt.circuit.sample.wardrobe.stores.UnderwearStore;
import org.jboss.gwt.circuit.sample.wardrobe.stores.UnderwearStoreAdapter;
import org.junit.Before;
import org.junit.Test;


public class WardrobeTest {

    private Dispatcher dispatcher;
    private OrderRecorder orderRecorder;

    @Before
    public void setUp() {
        dispatcher = new DAGDispatcher(new ChangeManagement());
        orderRecorder = new OrderRecorder();
        dispatcher.addDiagnostics(orderRecorder);

        new CoatStoreAdapter(new CoatStore(), dispatcher);
        new PulloverStoreAdapter(new PulloverStore(), dispatcher);
        new ShoesStoreAdapter(new ShoesStore(), dispatcher);
        new SocksStoreAdapter(new SocksStore(), dispatcher);
        new TrousersStoreAdapter(new TrousersStore(), dispatcher);
        new UndershirtStoreAdapter(new UndershirtStore(), dispatcher);
        new UnderwearStoreAdapter(new UnderwearStore(), dispatcher);
    }

    @Test
    public void dressOrder() {
        dispatcher.dispatch(new Dress());
        List<Class<?>> order = orderRecorder.getOrder();

        // verify seven stores
        assertEquals(7, order.size());

        // verify dependencies: Coat
        assertTrue(order.indexOf(CoatStore.class) > order.indexOf(PulloverStore.class));
        assertTrue(order.indexOf(CoatStore.class) > order.indexOf(TrousersStore.class));
        assertTrue(order.indexOf(CoatStore.class) > order.indexOf(UndershirtStore.class));
        assertTrue(order.indexOf(CoatStore.class) > order.indexOf(UnderwearStore.class));

        // verify dependencies: Pullover
        assertTrue(order.indexOf(PulloverStore.class) > order.indexOf(UnderwearStore.class));

        // verify dependencies: Shoes
        assertTrue(order.indexOf(ShoesStore.class) > order.indexOf(TrousersStore.class));
        assertTrue(order.indexOf(ShoesStore.class) > order.indexOf(SocksStore.class));
        assertTrue(order.indexOf(ShoesStore.class) > order.indexOf(UnderwearStore.class));

        // verify dependencies: Trousers
        assertTrue(order.indexOf(TrousersStore.class) > order.indexOf(UnderwearStore.class));
    }

    @Test
    public void undressOrder() {
        dispatcher.dispatch(new Undress());
        List<Class<?>> order = orderRecorder.getOrder();

        // verify seven stores
        assertEquals(7, order.size());

        // verify dependencies: Pullover
        assertTrue(order.indexOf(PulloverStore.class) > order.indexOf(CoatStore.class));

        // verify dependencies: Socks
        assertTrue(order.indexOf(SocksStore.class) > order.indexOf(ShoesStore.class));

        // verify dependencies: Trousers
        assertTrue(order.indexOf(TrousersStore.class) > order.indexOf(CoatStore.class));
        assertTrue(order.indexOf(TrousersStore.class) > order.indexOf(ShoesStore.class));

        // verify dependencies: Undershirt
        assertTrue(order.indexOf(UndershirtStore.class) > order.indexOf(PulloverStore.class));
        assertTrue(order.indexOf(UndershirtStore.class) > order.indexOf(CoatStore.class));

        // verify dependencies: Underwear
        assertTrue(order.indexOf(UnderwearStore.class) > order.indexOf(TrousersStore.class));
        assertTrue(order.indexOf(UnderwearStore.class) > order.indexOf(CoatStore.class));
        assertTrue(order.indexOf(UnderwearStore.class) > order.indexOf(ShoesStore.class));
    }
}
