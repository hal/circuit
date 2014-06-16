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
package org.jboss.gwt.flux.sample.calculator;

public class Term {

    public enum Op {
        PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/");
        private final String sign;

        Op(final String sign) {
            this.sign = sign;
        }

        public String sign() {
            return sign;
        }
    }


    private final int left;
    private final Op op;
    private final int right;

    public Term(final int left, final Op op, final int right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Term)) { return false; }

        Term term = (Term) o;

        if (left != term.left) { return false; }
        if (right != term.right) { return false; }
        if (op != term.op) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + op.hashCode();
        result = 31 * result + right;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d %s %d", left, op.sign(), right);
    }

    public int getLeft() {
        return left;
    }

    public Op getOp() {
        return op;
    }

    public int getRight() {
        return right;
    }
}
