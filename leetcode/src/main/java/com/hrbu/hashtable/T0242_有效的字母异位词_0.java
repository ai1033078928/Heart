package com.hrbu.hashtable;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.*;

public class T0242_有效的字母异位词_0 {

    @Test
    public void Main() {
//        String str1 = "anagram", str2 = "nagaram";
        String str1 = "aa", str2 = "bb";
//        System.out.println(isAnagram(str1, str2));
//        System.out.println(isAnagram2(str1, str2));
        System.out.println(isAnagram3(str1, str2));
    }


    /**
     * 遍历两个字符串，存入map集合，最后判断字符个数相等
     */
    public boolean isAnagram(String s, String t) {

        Map hashMap1 = new HashMap<Character, Integer>();
        Map hashMap2 = new HashMap<Character, Integer>();

        if (s.length() != t.length()) return false;
        for (int i = 0; i < s.length(); i++) {
            char cur1 = s.charAt(i);
            char cur2 = t.charAt(i);

            if (hashMap1.containsKey(cur1)) {
                hashMap1.put(cur1, (Integer)hashMap1.get(cur1) + 1);
            } else {
                hashMap1.put(cur1, 1);
            }

            if (hashMap2.containsKey(cur2)) {
                hashMap2.put(cur2, (Integer)hashMap2.get(cur2) + 1);
            } else {
                hashMap2.put(cur2, 1);
            }
        }



        if (hashMap1.size() != hashMap2.size()) return false;
        for (Object obj : hashMap1.keySet()) {
            char key = (char)obj;
            if (!hashMap1.get(key).equals(hashMap2.get(key))) return false;
        }
        return true;
    }

    public boolean isAnagram3(String s, String t) {
        if (s.length() != t.length()) return false;

        Map<Character, Integer> hashMap = new HashMap();
        for (int i = 0; i < s.length(); i++) {
            Character cur1 = s.charAt(i);
            Character cur2 = t.charAt(i);
            if (hashMap.containsKey(cur1)) {
                hashMap.put(cur1, hashMap.get(cur1) + 1);
            } else {
                hashMap.put(cur1, 1);
            }
            if (hashMap.containsKey(cur2)) {
                hashMap.put(cur2, hashMap.get(cur2) - 1);
            } else {
                hashMap.put(cur2, -1);
            }
        }
        for (Object o : hashMap.keySet()) {
            if (hashMap.get(o) != 0) return false;
        }
        return true;
    }

    /**
     * 异或原理(未完成)
     */
    /*public boolean isAnagram2(String s, String t) {
        if (s.length() != t.length()) return false;

        int xor1 = 0;
        int xor2 = 0;
        for (int i = 0; i < s.length(); i++) {
            xor1 ^= Integer.valueOf(s.charAt(i));
            xor2 ^= Integer.valueOf(t.charAt(i));
        }

        if (xor1 != 0 && (xor1 ^ xor2) == 0) {
            return true;
        } else {



            if ( (xor1 ^ xor2) == 0 ) return true; else return false;
        }
    }*/

}
