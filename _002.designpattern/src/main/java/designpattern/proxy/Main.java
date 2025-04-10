package designpattern.proxy;

import designpattern.state.GumballMachine;
import designpattern.state.GumballMonitor;

public class Main {
    public static void main(String[] args) {
        int count = 0;

        if (args.length < 2) {
            System.out.println("参数错误，退出");
            System.exit(1);
        }

        count = Integer.parseInt(args[0]);
        GumballMachine gumballMachine = new GumballMachine(count, args[1]);
        GumballMonitor gumballMonitor = new GumballMonitor(gumballMachine);

        for (int i = 0; i < 6; i++) {
            gumballMachine.insertQuarter();
            gumballMachine.turnCrank();
            gumballMonitor.report();
            System.out.println();
        }

    }
}
