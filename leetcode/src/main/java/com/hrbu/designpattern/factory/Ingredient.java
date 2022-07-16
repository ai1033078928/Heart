package com.hrbu.designpattern.factory;

public interface Ingredient {}

class Dough implements Ingredient {
    String name = "Dough";
    public Dough() {
        System.out.println("配料: " + name);
    }
}
class Sauce implements Ingredient {
    String name = "Sauce";
    public Sauce() {
    }
}
class Cheese implements Ingredient {
    String name = "Cheese";
    public Cheese() {
        System.out.println("配料: " + name);
    }
}
class Veggies implements Ingredient {
    String name = "Veggies";
}
class Carrot extends Veggies {
    public Carrot() {
        name = "胡萝卜";
        System.out.println("配料: " + name);
    }
}
class Lettuce extends Veggies {
    public Lettuce() {
        name = "生菜";
        System.out.println("配料: " + name);
    }
}
class Pepperoni implements Ingredient {
    String name = "Pepperoni";
    public Pepperoni() {
        System.out.println("配料: " + name);
    }
}

interface Clams extends Ingredient {}

class FreshClams implements Clams {
    String name = "新鲜蛤蜊";
    public FreshClams() {
        System.out.println("配料: " + name);
    }
}
class FrozenClams implements Clams {
    String name = "冰冻蛤蜊";
    public FrozenClams() {
        System.out.println("配料: " + name);
    }
}