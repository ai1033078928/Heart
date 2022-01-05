package com.hrbu.util;

public class ListNode {
    public int val;
    public ListNode next;

    ListNode(int x) {
        this.val = x;
        this.next = null;
    }

    ListNode(int x, ListNode next) {
        this.val = x;
        this.next = next;
    }
}
