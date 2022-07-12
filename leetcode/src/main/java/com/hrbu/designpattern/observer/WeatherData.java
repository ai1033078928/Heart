package com.hrbu.designpattern.observer;

import java.util.ArrayList;
import java.util.List;

interface Subject{
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}

public class WeatherData implements Subject {
    float tempreature;
    float humidity;
    float pressure;
    List<Observer> listObject;

    public WeatherData() {
        // 创建主题对象时，实例化存储观察者的集合
        listObject = new ArrayList<>();
    }

    public float getTempreature() {
        return tempreature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

    @Override
    public void registerObserver(Observer observer) {
        // 观察者注册，将其加入通知队列中
        listObject.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        // 观察者取消，将其从通知队列中删除
        listObject.remove(observer);
    }

    @Override
    public void notifyObservers() {
        System.out.println("观察者数量：" + listObject.size());
        // 遍历观察者集合，发送最新观测值
        for (Observer observer : listObject) {
            observer.update(getTempreature(), getHumidity(), getPressure());
        }
    }

    public void measurementsChanged() {
        // 当从气象站获取更新观测值时，通知观察者
        notifyObservers();
    }

    public void setMeasurements(float tempreature, float humidity, float pressure) {
        this.tempreature = tempreature;
        this.humidity = humidity;
        this.pressure = pressure;
        measurementsChanged();
    }
}
