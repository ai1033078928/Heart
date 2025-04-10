package designpattern.proxy;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 步骤一、制作远程接口
 * 1. 继承 Remote
 * 2. 所有方法都会抛出 RemoteException 异常
 * 3. 变量和返回值是元语（primitive）或者序列化（Serializable）的，因为需要进行网络 IO
 */
public interface MyRemote extends Remote {
    public String sayHello() throws RemoteException;
}
