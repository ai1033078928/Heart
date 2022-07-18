package com.hrbu.designpattern.command;

/**
 * 遥控器类
 */
public class SimpleRemoteController {
    // 插槽持有命令，控制着一个装置
    // 用来记录 7 个接口的命令
    Command[] onCommands;
    Command[] offCommands;

    public SimpleRemoteController() {
        // 实例化并初始化两个开关数组
        this.onCommands = new Command[7];
        this.offCommands = new Command[7];

        NoCommand noCommand = new NoCommand();
        for (int i = 0; i < 7; i++) {
            onCommands[i] = noCommand;
            offCommands[i] = noCommand;
        }
    }

    /**
     * @param slot   插槽位置
     * @param onCommand   开命令
     * @param offCommand  关命令
     * 用来设置插槽控制命令，可以通过多次调用改变行为
     */
    public void setCommand(int slot, Command onCommand, Command offCommand) {
        onCommands[slot] = onCommand;
        offCommands[slot] = offCommand;
    }

    /**
     * 按下开启按钮，命令衔接插槽，调用 execute() 方法
     */
    public void onButtonWasPressed(int slot) {
        onCommands[slot].execute();
    }

    public void offButtonWasPressed(int slot) {
        offCommands[slot].execute();
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < 7; i++) {
            stringBuffer.append("装置：")
                    .append(i)
                    .append("\t开启类：")
                    .append(onCommands[i].getClass().getName())
                    .append("\t关闭类：")
                    .append(offCommands.getClass().getName())
                    .append("\n");
        }

        return stringBuffer.toString();
    }
}
