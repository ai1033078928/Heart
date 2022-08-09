package com.hrbu.designpattern.iterator;

interface Iterator {
    boolean hasNext();
    Object next();
}
public class DinerMenuIterator implements Iterator{
    MenuItem[] items;
    int position; // 记录当前位置

    public DinerMenuIterator(MenuItem[] items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        if (position >= items.length || null == items[position]) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object next() {
        return items[position++];
    }
}
