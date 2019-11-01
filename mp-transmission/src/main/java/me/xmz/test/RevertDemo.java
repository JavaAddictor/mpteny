package me.xmz.test;

import java.util.Iterator;

public class RevertDemo {

    public static void main(String[] args) {
        RevertLinkedList<String> rst = new RevertLinkedList<>();
        rst.add("2h");
        rst.add("3hh");
        rst.add("4gt");
        rst.add("534a");
        rst.add("6dfs");
        rst.add("7dfs234");

        rst.revert();

        Iterator<String> iterator = rst.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            System.out.println(next);
        }
    }
}
