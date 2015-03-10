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
package org.jboss.gwt.circuit.sample.bookstore;

import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.dag.DAGDispatcher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookStoreTest {

    private Dispatcher dispatcher;
    private BookStore store;
    private Book book;

    @Before
    public void setUp() {
        dispatcher = new DAGDispatcher();
        store = new BookStore();
        new BookStoreAdapter(store, dispatcher);
        book = new Book("isbn-978-0345417954", "The Hotel New Hampshire", "John Irving");
    }

    @Test
    public void rate() {
        dispatcher.dispatch(new Rate(book, 1));
        dispatcher.dispatch(new Rate(book, 2));
        dispatcher.dispatch(new Rate(book, 3));
        dispatcher.dispatch(new Rate(book, 4));
        dispatcher.dispatch(new Rate(book, 5));

        assertEquals(3.0, store.getRating(book), .01);
    }
}
