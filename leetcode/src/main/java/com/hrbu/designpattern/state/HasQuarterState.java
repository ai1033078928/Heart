package com.hrbu.designpattern.state;

import java.util.Random;

public class HasQuarterState implements State {
    Random random = new Random(System.currentTimeMillis());
    GumballMachine gumballMachine;

    public HasQuarterState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    public void insertQuarter() {
        System.out.println("您已经投过钱");   // 如果有25分钱，提示已经投过钱
    }

    @Override
    public void ejectQuarter() {
        gumballMachine.setState(gumballMachine.getNoQuarterState());
        System.out.println("退回钱");
    }

    @Override
    public void turnCrank() {
        System.out.println("转动成功，正在发放糖果");
        int winner = random.nextInt(10);
        if (winner == 0 && gumballMachine.getCount() > 1) {
            // 如果赢了并且有 2 颗以上糖果的话，就进入 WinnerState 状态
            gumballMachine.setState(gumballMachine.getWinnerState());
        } else {
            gumballMachine.setState(gumballMachine.getSoldState());
        }
    }

    @Override
    public void dispense() {
        // System.out.println("ERROR【HAS_QUARTER】");
    }
}
