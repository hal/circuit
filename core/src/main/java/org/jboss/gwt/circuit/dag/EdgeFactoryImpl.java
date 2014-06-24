package org.jboss.gwt.circuit.dag;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultEdge;

/**
 * @author Heiko Braun
 * @date 20/06/14
 */
public class EdgeFactoryImpl implements EdgeFactory<Class<?>, DefaultEdge> {
    @Override
    public DefaultEdge createEdge(final Class<?> sourceVertex, final Class<?> targetVertex) {
        return new DefaultEdge(sourceVertex, targetVertex);
    }
}
