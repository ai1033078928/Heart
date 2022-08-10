package com.hrbu.designpattern.iterator;

import java.util.Hashtable;

@SuppressWarnings("all")
public class CafeMenu implements Menu {
    Hashtable menuItems = new Hashtable();

    public CafeMenu() {
        this.menuItems = menuItems;
        addItem("1", "1", true, 1);
        addItem("2", "2", true, 2);
        addItem("3", "3", true, 3);
        addItem("4", "4", true, 4);
        addItem("5", "5", true, 5);
    }

    public void addItem(String name, String description, boolean vegetaian, double price) {
        MenuItem menuItem = new MenuItem(name, description, vegetaian, price);
        menuItems.put(menuItem.getName(), menuItem);
    }

    @Override
    public Iterator createIterator() {
        return new CafeMenuIterator(menuItems);
    }
}
