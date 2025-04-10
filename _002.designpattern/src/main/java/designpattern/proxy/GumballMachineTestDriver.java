package designpattern.proxy;

import designpattern.state.GumballMachine;
import designpattern.state.GumballMonitor;

import java.rmi.Naming;

/**
 * @author ahb
 * @date 2022/9/20 20:27
 */
public class GumballMachineTestDriver {
    public static void main(String[] args) {
        int count;
        GumballMachine gumballMachine = null;

        if (args.length < 2) {
            System.out.println("参数错误，退出");
            System.exit(1);
        }

        try {
            count = Integer.parseInt(args[1]);
            // 保证 GumballMachine 实现 Remote 接口（作为服务端）
            gumballMachine = new GumballMachine(count, args[0]);

            Naming.bind("//" + args[0] + "/gumballmachine", gumballMachine);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        GumballMonitor gumballMonitor = new GumballMonitor(gumballMachine);

        for (int i = 0; i < 6; i++) {
            gumballMachine.insertQuarter();
            gumballMachine.turnCrank();
            gumballMonitor.report();
            System.out.println();
        }

    }
}
