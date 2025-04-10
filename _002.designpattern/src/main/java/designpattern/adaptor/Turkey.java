package designpattern.adaptor;

/**
 * 火鸡接口
 */
public interface Turkey {
    public void gobble();   // 火鸡咯咯叫
    public void fly();      // 火鸡飞
}

/**
 * 火鸡实现
 */
class WildTurkey implements Turkey {

    @Override
    public void gobble() {
        System.out.println("火鸡咯咯叫");
    }

    @Override
    public void fly() {
        System.out.println("火鸡飞行");
    }
}
