package example.lifecycle;

public class LifecycleManager {
    private Lifecycle lifecycle;

    public LifecycleManager(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void initAndStart() {
        lifecycle.initialize();
        lifecycle.start();
    }

    public void stopAndDestroy() {
        lifecycle.stop();
        lifecycle.destroy();
    }
}