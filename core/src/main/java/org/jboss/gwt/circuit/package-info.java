/**
 * <h1>Circuit</h1>
 * <p>Circuit is an adoption of the Flux architecture as described at http://facebook.github.io/react/docs/flux-overview.html.</p>
 *
 * <h2>Core Building Blocks</h2>
 *
 * <h3>Dispatcher</h3>
 * <p>PENDING</p>
 *
 * <h3>Stores</h3>
 * <p>
 * A store holds application state and manages segments of the domain model used by an application.
 * In to process actions store register callbacks with a {@link org.jboss.gwt.circuit.Dispatcher}.<p/>
 *
 * When actions are dispatched <u>stores run through a voting and a completion phase</u>.
 * Voting allows to reject actions or declare dependencies on other stores. The actual processing of the action
 * (and all corresponding state changes) is done in the completion phase.<p/>
 *
 * <u>It is mandatory to acknowledge each action through the {@link org.jboss.gwt.circuit.Dispatcher.Channel} after completion</u>.
 * </p>
 */
package org.jboss.gwt.circuit;