package com.hrbu.designpattern.observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observer;

public class Main {
    public static void main(String[] args) {
        /*WeatherData weatherData = new WeatherData();

        Observer currentConditionsDisplay = new CurrentConditionsDisplay(weatherData);
        Observer statisticsDisplay = new StatisticsDisplay(weatherData);
        Observer thirdPartyDisplay = new ThirdPartyDisplay(weatherData);
        Observer forecastDisplay = new ForecastDisplay(weatherData);
        weatherData.registerObserver(forecastDisplay);
        weatherData.removeObserver(currentConditionsDisplay);

        weatherData.setMeasurements(22, 22, 22);
        weatherData.setMeasurements(25, 25, 25);
        weatherData.setMeasurements(30, 30, 30);*/

        // 使用内置观察者模式实现 推送数据
        /*Weather weather = new Weather();
        Observer thirdPartyDisplay = new ThirdPartyDisplay(weather);
        Observer thirdPartyDisplay2 = new ThirdPartyDisplay(weather);
        weather.setMeasurements(Arrays.asList(25f, 25f, 25f));*/

        // 使用内置观察者模式实现观察者拉取数据
        Weather weather = new Weather();
        Observer thirdPartyDisplay1 = new ThirdPartyDisplay(weather);
        Observer thirdPartyDisplay2 = new ThirdPartyDisplay(weather);
        weather.setMeasurements(25f, 25f, 25f);
    }
}
