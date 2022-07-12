package com.hrbu.designpattern.observer;

public class Main {
    public static void main(String[] args) {
        WeatherData weatherData = new WeatherData();

        Observer currentConditionsDisplay = new CurrentConditionsDisplay(weatherData);
        Observer statisticsDisplay = new StatisticsDisplay(weatherData);
        Observer thirdPartyDisplay = new ThirdPartyDisplay(weatherData);
        Observer forecastDisplay = new ForecastDisplay(weatherData);
        weatherData.registerObserver(forecastDisplay);
        weatherData.removeObserver(currentConditionsDisplay);

        weatherData.setMeasurements(22, 22, 22);
        weatherData.setMeasurements(25, 25, 25);
        weatherData.setMeasurements(30, 30, 30);
    }
}
