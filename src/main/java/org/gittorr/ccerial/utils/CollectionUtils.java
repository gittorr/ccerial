package org.gittorr.ccerial.utils;

import java.util.*;

public class CollectionUtils {

    public static <E> Collection<E> makeUnmodifiable(Collection<E> collection) {
        return Collections.unmodifiableCollection(collection);
    }

    public static <E> List<E> makeUnmodifiable(List<E> list) {
        return Collections.unmodifiableList(list);
    }

    public static <E> Set<E> makeUnmodifiable(Set<E> set) {
        return Collections.unmodifiableSet(set);
    }

    public static <E> SortedSet<E> makeUnmodifiable(SortedSet<E> set) {
        return Collections.unmodifiableSortedSet(set);
    }

    public static <E> NavigableSet<E> makeUnmodifiable(NavigableSet<E> set) {
        return Collections.unmodifiableNavigableSet(set);
    }

    public static <E> Queue<E> makeUnmodifiable(Queue<E> queue) {
        return new UnmodifiableQueue<>(queue);
    }

    public static <E> Deque<E> makeUnmodifiable(Deque<E> deque) {
        return new UnmodifiableDeque<>(deque);
    }

    // Additional classes for unmodifiable Queue and Deque
    private static class UnmodifiableQueue<E> extends AbstractQueue<E> {
        private final Queue<E> queue;

        UnmodifiableQueue(Queue<E> queue) {
            this.queue = Objects.requireNonNull(queue);
        }

        @Override
        public Iterator<E> iterator() {
            return Collections.unmodifiableCollection(queue).iterator();
        }

        @Override
        public int size() {
            return queue.size();
        }

        @Override
        public boolean offer(E e) {
            throw new UnsupportedOperationException("This queue is unmodifiable");
        }

        @Override
        public E poll() {
            throw new UnsupportedOperationException("This queue is unmodifiable");
        }

        @Override
        public E peek() {
            return queue.peek();
        }
    }

    private static class UnmodifiableDeque<E> extends UnmodifiableQueue<E> implements Deque<E> {
        private final Deque<E> deque;

        UnmodifiableDeque(Deque<E> deque) {
            super(deque);
            this.deque = Objects.requireNonNull(deque);
        }

        @Override
        public void addFirst(E e) {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public void addLast(E e) {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public boolean offerFirst(E e) {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public boolean offerLast(E e) {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public E removeFirst() {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public E removeLast() {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public E pollFirst() {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public E pollLast() {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public E getFirst() {
            return deque.getFirst();
        }

        @Override
        public E getLast() {
            return deque.getLast();
        }

        @Override
        public E peekFirst() {
            return deque.peekFirst();
        }

        @Override
        public E peekLast() {
            return deque.peekLast();
        }

        @Override
        public boolean removeFirstOccurrence(Object o) {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public boolean removeLastOccurrence(Object o) {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public void push(E e) {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public E pop() {
            throw new UnsupportedOperationException("This deque is unmodifiable");
        }

        @Override
        public Iterator<E> descendingIterator() {
            final Iterator<E> it = deque.descendingIterator();
            return new Iterator<E>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public E next() {
                    return it.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Unmodifiable deque");
                }
            };
        }

    }
}
