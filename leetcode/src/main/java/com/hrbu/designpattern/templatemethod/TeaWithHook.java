package com.hrbu.designpattern.templatemethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 抽象类，用来作为基类，其子类必须实现其操作
 */
abstract class CaffeineBeverageWithHook {
    /**
     * 模板方法被声明为 final，以免算法顺序被改变（不能被子类覆盖）
     * 此方法定义了一连串步骤，每个步骤为一个方法
     */
    final void prepareRecipe() {
        boilWater();
        brew();
        pourInCup();
        // 加上条件语句，条件成立（customerWantsCondiments() 为 true，即顾客想要调料 ）时，才调用 addCondiments() 方法
        if (customerWantsCondiments()) {
            addCondiments();
        }
    }

    /**
     * 原语操作，子类必须实现他们
     */
    abstract void brew();
    abstract void addCondiments();

    void boilWater() {
        System.out.println("把水煮沸");
    }
    void pourInCup() {
        System.out.println("倒进杯子中");
    }

    /**
     * 这就是一个钩子，子类有可能覆盖此方法
     * @return
     */
    boolean customerWantsCondiments () {
        return true;
    }

}

class CoffeeWithHook extends CaffeineBeverageWithHook {

    @Override
    public void brew() {
        System.out.println("用沸水冲泡咖啡");
    }

    @Override
    public void addCondiments() {
        System.out.println("加糖和牛奶");
    }

    /**
     * 使用钩子：覆盖钩子，提供自己的功能
     * @return
     */
    @Override
    public boolean customerWantsCondiments() {
        String answer = getUserInput();
        return "y".equalsIgnoreCase(answer);
    }

    private String getUserInput () {
        String answer = null;

        System.out.println("咖啡里要加糖和牛奶吗？（y/n）");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            answer = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (null == answer) answer = "n";
        return answer;
    }
}

public class TeaWithHook extends CaffeineBeverageWithHook {

    @Override
    public void brew() {
        System.out.println("用沸水浸泡茶叶");
    }

    @Override
    public void addCondiments() {
        System.out.println("加柠檬");
    }
}