package designpattern.proxy;

import designpattern.state.State;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author ahb
 * @date 2022/9/20 20:33
 */
public interface GumballMachineRemote extends Remote {
    /**
     * 返回值都需要序列化
     */
    String getLocation() throws RemoteException;
    State getState() throws RemoteException;
    int getCount() throws RemoteException;
}
