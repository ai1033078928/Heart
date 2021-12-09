package com.hrbu.hashtable;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

/*
编写一个算法来判断一个数 n 是不是快乐数。

「快乐数」定义为：

对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和。
然后重复这个过程直到这个数变为 1，也可能是 无限循环 但始终变不到 1。
如果 可以变为  1，那么这个数就是快乐数。
如果 n 是快乐数就返回 true ；不是，则返回 false 。

 

示例 1：

输入：n = 19
输出：true
解释：
12 + 92 = 82
82 + 22 = 68
62 + 82 = 100
12 + 02 + 02 = 1
示例 2：

输入：n = 2
输出：false
 

提示：

1 <= n <= 231 - 1

来源：力扣（LeetCode）
链接：https://leetcode-cn.com/problems/happy-number
著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
*/
public class T0202_快乐数_0 {

    @Test
    public void Main() {

        System.out.println(isHappy(19));
        System.out.println(isHappy(2));

    }

    public boolean isHappy(int n) {
        // 保存中间结果
        HashSet<Integer> set = new HashSet<>();
        int num = n;
        while (true) {
            num = num_pow1(num);
            // 各个位上平方和等于1
            if (num == 1) return true;
            // 若情况出现重复，说明开始出现循环
            if (set.contains(num)) {
                break;
            }
            // 将中间结果添加到哈希集合
            set.add(num);
        }

        return false;
    }

    /**
     * 传入数字，计算各个位上数字的平方和(数字计算)[内存待优化]
     * @param num
     * @return
     */
    public int num_pow1(int num) {
        String str = String.valueOf(num);
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            // 取出当前位的值
            int cur = i == 0? num % 10 : (int) (num / Math.pow(10, i) % 10);
            sum += Math.pow(cur, 2);
        }
        return sum;
    }

    /**
     * 传入数字，计算各个位上数字的平方和(数字转字符) [内存时间待优化]
     * @param num
     * @return
     */
    public int num_pow2(int num) {
        String str = String.valueOf(num);
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum += Math.pow(Integer.parseInt(String.valueOf(str.charAt(i))), 2);
        }
        return sum;
    }


    @Test
    public void test() {
        System.out.println((int)'1');
        System.out.println("Asdadasdas".length());
        System.out.println(Math.pow(123 % Math.pow(10, 0), 2));
        System.out.println(Math.pow(123 % Math.pow(10, 1), 2));
        System.out.println(Math.pow(123 % Math.pow(10, 2), 2));
    }
}


