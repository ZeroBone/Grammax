package net.zerobone.grammax.utils.zerolist;

import java.util.ArrayList;
import java.util.Iterator;

public class ZeroListIterator<T extends ZeroListable> implements Iterator<T> {

    private int i = 0;

    private final ArrayList<T> elements;

    ZeroListIterator(ArrayList<T> elements) {
        this.elements = elements;
    }

    @Override
    public boolean hasNext() {
        return i < elements.size();
    }

    @Override
    public T next() {
        return elements.get(i++);
    }

}