package designpattern.composite;

public class MenuItem extends MenuComponent {
    String name;
    String description;
    boolean vegetaian;
    double price;

    public MenuItem(String name, String description, boolean vegetaian, double price) {
        this.name = name;
        this.description = description;
        this.vegetaian = vegetaian;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isVegetarian() {
        return vegetaian;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void print() {
        String isV = isVegetarian() ? "(v)" : "";
        System.out.println(" " + getName() + isV);
        System.out.println(" " + getDescription());
        System.out.println(" " + getPrice());
    }
}
