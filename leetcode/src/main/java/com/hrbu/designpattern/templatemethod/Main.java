package com.hrbu.designpattern.templatemethod;

public class Main {
    public static void main(String[] args) {
        TeaWithHook teaWithHook = new TeaWithHook();
        CoffeeWithHook coffeeWithHook = new CoffeeWithHook();

        System.out.println("制作茶...");
        teaWithHook.prepareRecipe();

        System.out.println("制作咖啡...");
        coffeeWithHook.prepareRecipe();

    }
}
