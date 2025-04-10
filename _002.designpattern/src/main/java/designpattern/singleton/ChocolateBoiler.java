package designpattern.singleton;

public class ChocolateBoiler {
    private boolean empty;   // 锅炉是否是空的
    private boolean boiled;  // 是否煮沸过
    private static ChocolateBoiler chocolateBoiler;

    private ChocolateBoiler() {
        empty = true;
        boiled = false;
    }

    public synchronized static ChocolateBoiler getInstance() {
        if (null == chocolateBoiler) {
            chocolateBoiler =  new ChocolateBoiler();
        }
        return chocolateBoiler;
    }

    public void  fill() {
        if (isEmpty()) {
            empty = false;
            boiled = false;
            // 在锅炉里填充巧克力和牛奶的混合物
        }
    }

    private boolean isEmpty() {
        return empty;
    }
}
