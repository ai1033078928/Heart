package designpattern.iterator;

import java.util.Hashtable;

public class CafeMenuIterator implements Iterator {
    Hashtable cafeMenu;
    int position; // 记录当前位置
    Object[] objects;  // Hashtable的values转化为数组

    public CafeMenuIterator(Hashtable cafeMenu) {
        this.cafeMenu = cafeMenu;
        objects = cafeMenu.values().toArray();
    }

    @Override
    public boolean hasNext() {
        if (position >= cafeMenu.values().size()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object next() {
        return objects[position++];
    }
}
