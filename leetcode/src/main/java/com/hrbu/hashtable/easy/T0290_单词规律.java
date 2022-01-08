package com.hrbu.hashtable.easy;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/*
给定一种规律 pattern 和一个字符串 str ，判断 str 是否遵循相同的规律。

这里的 遵循 指完全匹配，例如， pattern 里的每个字母和字符串 str 中的每个非空单词之间存在着双向连接的对应规律。

示例1:

输入: pattern = "abba", str = "dog cat cat dog"
输出: true
示例 2:

输入:pattern = "abba", str = "dog cat cat fish"
输出: false
示例 3:

输入: pattern = "aaaa", str = "dog cat cat dog"
输出: false
示例 4:

输入: pattern = "abba", str = "dog dog dog dog"
输出: false
说明:
你可以假设 pattern 只包含小写字母， str 包含了由单个空格分隔的小写字母。

来源：力扣（LeetCode）
链接：https://leetcode-cn.com/problems/word-pattern
著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
*/
public class T0290_单词规律 {

    @Test
    public void Main () {
//        String pattern = "abba", str = "dog cat cat dog";
        String pattern = "abba", str = "dog cat cat fish";
        System.out.println(wordPattern(pattern, str));
    }

    public boolean wordPattern(String pattern, String s) {
        Map hashMap = new HashMap<String, Character>();

        String[] split = s.split(" ");
        if (split.length != pattern.length()) return false;

        for (int i = 0; i < split.length; i++) {
            String str = split[i];
            char ch = pattern.charAt(i);

            if  (hashMap.containsKey(str)) {
                if (hashMap.get(split[i]).equals(ch)) continue; else return false;
            } else {
                if (hashMap.containsValue(ch)) return false;
                hashMap.put(str, ch);
            }
        }

        return true;
    }


    /**
     * @TODO put方法特性，如果存在相同的key，覆盖并返回前一个value
     * @param pattern
     * @param s
     * @return
     */
        public boolean wordPattern3(String pattern, String s) {
            Map hashMap = new HashMap<String, Integer>();

            String[] split = s.split(" ");
            if (split.length != pattern.length()) return false;

            // 这里一定要用Integer,使用int的话常量池会导致结果错误
            for (Integer i = 0; i < split.length; i++) {
                if ( hashMap.put(pattern.charAt(i), i) != hashMap.put(split[i], i) ) return false;
            }

            return true;
        }


    /**
     * 官方解法
     */
    public boolean wordPattern2(String pattern, String str) {
        Map<String, Character> str2ch = new HashMap<String, Character>();
        Map<Character, String> ch2str = new HashMap<Character, String>();
        int m = str.length();
        int i = 0;
        for (int p = 0; p < pattern.length(); ++p) {
            char ch = pattern.charAt(p);
            if (i >= m) {
                return false;
            }
            int j = i;
            while (j < m && str.charAt(j) != ' ') {
                j++;
            }
            String tmp = str.substring(i, j);
            if (str2ch.containsKey(tmp) && str2ch.get(tmp) != ch) {
                return false;
            }
            if (ch2str.containsKey(ch) && !tmp.equals(ch2str.get(ch))) {
                return false;
            }
            str2ch.put(tmp, ch);
            ch2str.put(ch, tmp);
            i = j + 1;
        }
        return i >= m;
    }

}
