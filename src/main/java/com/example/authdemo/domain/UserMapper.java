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

    private static UserMapper userMapper = new UserMapper();

    private UserMapper() {
        dataMap = new ConcurrentHashMap<>();
    }

    public static UserMapper getInstance() {
        return userMapper;
    }

    /**
     * 向数据中插入一条用户的信息
     * @Param userInfo -- 待插入的用户
     * @Return 成功插入的记录数，正常应该为1
     * */
    public int insert(User userInfo) {
        if (null == userInfo) return 0;
        if (null == userInfo.getName()) return 0;

        if (dataMap.containsKey(userInfo.getName())) {
            return 0;
        }
        dataMap.put(userInfo.getName(), userInfo);
        return 1;
    }

    /**
     * 从库中删除对应userName的信息
     * @Param userName -- 用户名
     * @Return 成功删除的记录数，正常应该是1
     * */
    public int delete(String userName) {
        if (null == userName) return 0;

        User delUser = dataMap.remove(userName);
        if (delUser == null) return 0;
        return 1;
    }

    /**
     * 根据userName查找容器内的用户对象
     * @param userName -- 用户名
     * @return 用户对象，没有则返回null
     */
    public User get(String userName) {
        return dataMap.get(userName);
    }

}
