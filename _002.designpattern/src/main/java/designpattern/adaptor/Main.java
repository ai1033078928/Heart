package designpattern.adaptor;

public class Main {
    public static void main(String[] args) {
        MallardDuck duck = new MallardDuck();   // 创建一只鸭子
        WildTurkey turkey = new WildTurkey();   // 创建一只火鸡

        Main main = new Main();
        main.testDuck(duck);
//        main.testDuck(turkey);
        TurkeyAdaptor turkeyAdaptor = new TurkeyAdaptor(turkey);
        main.testDuck(turkeyAdaptor);
    }

    /**
     * 鸭子测试类
     * @param duck
     */
    public void testDuck(Duck duck) {
        duck.quack();
        duck.fly();
    }
}
