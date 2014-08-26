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
package org.jboss.gwt.circuit.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to select a payload from an action by name. Must be used on process methods when the payload's type is
 * ambiguous.
 * <p/>
 * <pre>
 * public class Rate implements Action {
 *     private final String author;
 *     private final int stars;
 *     private final String comment;
 *
 *     public Rate(final String author, final int stars, final String comment) {
 *         this.author = author;
 *         this.stars = stars;
 *         this.comment = comment;
 *     }
 *
 *     public String getAuthor() { return author; }
 *     public int getStars() { return stars; }
 *     public String getComment() { return comment; }
 * }
 *
 * {@code @}Store
 * public BookStore {
 *     {@code @}Process(actionType = Rate.class)
 *     void rate(final int stars,
 *               {@code @}Payload("comment") final String comment,
 *               {@code @}Payload("author") final String author,
 *               final Dispatcher.Channel channel) {
 *         ...
 *     }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Payload {

    String value();
}
