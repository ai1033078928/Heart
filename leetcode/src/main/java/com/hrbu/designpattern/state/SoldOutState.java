package com.hrbu.designpattern.state;

public class SoldOutState implements State {
    GumballMachine gumballMachine;

    public SoldOutState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    public void insertQuarter() {
        System.out.println("您不能投入钱，糖果已经售罄");
    }

    @Override
    public void ejectQuarter() {
        System.out.println("未收到钱，不能退回");  // 糖果售罄，不能投入钱，自然不能退回
    }

    @Override
    public void turnCrank() {
        System.out.println("sorry，糖果已售罄");
    }

    @Override
    public void dispense() {
        // System.out.println("ERROR【SOLD_OUT】");
    }
}
