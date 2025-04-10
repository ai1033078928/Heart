package designpattern.command;

/**
 * 命令模式的客户
 */
public class Main {
    public static void main(String[] args) {
        Light light1 = new Light("厨房灯");
        Light light2 = new Light("卧室灯");
        Stereo stereo = new Stereo();
        LightOnCommand light1OnCommand = new LightOnCommand(light1);
        LightOnCommand light2OnCommand = new LightOnCommand(light2);
        LightOffCommand light1OffCommand = new LightOffCommand(light1);
        LightOffCommand light2OffCommand = new LightOffCommand(light2);
        StereoOnWithCDCommand stereoOnWithCDCommand = new StereoOnWithCDCommand(stereo);
        StereoOffCommand stereoOffCommand = new StereoOffCommand(stereo);

        SimpleRemoteController simpleRemoteController = new SimpleRemoteController();
        simpleRemoteController.setCommand(1, light1OnCommand, light1OffCommand);
        simpleRemoteController.setCommand(2, light2OnCommand, light2OffCommand);
        simpleRemoteController.setCommand(3, stereoOnWithCDCommand, stereoOffCommand);

        System.out.println(simpleRemoteController.toString());

        simpleRemoteController.onButtonWasPressed(1);
        simpleRemoteController.onButtonWasPressed(2);
        simpleRemoteController.onButtonWasPressed(3);
        simpleRemoteController.offButtonWasPressed(3);
    }
}
