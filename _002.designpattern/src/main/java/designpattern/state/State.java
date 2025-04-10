package designpattern.state;

import java.io.Serializable;

/**
 * @author ahb
 * @date 2022/9/20 20:33
 * 处理序列化问题：
 * 1. 继承 Serializable
 * 2. 每个状态都对象都维护一个对糖果机的引用。此时，状态对象可以调用糖果机的方法来改变糖果机的状态，需要加 transient 来修正
 */
public interface State extends Serializable {
    void insertQuarter();
    void ejectQuarter();
    void turnCrank();
    void dispense();
}
