package example.lifecycle;

public class Main {
    public static void main(String[] args) {
        MyComponent component = new MyComponent();
        LifecycleManager manager = new LifecycleManager(component);
        manager.initAndStart(); // 初始化并启动组件
        // 执行其他业务逻辑...
        manager.stopAndDestroy(); // 停止并销毁组件
    }
}