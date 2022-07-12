package com.hrbu.designpattern.observer;

public class StatisticsDisplay implements DisplayElement, Observer{
    private float tempreature;
    private float humidity;
    private float pressure;
    private Subject subject;

    public StatisticsDisplay(Subject sub) {
        this.subject = sub;
        subject.registerObserver(this);
    }

    @Override
    public String toString() {
        return "StatisticsDisplay{" +
                "tempreature=" + tempreature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }

    @Override
    public void display() {
        System.out.println(toString());;
    }

    @Override
    public void update(float tempreature, float humidity, float pressure) {
        this.tempreature = tempreature;
        this.humidity = humidity;
        this.pressure = pressure;
        display();
    }
}
