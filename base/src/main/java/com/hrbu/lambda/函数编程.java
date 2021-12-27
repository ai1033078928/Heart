package com.hrbu.lambda;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 函数编程(lambda表达式)
 *
 * lambda表达式仅能放入如下代码: 预定义使用了 @Functional 注释的函数式接口，自带一个抽象函数的方法，或者SAM(Single Abstract Method 单个抽象方法)类型
 */
public class 函数编程 {

    // lambda表达式有个限制，那就是只能引用 final 或 final 局部变量，这就是说不能在lambda内部修改定义在域外的变量。
    private final List<String> list = new ArrayList<String>() {
        {
            add("aaa");
            add("bbb");
            add("ccc");
            add("ddd");
        }
    };
    private final List<Person> list2 = new ArrayList<Person>() {
        {
            add(new Person("aaa"));
            add(new Person("bbb"));
            add(new Person("ccc"));
            add(new Person("ddd"));
        }
    };
    /**
     * lambda表达式内可以使用方法引用，仅当该方法不修改lambda表达式提供的参数。
     */
    @Test
    public void Test() {
        // 遍历list
        list.forEach((String n) -> System.out.print(n + " "));
        System.out.println();
        // 简写
        list.forEach(n -> System.out.println(n));
        System.out.println();
        // 使用方法引用
        list.forEach(System.out::println);
    }


    /**
     * 分类：惰性求值、及早求值
     */
    @Test
    public void Test2() {

        // filter只是描述了Stream，没有产生新的集合
        list2.stream().filter(f -> f.getName().equals("p1"));
        // collect最终会从Stream产生新值，拥有终止操作。
        List<Person> list3 = list2.stream().filter(f -> f.getName().equals("p1")).collect(Collectors.toList());


    }

}

class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}