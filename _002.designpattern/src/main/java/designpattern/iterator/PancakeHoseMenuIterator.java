package designpattern.iterator;

import java.util.ArrayList;

public class PancakeHoseMenuIterator implements Iterator {
    ArrayList menuItems;
    int position; // 记录当前位置

    public PancakeHoseMenuIterator(ArrayList menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public boolean hasNext() {
        if (position >= menuItems.size()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object next() {
        return menuItems.get(position++);
    }
}
