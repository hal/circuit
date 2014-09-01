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
import org.jboss.gwt.circuit.meta.Process;
import org.jboss.gwt.circuit.meta.Store;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Store
public class BookStore {

    private final Map<Book, Rating> ratings;

    public BookStore() {
        ratings = new HashMap<>();
    }

    @Process(actionType = Rate.class)
    public void rate(int stars, Book book, Dispatcher.Channel channel) {
        Rating rating = ratings.get(book);
        if (rating == null) {
            rating = new Rating();
            ratings.put(book, rating);
        }
        rating.add(stars);
        channel.ack();
    }

    public double getRating(Book book) {
        return ratings.containsKey(book) ? ratings.get(book).avg() : 0.0;
    }

    private final static class Rating {
        private final List<Integer> ratings;

        private Rating() {
            ratings = new LinkedList<>();
        }

        private void add(int rating) {
            ratings.add(rating);
        }

        private double avg() {
            if (ratings.isEmpty()) {
                return 0.0;
            }
            int sum = 0;
            for (Integer rating : ratings) {
                sum += rating;
            }
            return sum / ratings.size();
        }
    }
}
