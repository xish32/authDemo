package com.example.authdemo.entity;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 角色类
 * 存储角色相关信息
 */
public class Role {
    /** 下一个ID，给ID生成用 */
    private static AtomicLong nextId = new AtomicLong(1);
    /** 角色名，key */
    private String name;

    /** 角色id */
    private final long id;

    /** 更新时间 */
    private Date updateTime;

    public Role(String roleName) {
        this.name = roleName;
        this.id = nextId.getAndIncrement();
        this.updateTime = new Date();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
