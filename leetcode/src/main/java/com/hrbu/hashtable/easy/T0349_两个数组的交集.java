package com.hrbu.hashtable.easy;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class T0349_两个数组的交集 {
    @Test
    public void Main() {
        int[] nums1 = {1,2,2,1}, nums2 = {2,2};
        int[] ints = intersection(nums1, nums2);

        System.out.println(ints);
    }


    /**
     * HashSet 和 LinkedList
     * @param nums1
     * @param nums2
     * @return
     */
    public int[] intersection(int[] nums1, int[] nums2) {
        // 保存结果
        LinkedList<Integer> list = new LinkedList<Integer>();
        // 用来判断是否为交集
        HashSet<Object> hashSet = new HashSet<>();
        // 保存数组1中元素（并去重）
        for (int num : nums1) {
            hashSet.add(num);
        }
        // 判断数组2中元素是否在集合1中存在
        for (int num : nums2) {
            if (hashSet.contains(num)) {
                list.add(num);
                hashSet.remove(num);        // 保证结果没有重复元素
            }
        }
        // 将集合中元素放入数组并返回
        int[] ints = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ints[i] = list.get(i);
        }
        return ints;
    }
}
