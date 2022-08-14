package com.hrbu.designpattern.state;

public class SoldState implements State {
    GumballMachine gumballMachine;

    public SoldState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    public void insertQuarter() {
        System.out.println("请稍等，我们已经给出了糖果");
    }

    @Override
    public void ejectQuarter() {
        System.out.println("转动曲柄，已经拿到糖果，不能退钱");
    }

    @Override
    public void turnCrank() {
        System.out.println("已经拿到糖果，不能拿两次糖果");
    }

    @Override
    public void dispense() {
        gumballMachine.releaseBall();
        if (gumballMachine.getCount() > 0) {
            gumballMachine.setState(gumballMachine.getNoQuarterState());
        } else {
            System.out.println("糖果售罄");
            gumballMachine.setState(gumballMachine.getSoldOutState());
        }
    }
}
