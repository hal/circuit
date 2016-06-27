package org.jboss.gwt.circuit;

import com.google.common.collect.Iterables;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.jboss.gwt.circuit.dag.DAGDispatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ChangeSupportTest {

    private Dispatcher dispatcher;
    private FooStore fooStore;
    private FooBarAction action;

    @Before
    public void setUp() {
        dispatcher = new DAGDispatcher();
        fooStore = new FooStore(dispatcher);
        action = new FooBarAction(0);
    }

    @Test
    public void handlerRegistration() {
        final AtomicInteger counter = new AtomicInteger(0);
        HandlerRegistration handlerRegistration = fooStore.addChangeHandler(new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                counter.incrementAndGet();
            }
        });
        assertEquals(1, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(action)));

        dispatcher.dispatch(new FooBarAction(0));
        assertEquals(1, counter.get());

        handlerRegistration.removeHandler();
        assertEquals(0, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(action)));
    }

    @Test
    public void actionTypeRegistration() {
        final AtomicInteger counter = new AtomicInteger(0);
        HandlerRegistration actionTypeRegistration = fooStore.addChangeHandler(FooBarAction.class, new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                counter.incrementAndGet();
            }
        });
        assertEquals(0, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(1, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(action)));

        dispatcher.dispatch(new FooBarAction(0));
        assertEquals(1, counter.get());

        actionTypeRegistration.removeHandler();
        assertEquals(0, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(action)));
    }

    @Test
    public void actionRegistration() {
        final AtomicInteger counter = new AtomicInteger(0);
        HandlerRegistration actionTypeRegistration = fooStore.addChangeHandler(action, new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                counter.incrementAndGet();
            }
        });
        assertEquals(0, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(1, Iterables.size(fooStore.getChangeHandler(action)));

        dispatcher.dispatch(new FooBarAction(0));
        assertEquals(1, counter.get());

        actionTypeRegistration.removeHandler();
        assertEquals(0, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(action)));
    }

    @Test
    public void combinedRegistration() {
        final AtomicInteger counter = new AtomicInteger(0);
        HandlerRegistration handlerRegistration = fooStore.addChangeHandler(new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                counter.incrementAndGet();
            }
        });
        HandlerRegistration actionTypeRegistration = fooStore.addChangeHandler(FooBarAction.class, new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                counter.incrementAndGet();
            }
        });
        HandlerRegistration actionRegistration = fooStore.addChangeHandler(action, new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                counter.incrementAndGet();
            }
        });
        assertEquals(1, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(1, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(1, Iterables.size(fooStore.getChangeHandler(action)));

        dispatcher.dispatch(new FooBarAction(0));
        assertEquals(3, counter.get());

        handlerRegistration.removeHandler();
        actionTypeRegistration.removeHandler();
        actionRegistration.removeHandler();
        assertEquals(0, Iterables.size(fooStore.getChangeHandler()));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(FooBarAction.class)));
        assertEquals(0, Iterables.size(fooStore.getChangeHandler(action)));
    }
}