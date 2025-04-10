package designpattern.decorator;

public class Main {
    public static void main(String[] args) {
        Beverage darkRoast = new DarkRoast();
        darkRoast = new Milk(darkRoast);
        darkRoast = new Mocha(darkRoast);
        darkRoast = new Soy(darkRoast);

        System.out.println("商品：" + darkRoast.getDescription() + "\n价格：" + darkRoast.cost());

        Beverage espresso = new Espresso();
        espresso = new Milk(espresso);
        espresso = new Whip(espresso);

        System.out.println("商品：" + espresso.getDescription() + "\n价格：" + espresso.cost());
    }
}
