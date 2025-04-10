package com.hrbu.util;

import org.junit.Test;

public class 反转链表 {
    @Test
    public void Main() {
        ListNode node6 = new ListNode(6, null);
        ListNode node5 = new ListNode(5, node6);
        ListNode node4 = new ListNode(4, node5);
        ListNode node3 = new ListNode(3, node4);
        ListNode node2 = new ListNode(2, node3);
        ListNode node1 = new ListNode(1, node2);

        System.out.println(node1);
        ListNode node = reverseLink2(node1, null);
        System.out.println(node);
    }

    public ListNode reverseLink(ListNode head) {
        ListNode pre = null, next = head;

        while (next != null) {
            ListNode cur = next;
            next = next.next;
            cur.next = pre;
            pre = cur;
        }

        return pre;
    }


    public ListNode reverseLink2(ListNode source, ListNode tag) {
        if (source == null) return tag;

        ListNode cur = source;
        source = source.next;
        cur.next = tag;
        tag = cur;
        ListNode res = reverseLink2(source, tag);
        return res;
    }
}
