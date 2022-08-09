package com.hrbu.designpattern.iterator;

public class Main {
    public static void main(String[] args) {
        PancakeHoseMenu pancakeHoseMenu = new PancakeHoseMenu();
        DinerMenu dinerMenu = new DinerMenu();

        Waitress waitress = new Waitress(pancakeHoseMenu, dinerMenu);
        waitress.printMenu();
    }
}
