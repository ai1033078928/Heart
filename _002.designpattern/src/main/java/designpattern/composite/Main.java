package designpattern.composite;

public class Main {
    public static void main(String[] args) {
        MenuComponent pancakeHouseMenu = new Menu("煎饼", "早餐");
        MenuComponent dinerMenu = new Menu("餐厅", "午餐");
        MenuComponent cafeMenu = new Menu("咖啡", "晚餐");
        MenuComponent dessertMenu = new Menu("甜点", "");
        dessertMenu.add(new MenuItem("蛋糕", "蛋糕", true, 0));
        dinerMenu.add(dessertMenu);
        dinerMenu.add(new MenuItem("炒胡萝卜", "炒胡萝卜", true, 22));
        dinerMenu.add(new MenuItem("炒鸡蛋", "炒鸡蛋", false, 23));

        MenuComponent menuRoot = new Menu("所有菜单", "根节点");
        menuRoot.add(pancakeHouseMenu);
        menuRoot.add(dinerMenu);
        menuRoot.add(cafeMenu);

        Waitress waitress = new Waitress(menuRoot);
        waitress.printMenu();
    }
}
