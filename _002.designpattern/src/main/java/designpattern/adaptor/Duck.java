package designpattern.adaptor;

// 鸭子接口
public interface Duck {
    public void quack();
    public void fly();
}

class MallardDuck implements Duck{

    @Override
    public void quack() {
        System.out.println("绿头鸭叫");
    }

    @Override
    public void fly() {
        System.out.println("绿头鸭飞");
    }
}
