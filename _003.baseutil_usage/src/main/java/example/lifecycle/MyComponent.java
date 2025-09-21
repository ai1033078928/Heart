package example.lifecycle;

public class MyComponent implements Lifecycle {
    @Override
    public void initialize() {
        System.out.println("Initializing...");
        // 初始化代码，如资源加载等
    }

    @Override
    public void start() {
        System.out.println("Starting...");
        // 启动代码，如开启线程等
    }

    @Override
    public void stop() {
        System.out.println("Stopping...");
        // 停止代码，如关闭线程等
    }

    @Override
    public void destroy() {
        System.out.println("Destroying...");
        // 销毁代码，如资源释放等
    }
}