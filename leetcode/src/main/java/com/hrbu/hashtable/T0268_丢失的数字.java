package com.hrbu.hashtable;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/*
给定一个包含 [0, n] 中 n 个数的数组 nums ，找出 [0, n] 这个范围内没有出现在数组中的那个数。

 

示例 1：

输入：nums = [3,0,1]
输出：2
解释：n = 3，因为有 3 个数字，所以所有的数字都在范围 [0,3] 内。2 是丢失的数字，因为它没有出现在 nums 中。
示例 2：

输入：nums = [0,1]
输出：2
解释：n = 2，因为有 2 个数字，所以所有的数字都在范围 [0,2] 内。2 是丢失的数字，因为它没有出现在 nums 中。
示例 3：

输入：nums = [9,6,4,2,3,5,7,0,1]
输出：8
解释：n = 9，因为有 9 个数字，所以所有的数字都在范围 [0,9] 内。8 是丢失的数字，因为它没有出现在 nums 中。
示例 4：

输入：nums = [0]
输出：1
解释：n = 1，因为有 1 个数字，所以所有的数字都在范围 [0,1] 内。1 是丢失的数字，因为它没有出现在 nums 中。
 

提示：

n == nums.length
1 <= n <= 104
0 <= nums[i] <= n
nums 中的所有数字都 独一无二

来源：力扣（LeetCode）
链接：https://leetcode-cn.com/problems/missing-number
著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class T0268_丢失的数字 {
    @Test
    public void Main() {
        int[] nums = {9,6,4,2,3,5,7,0,1};
        System.out.println(missingNumber(nums));
        System.out.println(missingNumber2(nums));
        System.out.println(missingNumber3(nums));
    }

    public int missingNumber(int[] nums) {
        // 将数组另存到哈希Set
        Set hashSet = new HashSet<Integer>();
        
        for (int num : nums) {
            hashSet.add(num);
        }

        for (int i = 0; i <= nums.length; i++) {
            if (hashSet.contains(i)) continue; else return i;
        }
        return -1;
    }

    /**
     * @TODO 位运算
     * 使用异或原理   x^x=0  x^0=x
     */
    public int missingNumber2(int[] nums) {

        int xor = 0;
        int len = nums.length;
        for (int i = 0; i <= len; i++) {
            xor ^= i;
            if ( i !=  len) xor ^= nums[i];
        }
        return xor;
    }

    /**
     * @TODO 数学 高斯求和
     * （0-n的和）-（数组之和）
     */
    public int missingNumber3(int[] nums) {
        int len = nums.length;
        int sum1 = len * (len+1) / 2;
        int sum2 = 0;
        for (int num : nums) sum2 += num;
//        int sum2 = IntStream.of(nums).sum();
//        int sum2 = IntStream.of(nums).reduce(0, (x, y) -> x + y);
        return sum1 - sum2;
    }
}
