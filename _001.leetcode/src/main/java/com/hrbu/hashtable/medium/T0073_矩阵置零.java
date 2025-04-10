package com.hrbu.hashtable.medium;

import org.junit.Test;

public class T0073_矩阵置零 {
    @Test
    public void Main() {

        int[][] matrix = {
                {0,1,2,0},
                {3,4,5,2},
                {1,3,1,5}
        };

//        setZeroes(matrix);
        setZeroes2(matrix);


        // 打印数组
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 其他方法 待完善
     * 使用第一行和第一列保存需要置0的位置，两个变量保存第一行和第一列是否包含0
     */
    public void setZeroes2(int[][] matrix) {
        int row = matrix.length;
        int col = matrix[0].length;

        boolean r1 = false, c1 = false;

        for (int i = 0; i < row; i++) {
            if (matrix[i][0] == 0) {
                r1 = true;
                break;
            }
        }
        for (int i = 0; i < col; i++) {
            if (matrix[0][i] == 0) {
                c1 = true;
                break;
            }
        }

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (matrix[i][j] == 0) matrix[i][0] = matrix[0][j] = 0;
            }
        }

        for (int i = 1; i < row; i++) {
            for (int j = 1; j < col; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
            }
        }

        if ( r1 ) {
            for (int i = 0; i < row; i++) {
                matrix[i][0] = 0;
            }
        }
        if ( c1 ) {
            for (int i = 0; i < col; i++) {
                matrix[0][i] = 0;
            }
        }
    }

    /**
     * 使用布尔数组标记需要置为0的行和列
     * @param matrix
     */
    public void setZeroes(int[][] matrix) {
        int row = matrix.length;
        int col = matrix[0].length;

        boolean[] r = new boolean[row];
        boolean[] c = new boolean[col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if ( matrix[i][j] == 0 ) {
                    r[i] = c[j] = true;
                }
            }
        }

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if ( r[i] || c[j] ) {
                    matrix[i][j] = 0;
                }
            }
        }

    }
}
