package com.example.authdemo.domain;

import com.example.authdemo.entity.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户信息的Mapper类
 * 因为数据是存在内存里，所以这里就是实际是存储的地方了
 * 按设计要求做成单例
 */
public class UserRoleMapper {
    /** 该用户共有哪些角色ID，key--用户名，value--角色集合 */
    Map<String, Set<Long>> userRoleMap;
    /** 该角色下有哪些用户ID，key--角色名，value--用户集合 */
    Map<String, Set<Long>> roleUserMap;

    private static UserRoleMapper userRoleMapper = new UserRoleMapper();

    private UserRoleMapper() {
        userRoleMap = new ConcurrentHashMap<>();
        roleUserMap = new ConcurrentHashMap<>();
    }

    public static UserRoleMapper getInstance() {
        return userRoleMapper;
    }

    /**
     * 给某用户添加一个角色
     * @Param userName -- 用户名
     * @Param roleId -- 角色Id
     * @Return 成功插入的记录数
     * */
    public int addRoleToUser(String userName, long roleId) {
        if (null == userName) return 0;
        userRoleMap.compute(userName, (key, value) -> {
            if (value == null) {
                value = new HashSet<>();
            }
            value.add(roleId);
            return value;
        });
        return 1;
    }

    /**
     * 删除某用户的一个角色
     * @Param userName -- 用户名
     * @Param roleId -- 角色Id
     * @Return 成功删除的记录数
     * */
    public int delRoleFromUser(String userName, long roleId) {
        if (null == userName) return 0;
        Set<Long> roleIdSet = userRoleMap.get(userName);
        if (null == roleIdSet) return 0;
        return roleIdSet.remove(roleId) ? 1 : 0;
    }

    /***
     * 删除指定的用户关系
     * @param userName -- 用户名
     * @return 成功删除的记录数
     */
    public int delUser(String userName) {
        if (null == userName) return 0;
        return userRoleMap.remove(userName) != null ? 1 : 0;
    }


    /**
     * 查当前用户是否有对应的角色
     * @param userName 用户名
     * @param roleId 角色id
     * @return 存在则返回true，不存在则返回false（空值均返回false）
     */
    public boolean hasRoleFromUser(String userName, long roleId) {
        if (null == userName) return false;
        Set<Long> roleIdSet = userRoleMap.get(userName);
        if (null == roleIdSet) return false;
        return roleIdSet.contains(roleId);
    }

    /**
     * 查指定用户的所有角色
     * @param userName 用户名
     * @return 一个List，里面存储
     */
    public List<Long> getAllRolesFromUser(String userName) {
        if (null == userName) return new ArrayList<>();
        Set<Long> roleIdSet = userRoleMap.get(userName);
        return new ArrayList<>(roleIdSet);
    }

}
