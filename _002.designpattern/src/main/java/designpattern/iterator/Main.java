package designpattern.iterator;

public class Main {
    public static void main(String[] args) {
        Menu pancakeHoseMenu = new PancakeHoseMenu();
        Menu dinerMenu = new DinerMenu();
        Menu cafeMenu = new CafeMenu();

        Waitress waitress = new Waitress(new Menu[]{pancakeHoseMenu, dinerMenu, cafeMenu});
        waitress.printMenu();
    }
}
