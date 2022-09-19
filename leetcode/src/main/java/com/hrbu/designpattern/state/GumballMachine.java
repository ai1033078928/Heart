package com.hrbu.designpattern.state;

public class GumballMachine {
    private State soldOutState;     // 糖果售罄
    private State noQuarterState;       // 没有25分钱
    private State hasQuarterState;      // 有25分钱
    private State soldState;        // 售出糖果
    private State WinnerState;

    // 实例变量，持有当前的状态。初始化为“糖果售罄”状态。
    private State state = soldOutState;
    private int count = 0;  // 糖果数量

    private String location;

    public GumballMachine(int count, String location) {
        this.count = count;
        this.location = location;
        soldOutState = new SoldOutState(this);
        noQuarterState = new NoQuarterState(this);
        hasQuarterState = new HasQuarterState(this);
        soldState = new SoldState(this);
        WinnerState = new WinnerState(this);
        if (count > 0) state = noQuarterState;
    }

    public String getLocation() {
        return location;
    }

    public GumballMachine(int numerGumballs) {
        soldOutState = new SoldOutState(this);
        noQuarterState = new NoQuarterState(this);
        hasQuarterState = new HasQuarterState(this);
        soldState = new SoldState(this);
        WinnerState = new WinnerState(this);
        this.count = numerGumballs;
        if (numerGumballs > 0) state = noQuarterState;
    }

    public void insertQuarter() {
        state.insertQuarter();
    }

    public void ejectQuarter() {
        state.ejectQuarter();
    }

    public void turnCrank() {
        state.turnCrank();
        state.dispense();
    }

    // 发放糖果
    void releaseBall() {
        System.out.println("一个糖果从槽里滚出来");
        if (count != 0) count--;
    }

    public State getSoldOutState() {
        return soldOutState;
    }

    public State getNoQuarterState() {
        return noQuarterState;
    }

    public State getHasQuarterState() {
        return hasQuarterState;
    }

    public State getSoldState() {
        return soldState;
    }

    public State getState() {
        return state;
    }

    public State getWinnerState() {
        return WinnerState;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "GumballMachine{" +
                "state=" + state +
                ", count=" + count +
                '}';
    }
}
