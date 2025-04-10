package designpattern.observer;

// 1. 导入java内置包
import java.util.Observable;

// 2.继承主题类
public class Weather extends Observable {

    private float tempreature;
    private float humidity;
    private float pressure;

    public float getTempreature() {
        return tempreature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

    // 3. 不需要追踪观察者，父类已经实现
    // 4. 不需要构造器初始化观察者集合
    public Weather() {}

    public void setMeasurements(float v, float v1, float v2) {
        this.tempreature = v;
        this.humidity = v1;
        this.pressure = v2;
        measurementsChanged();
    }
    public void measurementsChanged() {
        // 5. 调用 notifyObservers() 之前，调用 setChanged() 设置状态（这一步必须做，因为 changed 默认为 false ）
        setChanged();
        // 注意：没有传送数据对象，则为观察者拉取
        notifyObservers();
    }

}
