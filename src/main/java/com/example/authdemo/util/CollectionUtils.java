package com.example.authdemo.util;


import java.util.Collection;

/**
 * 替代CollectionUtils用的工具类
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection collect) {
        return (null == collect) || (collect.isEmpty());
    }
}
