package com.hrbu.designpattern.observer;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ThirdPartyDisplay implements DisplayElement, Observer{
    private float tempreature;
    private float humidity;
    private float pressure;
    private Observable observable;

    public ThirdPartyDisplay(Observable weather) {
        this.observable = weather;
        this.observable.addObserver(this);
    }

    @Override
    public String toString() {
        return "ThirdPartyDisplay{" +
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
    public void update(Observable o, Object arg) {
        if (o instanceof Weather) {
            Weather weather = (Weather) o;
            this.tempreature = weather.getTempreature();
            this.humidity = weather.getHumidity();
            this.pressure = weather.getPressure();
            display();
        }
    }
}
