package designpattern.observer;

public class CurrentConditionsDisplay implements DisplayElement, Observer{
    private float tempreature;
    private float humidity;
    private float pressure;
    private Subject subject;

    public CurrentConditionsDisplay(Subject s) {
        this.subject = s;
        subject.registerObserver(this);
    }

    @Override
    public String toString() {
        return "CurrentConditionsDisplay{" +
                "tempreature=" + tempreature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }

    @Override
    public void update(float tempreature, float humidity, float pressure) {
        this.tempreature = tempreature;
        this.humidity = humidity;
        this.pressure = pressure;
        display();
    }

    @Override
    public void display() {
        System.out.println(toString());
    }
}
