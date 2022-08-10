package com.hrbu.designpattern.iterator;

public class DinerMenu implements Menu {
    static final int MAX_ITEM = 6;
    int numOfItems = 0;
    MenuItem[] menuItems;

    public DinerMenu() {
        menuItems = new MenuItem[MAX_ITEM];
        addItem("11", "11", true, 1.1);
        addItem("22", "22", true, 2.2);
        addItem("33", "33", true, 3.3);
        addItem("44", "44", true, 4.4);
        addItem("55", "55", true, 5.5);
    }

    public void addItem(String name, String description, boolean vegetaian, double price) {
        MenuItem menuItem = new MenuItem(name, description, vegetaian, price);

        if (numOfItems >= MAX_ITEM) {
            System.out.println("超过最大保存菜单数");
        } else {
            menuItems[numOfItems++] = new MenuItem(name, description, vegetaian, price);
        }
    }

    /*public MenuItem[] getMenuItems() {
        return menuItems;
    }*/

    @Override
    public Iterator createIterator() {
        return new DinerMenuIterator(menuItems);
    }
}
