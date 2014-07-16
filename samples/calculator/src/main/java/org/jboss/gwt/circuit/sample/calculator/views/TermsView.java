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
package org.jboss.gwt.circuit.sample.calculator.views;

import org.jboss.gwt.circuit.ChangeManagement;
import org.jboss.gwt.circuit.ChangedEvent;
import org.jboss.gwt.circuit.sample.calculator.CalculatorStore;

public class TermsView implements View {

    public TermsView(final CalculatorStore store, final ChangeManagement changeManagement) {
        changeManagement.addHandler(CalculatorStore.class, new ChangeManagement.Handler() {
            @Override
            public void onChange(final ChangedEvent event) {
                System.out.printf("Number of terms:    %d\n", store.getResults().size());
            }
        });
    }
}
