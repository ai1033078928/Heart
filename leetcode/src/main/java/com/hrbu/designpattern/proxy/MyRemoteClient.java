package com.hrbu.designpattern.proxy;

import java.rmi.Naming;

public class MyRemoteClient {
    public void go() {
        try {
            MyRemote serivce = (MyRemote) Naming.lookup("rmi://127.0.0.1/RemoteHello");
            String s = serivce.sayHello();
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyRemoteClient().go();
    }
}
