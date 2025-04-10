package designpattern.state;

public class NoQuarterState implements State {
    GumballMachine gumballMachine;

    public NoQuarterState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    public void insertQuarter() {
        System.out.println("您投入了25分钱");
        gumballMachine.setState(gumballMachine.getHasQuarterState());
    }

    @Override
    public void ejectQuarter() {
        System.out.println("您未投入钱，不能退回");
    }

    @Override
    public void turnCrank() {
        System.out.println("需要先投入钱");
    }

    @Override
    public void dispense() {
        // System.out.println("ERROR【NO_QUARTER】");
    }
}
