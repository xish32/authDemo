package com.example.authdemo.entity;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户类
 * 存储用户相关信息
 */
public class User {
    /** 下一个ID，给ID生成用 */
    private AtomicLong nextId = new AtomicLong(1);

    /** 用户名（登录名），key */
    private String name;

    /** 用户id */
    private final long id;

    /** 密码（加密形式） */
    private String password;

    /** 上次授权时间 */
    private Date lastAuthTime;

    /** 状态授权令牌 */
    private String authToken;

    /** 更新时间 */
    private Date updateTime;

    public User(String name) {
        this.name = name;
        this.id = nextId.getAndIncrement();
        //密码
        this.password = null;
        this.lastAuthTime = new Date();
        this.authToken = null;
        this.updateTime = new Date(this.lastAuthTime.getTime());
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastAuthTime() {
        return lastAuthTime;
    }

    public void setLastAuthTime(Date lastAuthTime) {
        this.lastAuthTime = lastAuthTime;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


}
