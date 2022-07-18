package com.hrbu.designpattern.command;

public interface Command {
    public void execute();
}

// 电灯类
class Light {
    public void on() {
        System.out.println("灯被打开");
    }
}

// 打开电灯
class LightOnCommand implements Command {
    Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }
}