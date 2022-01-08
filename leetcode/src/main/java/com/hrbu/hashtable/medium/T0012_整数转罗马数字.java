package com.hrbu.hashtable.medium;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class T0012_整数转罗马数字 {
    // 初始化对应关系
    Map<Integer, String> hashMap = new HashMap(){{
        put(1000,"M" );
        put(900 ,"CM");
        put(500 ,"D" );
        put(400 ,"CD");
        put(100 ,"C" );
        put(90  ,"XC");
        put(50  ,"L" );
        put(40  ,"XL");
        put(10  ,"X" );
        put(9   ,"IX");
        put(5   ,"V" );
        put(4   ,"IV");
        put(1   ,"I" );
    }};
    int[] key = {1000,900 ,500 ,400 ,100 ,90  ,50  ,40  ,10  ,9   ,5   ,4, 1};
    String[] value = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};

    @Test
    public void Main() {
        int[] nums = { 3, 4, 9, 58, 1994 };

//        System.out.println(intToRoman(nums[0]));
        for (int num : nums) {
            System.out.println(intToRoman(num));
        }
    }

    public String intToRoman(int num) {
        StringBuilder sb = new StringBuilder();

        while (num != 0) {
            for (int i = 0; i < key.length; ) {
                if (num >= key[i]) {
                    num -= key[i];
                    sb.append(value[i]);
                } else {
                    i++ ;
                }

            }
        }

        return String.valueOf(sb);
    }
}
