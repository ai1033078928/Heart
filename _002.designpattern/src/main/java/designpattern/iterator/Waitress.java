package designpattern.iterator;

public class Waitress {
    Menu[] menus;

    public Waitress(Menu[] menus) {
        this.menus = menus;
    }

    private void printMenu(Iterator iterator) {
        while (iterator.hasNext()) {
            MenuItem menuItem = (MenuItem)iterator.next();
            System.out.println(menuItem.toString());
        }
    }

    public void printMenu() {
        Iterator pancakeHoseMenuIterator = menus[0].createIterator();
        Iterator dinerMenuIterator = menus[1].createIterator();
        Iterator cafeMenuIterator = menus[2].createIterator();
        System.out.println("----- pancakeHoseMenuIterator ----");
        printMenu(pancakeHoseMenuIterator);
        System.out.println("----- dinerMenuIterator ----");
        printMenu(dinerMenuIterator);
        System.out.println("----- cafeMenuIterator ----");
        printMenu(cafeMenuIterator);
    }
}
