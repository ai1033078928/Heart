package com.hrbu.designpattern.proxy;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 步骤二、制作远程实现
 * 1. 实现远程接口 xxxRemote
 * 2. 继承 UnicastRemoteObject（使对象继承超类的远程功能）
 * 3. 创建无参构造器，并抛出异常
 * 4. 用 RMI Registry 注册此服务
 *
 *
 * 步骤三、产生 Stub 和 Skeleton（`rmic 包名.MyRemoteImpl`）
 * 步骤四、执行 remiregistry（保证启动目录可以访问类，最简单做法 classes 下启动）（rmiregistry）
 * 步骤五、启动服务（`java 包名.MyRemoteImpl`）
 */
public class MyRemoteImpl extends UnicastRemoteObject implements MyRemote {
    protected MyRemoteImpl() throws RemoteException {}

    @Override
    public String sayHello() throws RemoteException {
        return "Hello World!!!!!!!!!!!!!!";
    }

    public static void main(String[] args) {
        try {
            MyRemoteImpl service = new MyRemoteImpl();
            // 先保证 RMI Registry 正在运行，然后注册服务（stub）
            Naming.bind("RemoteHello", service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
