package example.lifecycle;

public interface Lifecycle {
    void initialize();
    void start();
    void stop();
    void destroy();
}
