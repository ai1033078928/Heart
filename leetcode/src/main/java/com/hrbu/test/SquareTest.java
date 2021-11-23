package com.hrbu.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SquareTest {
    /**
     * @BeforeClass 全局只会执行一次，而且是第一个运行
     * @Before 在测试方法运行之前运行
     * @Test 测试方法
     * @After 在测试方法运行之后允许
     * @AfterClass 全局只会执行一次，而且是最后一个运行
     * @Ignore 忽略此方法
     */
    private static Calculator calculator = new Calculator();
    private int param;
    private int result;

    @Parameterized.Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {2, 4},
                {0, 0},
                {-3, 9},
        });
    }

    //构造函数，对变量进行初始化
    public SquareTest(int param, int result){
        this.param = param;
        this.result = result;
    }

    @Test
    public void square(){
        calculator.square(param);
        Assert.assertEquals(result, calculator.getResult());
    }

}
