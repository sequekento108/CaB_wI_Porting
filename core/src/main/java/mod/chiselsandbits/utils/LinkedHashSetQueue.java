package mod.chiselsandbits.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LinkedHashSetQueue<T> implements Queue<T> {

    private final LinkedHashSet<T> set = new LinkedHashSet<>();

    public LinkedHashSetQueue() {
    }

    @Override
    public Spliterator<T> spliterator() {
        return set.spliterator();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public boolean add(T t) {
        return set.add(t);
    }

    @Override
    public boolean offer(T t) {
        return add(t);
    }

    @Override
    public T remove() {
        if (this.isEmpty())
            throw new NoSuchElementException();

        final T first = set.iterator().next();
        remove(first);
        return first;
    }

    @Override
    public T poll() {
        if (this.isEmpty())
            return null;

        final T first = set.iterator().next();
        remove(first);
        return first;
    }

    @Override
    public T element() {
        if (this.isEmpty())
            throw new NoSuchElementException();

        return set.iterator().next();
    }

    @Override
    public T peek() {
        if (this.isEmpty())
            return null;

        return set.iterator().next();
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public Object clone() {
        return set.clone();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return set.toArray();
    }

    @NotNull
    @Override
    public <T1> T1 @NotNull [] toArray(T1 @NotNull [] a) {
        return set.toArray(a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedHashSetQueue<?> that = (LinkedHashSetQueue<?>) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return set.removeAll(c);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return set.addAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public String toString() {
        return set.toString();
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return set.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return set.removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return set.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return set.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        set.forEach(action);
    }
}
