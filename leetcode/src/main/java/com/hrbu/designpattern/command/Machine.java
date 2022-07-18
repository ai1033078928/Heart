package com.hrbu.designpattern.command;

/**
 * 各厂商装置类
 */
public interface Machine {
}

// 电灯类
class Light {
    String name;
    public Light(String name) {
        this.name = name;
    }

    public void on() {
        System.out.println(name + "被打开");
    }
    public void off() {
        System.out.println(name + "被关闭");
    }
}

/**
 * 音响类
 */
class Stereo {
    public void on() {
        System.out.println("打开音响");
    }

    public void setCD() {
        System.out.println("播放CD");
    }

    public void setVolume(int val) {
        System.out.println("设置音量" + val);
    }

    public void off() {
        System.out.println("关闭音响");
    }
}