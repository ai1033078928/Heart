package com.aihb.other;

import com.google.common.collect.ArrayListMultimap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OperateMultimapUtils {

    public static void operateMultimap(List<GirlVo> list) {
        /**1.创建ArrayListMultimap*/
        ArrayListMultimap<String, GirlVo> girlMultiMap = ArrayListMultimap.create();
        /**2.将cupSize相同的对象归类,ArrayListMultimap会放入同一集合中*/
        for (GirlVo girlVo : list) {
            girlMultiMap.put(girlVo.getCupSize(), girlVo);
        }
        /**3.变量归类后对象,并输出*/
        for (Map.Entry<String, Collection<GirlVo>> entryOfGirl : girlMultiMap.asMap().entrySet()) {
            String cupSize = entryOfGirl.getKey();
            Collection<GirlVo> collection = entryOfGirl.getValue();
            System.out.println(cupSize +"罩杯的女孩如下: ");
            for (GirlVo girlVo : collection) {
                System.out.println(girlVo.toString());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("测试开始......");
        List<GirlVo> list = getData();
        operateMultimap(list);
        System.out.println("测试结束......");
    }

    public static List<GirlVo> getData(){

        GirlVo girlVo1 = new GirlVo();
        girlVo1.setGirlId("1001");
        girlVo1.setGirlName("张一");
        girlVo1.setAge(27);
        girlVo1.setHeight(169);
        girlVo1.setCupSize("F");

        GirlVo girlVo2 = new GirlVo();
        girlVo2.setGirlId("1002");
        girlVo2.setGirlName("王二");
        girlVo2.setAge(29);
        girlVo2.setHeight(175);
        girlVo2.setCupSize("B");

        GirlVo girlVo3 = new GirlVo();
        girlVo3.setGirlId("1003");
        girlVo3.setGirlName("刘五");
        girlVo3.setAge(24);
        girlVo3.setHeight(160);
        girlVo3.setCupSize("B");

        GirlVo girlVo4 = new GirlVo();
        girlVo4.setGirlId("1004");
        girlVo4.setGirlName("陈六");
        girlVo4.setAge(21);
        girlVo4.setHeight(168);
        girlVo4.setCupSize("C");

        GirlVo girlVo5 = new GirlVo();
        girlVo5.setGirlId("1005");
        girlVo5.setGirlName("赵七");
        girlVo5.setAge(25);
        girlVo5.setHeight(169);
        girlVo5.setCupSize("C");

        GirlVo girlVo6 = new GirlVo();
        girlVo6.setGirlId("1006");
        girlVo6.setGirlName("张八");
        girlVo6.setAge(26);
        girlVo6.setHeight(165);
        girlVo6.setCupSize("A");

        List<GirlVo> list = new ArrayList();
        list.add(girlVo1);
        list.add(girlVo2);
        list.add(girlVo3);
        list.add(girlVo4);
        list.add(girlVo5);
        list.add(girlVo6);
        return list;
    }

}

class GirlVo implements Serializable {

    private static final long serialVersionUID = -8545755852948661858L;

    /**ID号*/
    String girlId;
    /**姓名*/
    String girlName;
    /**年龄*/
    long age;
    /**身高*/
    double height;
    /**罩杯*/
    String cupSize;
    public String getGirlId() {
        return girlId;
    }
    public void setGirlId(String girlId) {
        this.girlId = girlId;
    }
    public String getGirlName() {
        return girlName;
    }
    public void setGirlName(String girlName) {
        this.girlName = girlName;
    }
    public long getAge() {
        return age;
    }
    public void setAge(long age) {
        this.age = age;
    }
    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }
    public String getCupSize() {
        return cupSize;
    }
    public void setCupSize(String cupSize) {
        this.cupSize = cupSize;
    }

    @Override
    public String toString() {

        return "GirlVo [girlId=" + girlId
                +",girlName=" + girlName
                +",age=" + age
                +",height=" + height
                +",cupSize=" + cupSize
                +"]";
    }
}
