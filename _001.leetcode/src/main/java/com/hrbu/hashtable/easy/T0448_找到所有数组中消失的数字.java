package com.hrbu.hashtable.easy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class T0448_找到所有数组中消失的数字 {

    @Test
    public void Main() {
        int[] nums = {4,3,2,7,8,2,3,1};
//        int[] nums = {1,1};

        List<Integer> disappearedNumbers = findDisappearedNumbers3(nums);
        System.out.println(disappearedNumbers);

    }

    public List<Integer> findDisappearedNumbers(int[] nums) {
        // 保存结果
        List<Integer> list = new LinkedList<>();
        Integer[] indexs = new Integer[nums.length + 1];

        for (int num : nums) {
            indexs[num] = 1;
        }

        for (int i = 1; i < indexs.length; i++) {
            if (null == indexs[i]) list.add(i);
        }

        return list;
    }


    /**
     * 官方解： 遍历nums，遍历到x， 就将下标为x-1的数+n，则最后小于n的【下标+1】为没有出现的数字
     * @param nums
     * @return
     */
    public List<Integer> findDisappearedNumbers2(int[] nums) {
        List<Integer> list = new LinkedList<>();

        int len = nums.length;
        for (int num : nums) {
            nums[(num - 1)%len] += len;
        }

        for (int i = 0; i < len; i++) {
            if (nums[i] <= len) list.add(i+1);
        }

        return list;
    }

    /**
     * 原地hash
     * @param nums
     * @return
     */
    public List<Integer> findDisappearedNumbers3(int[] nums) {

        int len = nums.length;
        int index = 0;
        while (index < len) {
            if (nums[index] - 1 == index) {
                index ++;
            } else {
                int tag = nums[index] - 1;
                if (nums[index] == nums[tag]){
                    index ++;
                    continue;
                }
                int temp = nums[index];
                nums[index] = nums[tag];
                nums[tag] = temp;

            }
        }

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (nums[i] - 1 != i) list.add(i+1);
        }

        return list;
    }

}
