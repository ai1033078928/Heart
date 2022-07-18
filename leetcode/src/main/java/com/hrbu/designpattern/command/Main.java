package com.hrbu.designpattern.command;

/**
 * 命令模式的客户
 */
public class Main {
    public static void main(String[] args) {
        SimpleRemoteController simpleRemoteController = new SimpleRemoteController();
        simpleRemoteController.setCommand(new LightOnCommand(new Light()));

        simpleRemoteController.buttonWasPressed();
    }
}
