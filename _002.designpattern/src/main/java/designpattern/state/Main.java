package designpattern.state;

public class Main {
    public static void main(String[] args) {
        GumballMachine gumballMachine = new GumballMachine(100);
        /*System.out.println(gumballMachine);
        gumballMachine.insertQuarter();  // 投入硬币
        gumballMachine.turnCrank();     // 转动曲柄
        System.out.println(gumballMachine);
        gumballMachine.insertQuarter(); // 投入硬币
        gumballMachine.ejectQuarter();  // 退回硬币
        gumballMachine.turnCrank();     // 转动曲柄
        System.out.println(gumballMachine);
        gumballMachine.insertQuarter(); // 投入硬币
        gumballMachine.turnCrank();     // 转动曲柄
        gumballMachine.insertQuarter(); // 投入硬币
        gumballMachine.turnCrank();     // 转动曲柄
        gumballMachine.ejectQuarter();  // 退回硬币
        System.out.println(gumballMachine);
        gumballMachine.insertQuarter(); // 投入硬币
        gumballMachine.insertQuarter(); // 投入硬币
        gumballMachine.turnCrank();     // 转动曲柄
        gumballMachine.insertQuarter(); // 投入硬币
        gumballMachine.turnCrank();     // 转动曲柄
        gumballMachine.insertQuarter(); // 投入硬币
        gumballMachine.turnCrank();     // 转动曲柄
        System.out.println(gumballMachine);*/

        for (int i = 0; i < 50; i++) {
            gumballMachine.insertQuarter();
            gumballMachine.turnCrank();
            System.out.println(gumballMachine);
        }
    }
}
