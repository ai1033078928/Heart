package com.hrbu.designpattern.factory;

public class Main {
    public static void main(String[] args) {
        PizzaStore nyStylePizzaStore = new NYStylePizzaStore(new NYPizzaIngredientFactory());
        PizzaStore chicagoStylePizzaStore = new ChicagoStylePizzaStore(new ChicagoPizzaIngredientFactory());

        Pizza nyCheese = nyStylePizzaStore.orderPizza("Clam");
        System.out.println(nyCheese.getName());

        Pizza chCheese = chicagoStylePizzaStore.orderPizza("Clam");
        System.out.println(chCheese.getName());
    }
}
