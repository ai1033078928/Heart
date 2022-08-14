package com.hrbu.designpattern.state;

public class GumballMachineBak {
    final static int SOLD_OUT = 0;      // 糖果售罄
    final static int NO_QUARTER = 1;    // 没有25分钱
    final static int HAS_QUARTER = 2;   // 有25分钱
    final static int SOLD = 3;          // 售出糖果

    // 实例变量，持有当前的状态。初始化为“糖果售罄”状态。
    int state = SOLD_OUT;
    int count = 0;  // 糖果数量

    public GumballMachineBak(int count) {
        this.count = count;
        if (count > 0) state = NO_QUARTER;
    }

    // 投入钱
    public void insertQuarter() {
        switch (state) {
            case HAS_QUARTER:       // 如果有25分钱，提示已经投过钱
                System.out.println("您已经投过钱");
                break;
            case NO_QUARTER:
                state = HAS_QUARTER;
                System.out.println("您投入了25分钱");
                break;
            case SOLD_OUT:
                System.out.println("您不能投入钱，糖果已经售罄");
                break;
            case SOLD:
                System.out.println("请稍等，我们已经给出了糖果");
                break;
        }
    }

    // 退回钱
    public void ejectQuarter() {
        switch (state) {
            case HAS_QUARTER:
                state = NO_QUARTER;
                System.out.println("退回钱");
                break;
            case NO_QUARTER:
                System.out.println("您未投入钱，不能退回");
                break;
            case SOLD_OUT:
                System.out.println("未收到钱，不能退回");  // 糖果售罄，不能投入钱，自然不能退回
                break;
            case SOLD:
                System.out.println("转动曲柄，已经拿到糖果，不能退钱");
                break;
        }
    }

    // 转动曲柄
    public void turnCrank() {
        switch (state) {
            case HAS_QUARTER:
                System.out.println("转动成功，正在发放糖果");
                state = SOLD;
                dispense();
                break;
            case NO_QUARTER:
                System.out.println("需要先投入钱");
                break;
            case SOLD_OUT:
                System.out.println("sorry，糖果已售罄");
                break;
            case SOLD:
                System.out.println("已经拿到糖果，不能拿两次糖果");
                break;
        }
    }

    // 发放糖果
    public void dispense() {
        switch (state) {
            case SOLD:
                System.out.println("一个糖果从槽里滚出来");
                count--;
                if (count == 0) {
                    state = SOLD_OUT;
                    System.out.println("糖果售罄");
                } else {
                    state = NO_QUARTER;
                }
                break;
            // 以下情况不应该发生
            case HAS_QUARTER:
                System.out.println("ERROR【HAS_QUARTER】");
                break;
            case NO_QUARTER:
                System.out.println("ERROR【NO_QUARTER】");
                break;
            case SOLD_OUT:
                System.out.println("ERROR【SOLD_OUT】");
                break;
        }
    }

    @Override
    public String toString() {
        return "GumballMachine{" +
                "state=" + state +
                ", count=" + count +
                '}';
    }
}
