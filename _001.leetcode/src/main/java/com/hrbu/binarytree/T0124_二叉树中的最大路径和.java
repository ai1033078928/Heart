package com.hrbu.binarytree;

import org.junit.Test;

public class T0124_二叉树中的最大路径和 {

    @Test
    public void Main() {
        TreeNode root = Tree.getTree(new Integer[]{-10, 9, 20, null, null, 15, 7});
        System.out.println(maxPathSum(root));
    }


    public int maxPathSum(TreeNode root) {


//        maxPathSum(root.left);
//        maxPathSum(root.right);

        return 1;
    }
}
