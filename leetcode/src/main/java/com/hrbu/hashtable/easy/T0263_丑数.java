package com.hrbu.hashtable.easy;

import org.junit.Test;

public class T0263_丑数 {
    @Test
    public void Main() {
        System.out.println(isUgly(14));
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
