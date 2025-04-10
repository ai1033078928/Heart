package designpattern.state;

import designpattern.proxy.GumballMachineRemote;

import java.rmi.RemoteException;

/**
 * @author ahb
 * GumballMonitor 客户端
 */
public class GumballMonitor {
    // GumballMachine gumballMachine;
    GumballMachineRemote gumballMachine;

    public GumballMonitor(GumballMachineRemote gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    public void report () {
        try {
            System.out.println("位置：" + gumballMachine.getLocation());
            System.out.println("状态：" + gumballMachine.getState());
            System.out.println("剩余糖果数：" + gumballMachine.getCount());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
