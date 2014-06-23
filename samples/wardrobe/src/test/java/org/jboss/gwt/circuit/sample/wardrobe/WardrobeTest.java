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
package org.jboss.gwt.circuit.sample.wardrobe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.gwt.circuit.Store;
import org.jboss.gwt.circuit.impl.DAGDispatcher;
import org.jboss.gwt.circuit.sample.wardrobe.actions.Dress;
import org.jboss.gwt.circuit.sample.wardrobe.actions.Undress;
import org.jboss.gwt.circuit.sample.wardrobe.stores.CoatStoreImpl;
import org.jboss.gwt.circuit.sample.wardrobe.stores.PulloverStoreImpl;
import org.jboss.gwt.circuit.sample.wardrobe.stores.ShoesStoreImpl;
import org.jboss.gwt.circuit.sample.wardrobe.stores.SocksStoreImpl;
import org.jboss.gwt.circuit.sample.wardrobe.stores.TrousersStoreImpl;
import org.jboss.gwt.circuit.sample.wardrobe.stores.UndershirtStoreImpl;
import org.jboss.gwt.circuit.sample.wardrobe.stores.UnderwearStoreImpl;
import org.junit.Before;
import org.junit.Test;


public class WardrobeTest {

    private WardrobeDispatcher dispatcher;
    private OrderRecorder orderRecorder;

    @Before
    public void setUp() {
        dispatcher = new WardrobeDispatcher(new DAGDispatcher());
        orderRecorder = new OrderRecorder();
        dispatcher.addDiagnostics(orderRecorder);

        new CoatStoreImpl(dispatcher.getDispatcher());
        new PulloverStoreImpl(dispatcher.getDispatcher());
        new ShoesStoreImpl(dispatcher.getDispatcher());
        new SocksStoreImpl(dispatcher.getDispatcher());
        new TrousersStoreImpl(dispatcher.getDispatcher());
        new UndershirtStoreImpl(dispatcher.getDispatcher());
        new UnderwearStoreImpl(dispatcher.getDispatcher());
    }

    @Test
    public void dressOrder() {
        dispatcher.dispatch(new Dress());
        List<Class<? extends Store>> order = orderRecorder.getOrder();

        // verify seven stores
        assertEquals(7, order.size());

        // verify dependencies: Coat
        assertTrue(order.indexOf(CoatStoreImpl.class) > order.indexOf(PulloverStoreImpl.class));
        assertTrue(order.indexOf(CoatStoreImpl.class) > order.indexOf(TrousersStoreImpl.class));
        assertTrue(order.indexOf(CoatStoreImpl.class) > order.indexOf(UndershirtStoreImpl.class));
        assertTrue(order.indexOf(CoatStoreImpl.class) > order.indexOf(UnderwearStoreImpl.class));

        // verify dependencies: Pullover
        assertTrue(order.indexOf(PulloverStoreImpl.class) > order.indexOf(UnderwearStoreImpl.class));

        // verify dependencies: Shoes
        assertTrue(order.indexOf(ShoesStoreImpl.class) > order.indexOf(TrousersStoreImpl.class));
        assertTrue(order.indexOf(ShoesStoreImpl.class) > order.indexOf(SocksStoreImpl.class));
        assertTrue(order.indexOf(ShoesStoreImpl.class) > order.indexOf(UnderwearStoreImpl.class));

        // verify dependencies: Trousers
        assertTrue(order.indexOf(TrousersStoreImpl.class) > order.indexOf(UnderwearStoreImpl.class));
    }

    @Test
    public void undressOrder() {
        dispatcher.dispatch(new Undress());
        List<Class<? extends Store>> order = orderRecorder.getOrder();

        // verify seven stores
        assertEquals(7, order.size());

        // verify dependencies: Pullover
        assertTrue(order.indexOf(PulloverStoreImpl.class) > order.indexOf(CoatStoreImpl.class));

        // verify dependencies: Socks
        assertTrue(order.indexOf(SocksStoreImpl.class) > order.indexOf(ShoesStoreImpl.class));

        // verify dependencies: Trousers
        assertTrue(order.indexOf(TrousersStoreImpl.class) > order.indexOf(CoatStoreImpl.class));
        assertTrue(order.indexOf(TrousersStoreImpl.class) > order.indexOf(ShoesStoreImpl.class));

        // verify dependencies: Undershirt
        assertTrue(order.indexOf(UndershirtStoreImpl.class) > order.indexOf(PulloverStoreImpl.class));
        assertTrue(order.indexOf(UndershirtStoreImpl.class) > order.indexOf(CoatStoreImpl.class));

        // verify dependencies: Underwear
        assertTrue(order.indexOf(UnderwearStoreImpl.class) > order.indexOf(TrousersStoreImpl.class));
        assertTrue(order.indexOf(UnderwearStoreImpl.class) > order.indexOf(CoatStoreImpl.class));
        assertTrue(order.indexOf(UnderwearStoreImpl.class) > order.indexOf(ShoesStoreImpl.class));
    }
}
