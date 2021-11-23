package com.hrbu.hashtable;

import org.junit.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
 *
 * 你可以按任意顺序返回答案。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：nums = [2,7,11,15], target = 9
 * 输出：[0,1]
 * 解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
 * 示例 2：
 *
 * 输入：nums = [3,2,4], target = 6
 * 输出：[1,2]
 * 示例 3：
 *
 * 输入：nums = [3,3], target = 6
 * 输出：[0,1]
 *  
 *
 * 提示：
 *
 * 2 <= nums.length <= 104
 * -109 <= nums[i] <= 109
 * -109 <= target <= 109
 * 只会存在一个有效答案
 * 进阶：你可以想出一个时间复杂度小于 O(n2) 的算法吗？
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/two-sum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class 两数之和 {

    @Test
    public void Main() {
        int[] ints = twoSum3(new int[]{2, 7, 11, 15}, 9);
        for (int anInt : ints) {
            System.out.printf(String.valueOf(anInt));
        }
    }


    /**
     * 1 暴力遍历
     */
    public int[] twoSum(int[] nums, int target) {

        for (int i = 0; i < nums.length; i++) {
            int i1 = i + 1;
            for (; i1 < nums.length; i1++) {
                if (nums[i] + nums[i1] == target) {
                    return new int[]{i, i1};
                }
            }
        }
        return new int[0];
    }


    /**
     *
     * 2 两遍hash表，空间换时间
     */
    public int[] twoSum2(int[] nums, int target) {

        Map <Integer, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            hashMap.put(nums[i] , i);
        }

        for (int i = 0; i < nums.length; i++) {
            int num_index = hashMap.getOrDefault(target - nums[i], -1);
            if ( num_index != -1 && num_index != i) {
                return new int[]{i, num_index};
            }
        }
        return new int[0];
    }

    /**
     * 3 第二种方法改进，一遍hash表
     */
    public int[] twoSum3(int[] nums, int target) {

        Map <Integer, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            if (hashMap.containsKey( target - nums[i] )) {
                return new int[]{hashMap.get(target - nums[i]), i};
            } else {
                hashMap.put( nums[i], i );
            }
        }

        return new int[0];
    }
}
