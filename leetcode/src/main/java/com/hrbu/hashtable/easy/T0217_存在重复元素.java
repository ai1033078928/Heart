package com.hrbu.hashtable.easy;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;

public class T0217_存在重复元素 {

    @Test
    public void Main() {
        int[] nums = {1,2,3,1};
        System.out.println(containsDuplicate2(nums));
    }


    /**
     * hashSet判断包含
     * @param nums
     * @return
     */
    public boolean containsDuplicate(int[] nums) {
        HashSet<Integer> hashSet = new HashSet<>();
        for (int num : nums) {
            if (hashSet.contains(num)) return true;
            hashSet.add(num);
        }
        return false;
    }

    /**
     * 循环遍历(超时)
     * @param nums
     * @return
     */
    public boolean containsDuplicate2(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) return true;
            }
        }
        return false;
    }


    /**
     * 排序
     * @param nums
     * @return
     */
    public boolean containsDuplicate3(int[] nums) {
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] == nums[i+1]) return true;
        }
        return false;
    }
}
