package com.example.authdemo.domain;

import com.example.authdemo.entity.User;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户信息的Mapper类
 * 因为数据是存在内存里，所以这里就是实际是存储的地方了
 * 按设计要求做成单例
 */
public class UserMapper {
    Map<String, User> dataMap;

    private UserMapper() {
        dataMap = new ConcurrentHashMap<>();
    }

    /**  */
    public int insert(User userInfo) {
        return 0;
    }

    /**  */
    public int delete(String userName) {
        return 0;
    }


}
