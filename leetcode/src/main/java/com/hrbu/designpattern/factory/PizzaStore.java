package com.hrbu.designpattern.factory;

public abstract class PizzaStore {
    Pizza pizza = null;
    PizzaIngredientFactory pizzaIngredientFactory;   // 新增原料工厂

    public Pizza orderPizza(String type) {
        return createPizza(type);
    }

    public void makePizza() {
        if (null != this.pizza) {
            pizza.perpare();
            pizza.bake();
            pizza.cut();
            pizza.box();
        }
    }

    protected abstract Pizza createPizza(String type);
}

class NYStylePizzaStore extends PizzaStore {
    public NYStylePizzaStore(PizzaIngredientFactory pizzaIngredientFactory) {
        super.pizzaIngredientFactory = pizzaIngredientFactory;
    }

    @Override
    protected Pizza createPizza(String type) {
        if ("clam".equalsIgnoreCase(type)) {
            pizza = new ClamPizza(pizzaIngredientFactory);
        } else if ("veggie".equalsIgnoreCase(type)) {
            pizza = new VeggiePizza();
        } else if ("cheese".equalsIgnoreCase(type)) {
            pizza = new NYStyleCheesePizza();
        }
        makePizza();
        return pizza;
    }
}

class ChicagoStylePizzaStore extends PizzaStore {
    public ChicagoStylePizzaStore(PizzaIngredientFactory pizzaIngredientFactory) {
        super.pizzaIngredientFactory = pizzaIngredientFactory;
    }

    @Override
    protected Pizza createPizza(String type) {
        if ("clam".equalsIgnoreCase(type)) {
            pizza = new ClamPizza(pizzaIngredientFactory);
        } else if ("veggie".equalsIgnoreCase(type)) {
            pizza = new VeggiePizza();
        } else if ("cheese".equalsIgnoreCase(type)) {
            pizza = new ChicagoStyleCheesePizza();
        }
        makePizza();
        return pizza;
    }
}