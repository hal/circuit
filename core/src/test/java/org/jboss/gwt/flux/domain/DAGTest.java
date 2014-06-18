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
package org.jboss.gwt.flux.domain;

import java.util.Set;

import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.Assert;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Harald Pehl
 */
public class DAGTest {

    private DirectedGraph<String, DefaultEdge> dag;

    @Before
    public void setUp() {
        dag = new DefaultDirectedGraph<>(DefaultEdge.class);

        dag.addVertex("A");
        dag.addVertex("B");
        dag.addVertex("C");
        dag.addVertex("D");
        dag.addVertex("E");
        dag.addVertex("F");
        dag.addVertex("G");

        dag.addEdge("A", "G");
        dag.addEdge("A", "B");
        dag.addEdge("A", "F");

        dag.addEdge("B", "C");
        dag.addEdge("B", "E");

        dag.addEdge("C", "G");

        dag.addEdge("D", "C");

        dag.addEdge("E", "D");

        dag.addEdge("F", "D");
    }

    @Test
    public void addCycle() throws DirectedAcyclicGraph.CycleFoundException {
        dag.addEdge("C", "A");
        CycleDetector<String, DefaultEdge> detector = new CycleDetector<>(dag);
        Set<String> cycles = detector.findCycles();
        Assert.assertFalse(cycles.isEmpty());
    }

    @Test
    public void executionOrder() {
        TopologicalOrderIterator<String, DefaultEdge> iterator = new TopologicalOrderIterator<>(
                dag);
        while(iterator.hasNext()) {
            String vertex = iterator.next();
            System.out.println(vertex);
        }

        System.out.println("----- Depth First (Visitor Pattern!)");
        DepthFirstIterator<String, DefaultEdge> iterator1 = new DepthFirstIterator<>(dag, "A");
        while(iterator1.hasNext()) {
            String vertex = iterator1.next();
            System.out.println(vertex);
        }
    }
}
