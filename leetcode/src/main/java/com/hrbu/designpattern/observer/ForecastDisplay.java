package com.hrbu.designpattern.observer;

public class ForecastDisplay implements DisplayElement, Observer{
    private float tempreature;
    private float humidity;
    private float pressure;
    private Subject subject;

    public ForecastDisplay(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "ForecastDisplay{" +
                "tempreature=" + tempreature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }

    @Override
    public void display() {
        System.out.println(toString());
    }

    @Override
    public void update(float tempreature, float humidity, float pressure) {
        this.tempreature = tempreature;
        this.humidity = humidity;
        this.pressure = pressure;
        display();
    }
}
