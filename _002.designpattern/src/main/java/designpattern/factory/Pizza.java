package designpattern.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Pizza {
    protected String name;            // 披萨名字
    protected Dough dough;           // 面团类型
    protected Sauce sauce;           // 酱料类型
    protected List<Ingredient> topping= new ArrayList();     // 一套佐料
    protected Clams clams;

    public String getName() {
        return name;
    }

    public void perpare(){
        System.out.println("准备原料");
    }
    public void bake(){
        System.out.println("炙烤披萨");
    }
    public void cut(){
        System.out.println("披萨切片");
    }
    public void box(){
        System.out.println("披萨装盒");
    }
}


class ClamPizza extends Pizza {
    PizzaIngredientFactory pizzaIngredientFactory;   // 新增原料工厂

    public ClamPizza(PizzaIngredientFactory pizzaIngredientFactory) {
        name = "ClamPizza";
        this.pizzaIngredientFactory = pizzaIngredientFactory;
    }

    @Override
    public void perpare() {
        super.perpare();
        dough = pizzaIngredientFactory.createDough();
        sauce = pizzaIngredientFactory.createSauce();
        topping = Arrays.asList(pizzaIngredientFactory.createVeggies());
        clams = pizzaIngredientFactory.createClam();
    }
}
class VeggiePizza extends Pizza {
    public VeggiePizza() {
        name = "VeggiePizza";
        System.out.println("初始化" + name);
    }
}
class NYStyleCheesePizza extends Pizza {
    public NYStyleCheesePizza() {
        name = "NYStyleCheesePizza";
        System.out.println("初始化" + name);
    }
}
class ChicagoStyleCheesePizza extends Pizza {
    public ChicagoStyleCheesePizza() {
        name = "ChicagoStyleCheesePizza";
        System.out.println("初始化" + name);
    }

    @Override
    public void cut() {
        System.out.println("披萨切为方块");
    }
}