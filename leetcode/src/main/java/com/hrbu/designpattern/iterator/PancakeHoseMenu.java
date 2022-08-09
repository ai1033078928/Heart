package com.hrbu.designpattern.iterator;

import java.util.ArrayList;

public class PancakeHoseMenu {
    ArrayList menuItems;

    public PancakeHoseMenu() {
        menuItems = new ArrayList<>();
        addItem("111", "111", true, 1.11);
        addItem("222", "222", true, 2.22);
        addItem("333", "333", true, 3.33);
        addItem("444", "444", true, 4.44);
        addItem("555", "555", true, 5.55);
    }

    public void addItem(String name, String description, boolean vegetaian, double price) {
        MenuItem menuItem = new MenuItem(name, description, vegetaian, price);
        menuItems.add(menuItem);
    }

    /*public ArrayList getMenuItem() {
        return menuItems;
    }*/
    public Iterator createIterator() {
        return new PancakeHoseMenuIterator(menuItems);
    }
}
