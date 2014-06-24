package org.jboss.gwt.circuit.dag;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * An optionally-bounded blocking queue based on linked nodes.
 * This queue orders elements FIFO (first-in-first-out).
 * The head of the queue is that element that has been on the queue the longest time.
 * The tail of the queue is that element that has been on the queue the shortest time.
 * New elements are inserted at the tail of the queue, and the queue retrieval operations obtain elements at the head of the queue.
 * <p/>
 * The optional capacity bound constructor argument serves as a way to prevent excessive queue expansion.
 * The capacity, if unspecified, is equal to Integer.MAX_VALUE.
 * Linked nodes are dynamically created upon each insertion unless this would bring the queue above capacity.
 *
 * @author Heiko Braun
 * @param <T> queue items
 */
public class BoundedQueue<T> implements Queue<T> {

    private final LinkedList<T> list = new LinkedList<T>();

    private final int capacity;

    public BoundedQueue(int capacity) {
        this.capacity = capacity;
    }

    public BoundedQueue() {
        this.capacity = Integer.MAX_VALUE;
    }

    @Override
    public boolean add(T e) {
        if(size()>=capacity)
            throw new IllegalStateException("Capacity exceeded: "+capacity);

        return list.add(e);
    }

    @Override
    public boolean offer(T e) {
        boolean added = false;
        if(size()<capacity) {
            added = list.add(e);
        }
        return added;
    }

    @Override
    public T remove() {
        return list.removeFirst();
    }

    @Override
    public T poll() {
        if (list.size() > 0) {
            return list.removeFirst();
        }
        return null;
    }

    @Override
    public T element() {
        if (list.size() > 0) {
            return list.getFirst();
        }
        throw new NoSuchElementException();
    }

    @Override
    public T peek() {
        if (list.size() > 0) {
            return list.getFirst();
        }
        return null;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    public int remainingCapacity() {
        return capacity-size();
    }

}