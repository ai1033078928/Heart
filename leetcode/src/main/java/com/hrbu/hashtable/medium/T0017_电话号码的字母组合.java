package com.hrbu.hashtable.medium;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class T0017_电话号码的字母组合 {

        // 初始化对应关系
        Map<Character, List> hashMap = new HashMap(){{
            put('2' ,Arrays.asList("a","b","c") );
            put('3' ,Arrays.asList("d","e","f") );
            put('4' ,Arrays.asList("g","h","i") );
            put('5' ,Arrays.asList("j","k","l") );
            put('6' ,Arrays.asList("m","n","o") );
            put('7' ,Arrays.asList("p","q","r", "s") );
            put('8' ,Arrays.asList("t","u","v") );
            put('9' ,Arrays.asList("w","x", "y", "z") );
        }};

    @Test
    public void Main() {
        System.out.println(letterCombinations("23"));
    }

    public List<String> letterCombinations(String digits) {
        // 保存结果
        List<String> strList = new LinkedList<>();
        // 若为空字符串，返回空集合
        if (digits.length() == 0) return strList;

        // 做笛卡尔积
        for (int i = 0; i < digits.length(); i++) {
            List list = hashMap.get(digits.charAt(i));
            if (strList.isEmpty()) {
                strList = list;
            } else {
                strList = (List) strList.stream().flatMap(char1 -> list.stream().map(char2 -> char1 + char2)).collect(Collectors.toList());
            }
        }

        return strList;
    }


    /* ===============================================开始======================================================= */
    /**
     * @TODO 官方解：回溯 待学习
     */
    public List<String> letterCombinations2(String digits) {
        List<String> combinations = new ArrayList<String>();
        if (digits.length() == 0) {
            return combinations;
        }
        Map<Character, String> phoneMap = new HashMap<Character, String>() {{
            put('2', "abc");
            put('3', "def");
            put('4', "ghi");
            put('5', "jkl");
            put('6', "mno");
            put('7', "pqrs");
            put('8', "tuv");
            put('9', "wxyz");
        }};
        backtrack(combinations, phoneMap, digits, 0, new StringBuffer());
        return combinations;
    }

    public void backtrack(List<String> combinations, Map<Character, String> phoneMap, String digits, int index, StringBuffer combination) {
        if (index == digits.length()) {
            combinations.add(combination.toString());
        } else {
            char digit = digits.charAt(index);
            String letters = phoneMap.get(digit);
            int lettersCount = letters.length();
            for (int i = 0; i < lettersCount; i++) {
                combination.append(letters.charAt(i));
                backtrack(combinations, phoneMap, digits, index + 1, combination);
                combination.deleteCharAt(index);
            }
        }
    }
/* ===============================================结束======================================================= */

    // 集合做笛卡尔积
    public static List descartes(List... lists) {

        List tempList = new ArrayList<>();
        for (List list : lists) {
            if (tempList.isEmpty()) {
                    tempList = list;
            } else {
                    tempList = (List)tempList.stream().flatMap(item -> list.stream().map(item2 -> item + " " + item2)).collect(Collectors.toList());
            }

        }

        return tempList;

    }
}
