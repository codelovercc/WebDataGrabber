package com.cool.util;

import java.util.*;

/**
 * Created by codelover on 17/6/19.
 */
public class LinkedMaxList<E> implements MaxList<E> {

    private final LinkedList<E> list;
    private int maxSize;

    public LinkedMaxList(int maxSize) {
        this.maxSize = maxSize;
        list = new LinkedList<E>();
    }

    @Override
    public boolean add(E e) {
        if (isMaxed()) {
            list.removeFirst();
        }
        return list.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean b = true;
        for (E e :
                c) {
            b &= add(e);
        }
        return b;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        for (E e :
                c) {
            add(index++, e);
        }
        return true;
    }

    @Override
    public void add(int index, E element) {
        if (isMaxed()) {
            if (index != 0) {
                list.removeFirst();
            } else {
                try {
                    list.remove(1);
                } catch (IndexOutOfBoundsException ignored) {

                }
            }
        }
        list.add(index, element);
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        int size = size() - maxSize;
        for (int i = 0; i < size; i++) {
            try {
                list.removeFirst();
            } catch (NoSuchElementException e) {
                break;
            }
        }

    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isMaxed() {
        return size() >= maxSize;
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
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
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

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        return list.set(index, element);
    }


    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}
