package com.hrbu.designpattern.iterator;

public class Waitress {
    PancakeHoseMenu pancakeHoseMenu;
    DinerMenu dinerMenu;

    public Waitress(PancakeHoseMenu pancakeHoseMenu, DinerMenu dinerMenu) {
        this.pancakeHoseMenu = pancakeHoseMenu;
        this.dinerMenu = dinerMenu;
    }

    private void printMenu(Iterator iterator) {
        while (iterator.hasNext()) {
            MenuItem menuItem = (MenuItem)iterator.next();
            System.out.println(menuItem.toString());
        }
    }

    public void printMenu() {
        Iterator pancakeHoseMenuIterator = pancakeHoseMenu.createIterator();
        Iterator dinerMenuIterator = dinerMenu.createIterator();
        System.out.println("----- pancakeHoseMenuIterator ----");
        printMenu(pancakeHoseMenuIterator);
        System.out.println("----- dinerMenuIterator ----");
        printMenu(dinerMenuIterator);
    }
}
