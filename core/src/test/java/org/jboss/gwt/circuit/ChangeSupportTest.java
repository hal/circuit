package org.jboss.gwt.circuit;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Iterables;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.jboss.gwt.circuit.dag.DAGDispatcher;
import org.junit.Before;
import org.junit.Test;

public class ChangeSupportTest {

    private Dispatcher dispatcher;
    private FooStore fooStore;

    @Before
    public void setUp() {
        dispatcher = new DAGDispatcher();
        fooStore = new FooStore(dispatcher);
    }

    @Test
    public void handlerRegistration() {
        final AtomicInteger counter = new AtomicInteger(0);
        HandlerRegistration handlerRegistration = fooStore.addChangeHandler(new PropagatesChange.Handler() {
            @Override
            public void onChanged(final Class<?> actionType) {
                counter.incrementAndGet();
            }
        });
        assertEquals(1, Iterables.size(fooStore.getHandler()));
        assertEquals(0, Iterables.size(fooStore.getActionHandler(FooBarAction.class)));

        dispatcher.dispatch(new FooBarAction(0));
        assertEquals(1, counter.get());

        handlerRegistration.removeHandler();
        assertEquals(0, Iterables.size(fooStore.getHandler()));
        assertEquals(0, Iterables.size(fooStore.getActionHandler(FooBarAction.class)));
    }

    @Test
    public void actionRegistration() {
        final AtomicInteger counter = new AtomicInteger(0);
        HandlerRegistration actionRegistration = fooStore.addChangeHandler(FooBarAction.class, new PropagatesChange.Handler() {
            @Override
            public void onChanged(final Class<?> actionType) {
                counter.incrementAndGet();
            }
        });
        assertEquals(0, Iterables.size(fooStore.getHandler()));
        assertEquals(1, Iterables.size(fooStore.getActionHandler(FooBarAction.class)));

        dispatcher.dispatch(new FooBarAction(0));
        assertEquals(1, counter.get());

        actionRegistration.removeHandler();
        assertEquals(0, Iterables.size(fooStore.getHandler()));
        assertEquals(0, Iterables.size(fooStore.getActionHandler(FooBarAction.class)));
    }

    @Test
    public void combinedRegistration() {
        final AtomicInteger counter = new AtomicInteger(0);
        HandlerRegistration handlerRegistration = fooStore.addChangeHandler(new PropagatesChange.Handler() {
            @Override
            public void onChanged(final Class<?> actionType) {
                counter.incrementAndGet();
            }
        });
        HandlerRegistration actionRegistration = fooStore.addChangeHandler(FooBarAction.class, new PropagatesChange.Handler() {
            @Override
            public void onChanged(final Class<?> actionType) {
                counter.incrementAndGet();
            }
        });
        assertEquals(1, Iterables.size(fooStore.getHandler()));
        assertEquals(1, Iterables.size(fooStore.getActionHandler(FooBarAction.class)));

        dispatcher.dispatch(new FooBarAction(0));
        assertEquals(2, counter.get());

        handlerRegistration.removeHandler();
        actionRegistration.removeHandler();
        assertEquals(0, Iterables.size(fooStore.getHandler()));
        assertEquals(0, Iterables.size(fooStore.getActionHandler(FooBarAction.class)));
    }
}