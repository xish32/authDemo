package com.example.authdemo.util;

/***
 * 给String用的工具类
 * 主要是为了写起来方便
 */
public class StringUtil {
    public static boolean equals(String a, String b) {
        if ((null == a) && (null == b)) return true;
        if (null == a) return false;
        if (null == b) return false;

        return a.equals(b);
    }

}
