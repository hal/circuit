package org.jboss.gwt.flux.impl;

import org.jboss.gwt.flux.Dispatcher;

/**
 * @author Heiko Braun
 * @date 16/06/14
 */
public class NoopContext {
    public static Dispatcher.Context INSTANCE = new Dispatcher.Context() {
        @Override
        public void yield() {
            // noop
        }
    };
}
