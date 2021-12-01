package com.hrbu.hashtable;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/*
罗马数字包含以下七种字符: I， V， X， L，C，D 和 M。

字符          数值
I             1
V             5
X             10
L             50
C             100
D             500
M             1000
例如， 罗马数字 2 写做 II ，即为两个并列的 1 。12 写做 XII ，即为 X + II 。 27 写做  XXVII, 即为 XX + V + II 。

通常情况下，罗马数字中小的数字在大的数字的右边。但也存在特例，例如 4 不写做 IIII，而是 IV。数字 1 在数字 5 的左边，所表示的数等于大数 5 减小数 1 得到的数值 4 。同样地，数字 9 表示为 IX。这个特殊的规则只适用于以下六种情况：

I 可以放在 V (5) 和 X (10) 的左边，来表示 4 和 9。
X 可以放在 L (50) 和 C (100) 的左边，来表示 40 和 90。 
C 可以放在 D (500) 和 M (1000) 的左边，来表示 400 和 900。
给定一个罗马数字，将其转换成整数。

示例 1:

输入: s = "III"
输出: 3
示例 2:

输入: s = "IV"
输出: 4
示例 3:

输入: s = "IX"
输出: 9
示例 4:

输入: s = "LVIII"
输出: 58
解释: L = 50, V= 5, III = 3.
示例 5:

输入: s = "MCMXCIV"
输出: 1994
解释: M = 1000, CM = 900, XC = 90, IV = 4.
 

提示：

1 <= s.length <= 15
s 仅含字符 ('I', 'V', 'X', 'L', 'C', 'D', 'M')
题目数据保证 s 是一个有效的罗马数字，且表示整数在范围 [1, 3999] 内
题目所给测试用例皆符合罗马数字书写规则，不会出现跨位等情况。
IL 和 IM 这样的例子并不符合题目要求，49 应该写作 XLIX，999 应该写作 CMXCIX 。
关于罗马数字的详尽书写规则，可以参考 罗马数字 - Mathematics 。

来源：力扣（LeetCode）
链接：https://leetcode-cn.com/problems/roman-to-integer
著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class T0013_罗马数字转整数 {

    // 初始化对应关系
    Map<Character, Integer> hashMap = new HashMap<Character, Integer> (){{
        put('I',1);
        put('V',5);
        put('X',10);
        put('L',50);
        put('C',100);
        put('D',500);
        put('M',1000);
    }};

    @Test
    public void Main() {
        String[] strs = new String[]{"III", "IV", "IX", "LVIII", "MCMXCIV"};

        for (String str : strs) {
            System.out.println(romanToInt2(str));
        }
    }

    /**
     * 1 先初始化设置对应关系，然后特殊情况进行处理
     */
    public int romanToInt(String s) {
        char[] chars = s.toCharArray();

        // 用来记录值
        int sum = 0;
        for (int i = 0; i < chars.length - 1 ; i++) {
            switch ( chars[i] ) {
                case 'I' :
                    if ( 'V' == chars[i+1] || 'X' == chars[i+1] )
                        sum -= 1;
                    else
                        sum += 1;
                    break;
                case 'X' :
                    if ( 'L' == chars[i+1] || 'C' == chars[i+1] )
                        sum -= 10;
                    else
                        sum += 10;
                    break;
                case 'C' :
                    if ( 'D' == chars[i+1] || 'M' == chars[i+1] )
                        sum -= 100;
                    else
                        sum += 100;
                    break;
                default  : sum += hashMap.getOrDefault(chars[i], 0);  break;
            }
        }

        return sum += hashMap.get(chars[chars.length - 1]);
    }


    /**
     * 2 1的改进，特殊情况使用同一个判断，即 num[i] < num[i+1]时，计算变为相减
     */
    public int romanToInt2(String s) {
        int len = s.length();

        // 用来记录值
        int sum = 0;
        for (int i = 0; i < len ; i++) {
            int num = hashMap.getOrDefault(s.charAt(i), 0);
            if ( i < len - 1 && hashMap.get(s.charAt(i)) < hashMap.get(s.charAt(i+1)) ) {
                sum -= num;
            } else {
                sum += num;
            }
        }

        return sum;
    }
}
