
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
      private float tempreature;
      private float humidity;
      private float pressure;
      private List<Observer> listObject;
  
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


#### 使用内置观察者模式实现主题推送数据

<code-group>
  <code-block title="主题（Subject）" active>
  ```java
  // 1. 导入java内置包
  import java.util.Observable;
  
  // 2.继承主题类
  public class Weather extends Observable {
  
      // 3. 不需要追踪观察者，父类已经实现
      // 4. 不需要构造器初始化观察者集合
      public Weather() {}
  
      public void setMeasurements(Object arg) {
          // 5. 调用 notifyObservers() 之前，调用 setChanged() 设置状态（这一步必须做，因为 changed 默认为 false ）
          setChanged();
          // 注意：传入数据参数，观察模式为主题推送；若没有传送数据对象，则为观察者拉取？
          notifyObservers(arg);
      }
  }
  ```
  </code-block>
  
  <code-block title="观察者（Observer）">
  ```java
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
          List list = (List) arg;
          this.tempreature = (float) list.get(0);
          this.humidity = (float) list.get(1);
          this.pressure = (float) list.get(2);
          display();
      }
  }
  ```
  </code-block>
  
  <code-block title="启动类">
  ```java
  import java.util.Arrays;
  import java.util.Observer;
  
  public class Main {
      public static void main(String[] args) {
          // 使用内置观察者模式实现 推送数据
          Weather weather = new Weather();
          Observer thirdPartyDisplay = new ThirdPartyDisplay(weather);
          Observer thirdPartyDisplay2 = new ThirdPartyDisplay(weather);
          weather.setMeasurements(Arrays.asList(25f, 25f, 25f));
      }
  }
  ```
  </code-block>
</code-group>

#### 使用内置观察者模式实现观察者拉取数据

<code-group>
  <code-block title="主题（Subject）" active>
  ```java
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
  ```
  </code-block>
  
  <code-block title="观察者（Observer）">
  ```java
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
  ```
  </code-block>
  
  <code-block title="启动类">
  ```java
  import java.util.Observer;
  
  public class Main {
      public static void main(String[] args) {
          // 使用内置观察者模式实现观察者拉取数据
          Weather weather = new Weather();
          Observer thirdPartyDisplay1 = new ThirdPartyDisplay(weather);
          Observer thirdPartyDisplay2 = new ThirdPartyDisplay(weather);
          weather.setMeasurements(25f, 25f, 25f);
      }
  }
  ```
  </code-block>
</code-group>