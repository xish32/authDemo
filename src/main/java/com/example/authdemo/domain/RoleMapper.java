package com.example.authdemo.domain;

import com.example.authdemo.entity.Role;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户信息的Mapper类
 * 因为数据是存在内存里，所以这里就是实际是存储的地方了
 * 按设计要求做成单例
 */
public class RoleMapper {
    Map<String, Role> dataMap;

    private static RoleMapper roleMapper = new RoleMapper();

    private RoleMapper() {
        dataMap = new ConcurrentHashMap<>();
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
        return 1;
    }

    /**
     * 从库中删除对应roleName的角色
     * @Param roleName -- 角色名
     * @Return 成功删除的记录数，正常应该是1
     * */
    public int delete(String roleName) {
        if (null == roleName) return 0;

        Role delUser = dataMap.remove(roleName);
        if (delUser == null) return 0;
        return 1;
    }

    /**
     * 根据roleName查找容器内的用户对象
     * @param roleName -- 角色名
     * @return 角色对象，没有则返回null
     */
    public Role get(String roleName) {
        return dataMap.get(roleName);
    }

}
