package com.example.authdemo.entity;

import java.util.Date;

/**
 * 角色类
 * 存储角色相关信息
 */
public class Role {
    /** 角色名，key */
    private String name;

    /** 角色id */
    private long id;

    /** 更新时间 */
    private Date updateTime;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
