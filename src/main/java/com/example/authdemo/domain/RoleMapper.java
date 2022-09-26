package com.example.authdemo.domain;

import com.example.authdemo.entity.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户信息的Mapper类
 * 因为数据是存在内存里，所以这里就是实际是存储的地方了
 * 按设计要求做成单例
 */
public class RoleMapper {
    Map<String, Role> dataMap;
    Map<Long, Role> idMap;

    private static RoleMapper roleMapper = new RoleMapper();

    private RoleMapper() {
        dataMap = new ConcurrentHashMap<>();
        idMap = new ConcurrentHashMap<>();
    }

    public static RoleMapper getInstance() {
        return roleMapper;
    }

    /**
     * 向数据中插入一条角色信息
     * @Param roleInfo -- 待插入的元素
     * @Return 成功插入的记录数，正常应该为1
     * */
    public int insert(Role roleInfo) {
        if (null == roleInfo) return 0;
        if (null == roleInfo.getName()) return 0;

        if (dataMap.containsKey(roleInfo.getName())) {
            return 0;
        }

        dataMap.put(roleInfo.getName(), roleInfo);
        idMap.put(roleInfo.getId(), roleInfo);
        return 1;
    }

    /**
     * 从库中删除对应roleName的角色
     * @Param roleName -- 角色名
     * @Return 成功删除的记录数，正常应该是1
     * */
    public int delete(String roleName) {
        if (null == roleName) return 0;

        idMap.remove(roleName);
        return dataMap.remove(roleName) != null ? 1 : 0;
    }

    /**
     * 根据roleName查找容器内的角色对象
     * @param roleName -- 角色名
     * @return 角色对象，没有则返回null
     */
    public Role get(String roleName) {
        return dataMap.get(roleName);
    }

    /***
     * 根据id清单获取对应的角色对象列表
     * @param id 对应的Id
     * @return 角色对象列表，没有则返回空
     */
    public String getRoleNameById(long id) {
        Role role = idMap.get(id);
        if (null == role) return null;
        return role.getName();
    }
}
