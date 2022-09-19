package com.hrbu.designpattern.state;

public class GumballMonitor {
    GumballMachine gumballMachine;

    public GumballMonitor(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    public void report () {
        System.out.println("位置：" + gumballMachine.getLocation());
        System.out.println("状态：" + gumballMachine.getState());
        System.out.println("剩余糖果数：" + gumballMachine.getCount());
    }
}
