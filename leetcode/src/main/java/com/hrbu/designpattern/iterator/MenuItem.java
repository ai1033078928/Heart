package com.hrbu.designpattern.iterator;

public class MenuItem {
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVegetaian() {
        return vegetaian;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", vegetaian=" + vegetaian +
                ", price=" + price +
                '}';
    }
}
