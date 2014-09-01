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

import org.jboss.gwt.circuit.Action;

public class Rate implements Action {

    private final Book book;
    private final int stars;

    public Rate(Book book, int stars) {
        this.book = book;
        this.stars = stars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rate)) return false;

        Rate rate = (Rate) o;

        if (stars != rate.stars) return false;
        if (!book.equals(rate.book)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = book.hashCode();
        result = 31 * result + stars;
        return result;
    }

    @Override
    public String toString() {
        return "Rate(" + book + ": " + stars + " stars)";
    }

    public Book getBook() {
        return book;
    }

    public int getStars() {
        return stars;
    }
}
