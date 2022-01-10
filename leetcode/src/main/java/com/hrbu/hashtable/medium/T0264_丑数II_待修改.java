package com.hrbu.hashtable.medium;

import org.junit.Test;

public class T0264_丑数II_待修改 {
    @Test
    public void Main() {
        System.out.println(nthUglyNumber(1352));
    }


    /**
     * 超出时间限制（待完善）
     * @param n
     * @return
     */
    public int nthUglyNumber(int n) {
        if (n == 1) return 1;
        int num = 1;

        for (int i = 2; ; i++) {
            if (isUgly(i)) {
                num ++;
                if (num == n) return i;
            }
        }

    }

    private boolean isUgly(int n) {
        if (n < 1) return false;
        if (n == 1) return true;

        while (n > 1) {
            if ( n % 2 == 0) {
                n /= 2;
            } else if ( n % 3 == 0) {
                n /= 3;
            } else if ( n % 5 == 0) {
                n /= 5;
            } else {
                return false;
            }

        }
        return true;
    }
}
