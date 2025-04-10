package com.hrbu.binarytree;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author ahb
 * @time 20220307
 */
public class Tree {


    /**
     *                   00
     *           01             02
     *      03        04      05       06
     *   07   08   09   10  11 12   13    14
     */
    public static TreeNode getTree(Integer[] arr) {
        int size = arr.length;
        if (size == 0) return null;

        TreeNode head = new TreeNode(arr[0]);

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(head);

        for (int i = 1; i < size ; i += 2) {
            TreeNode pop = queue.poll();
            if (null != arr[i]) {
                pop.left = new TreeNode(arr[i]);
                queue.offer(pop.left);
            }
            if (null != arr[i+1]) {
                pop.right = new TreeNode(arr[i+1]);
                queue.offer(pop.right);
            }
        }

        return head;
    }

    /**
     *
     */
    public static Integer[] getArray(TreeNode node) {

        LinkedList<Integer> list = new LinkedList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(node);
        list.add(node.val);

        while (!queue.isEmpty()) {
            TreeNode treeNode = queue.poll();

            if (null != treeNode.left) {
                list.add(treeNode.left.val);
                queue.offer(treeNode.left);
            }

            if (null != treeNode.right) {
                list.add(treeNode.right.val);
                queue.offer(treeNode.right);
            }
        }


        return list.toArray(new Integer[list.size()]);
    }

    @Test
    public void Main() {
        Integer[] root = new Integer[]{-10,9,20,null,null,15,7};
//        Integer[] root = new Integer[]{1,2,3};

        Integer[] array = getArray(getTree(root));

        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }

    }
}
