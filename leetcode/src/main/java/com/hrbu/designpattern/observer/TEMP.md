
#### 实现观察者模式（主题推送信息）

<code-group>
  <code-block title="主题（Subject）" active>
  ```java
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
  ```
  </code-block>
  
  <code-block title="观察者（Observer）">
  ```java
  public interface Observer {
      void update(float tempreature, float humidity, float pressure);
  }

  public interface DisplayElement {
      void display();
  }

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
  // 其他观察者类似
  ```
  </code-block>
  
  <code-block title="启动类">
  ```java
  public class Main {
      public static void main(String[] args) {
          WeatherData weatherData = new WeatherData();
  
          Observer currentConditionsDisplay = new CurrentConditionsDisplay(weatherData);
          Observer statisticsDisplay = new StatisticsDisplay(weatherData);
          Observer thirdPartyDisplay = new ThirdPartyDisplay(weatherData);
          Observer forecastDisplay = new ForecastDisplay(weatherData);
          weatherData.registerObserver(forecastDisplay);
          // weatherData.removeObserver(currentConditionsDisplay);
  
          weatherData.setMeasurements(22, 22, 22);
          weatherData.setMeasurements(25, 25, 25);
          weatherData.setMeasurements(30, 30, 30);
      }
  }
  ```
  </code-block>
</code-group>