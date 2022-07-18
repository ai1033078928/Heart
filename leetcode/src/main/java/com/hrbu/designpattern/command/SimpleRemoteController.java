package com.hrbu.designpattern.command;

public class SimpleRemoteController {
    // 插槽持有命令，控制着一个装置
    Command slot;

    /**
     * 用来设置插槽控制命令，可以通过多次调用改变行为
     */
    public void setCommand(Command command) {
        slot = command;
    }

    /**
     * 按下按钮，命令衔接插槽，调用 execute() 方法
     */
    public void buttonWasPressed() {
        slot.execute();
    }
}
