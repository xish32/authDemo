package com.example.authdemo.domain;

import com.example.authdemo.entity.User;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class UserMapperTest {
    @Test
    public void testUserMapper() {
        UserMapper userMapper = UserMapper.getInstance();
        assertEquals(1, userMapper.insert(new User("enterprise")));
        assertEquals(0, userMapper.insert(new User(null)));
        assertEquals(0, userMapper.insert(null));
        assertEquals("enterprise", userMapper.get("enterprise").getName());
        assertEquals(1L, userMapper.get("enterprise").getId());
        assertEquals(0, userMapper.delete(null));
        assertEquals(0, userMapper.delete("yorktown"));
        assertEquals(1, userMapper.delete("enterprise"));
    }

    @Test
    public void testToken() {
        UserMapper userMapper = UserMapper.getInstance();
        assertEquals(1, userMapper.insert(new User("enterprise")));
        assertEquals(1, userMapper.addToken("enterprise", "123", new Date(), new Date()));
        assertEquals("123", userMapper.get("enterprise").getAuthToken());
        assertEquals(1, userMapper.addToken("enterprise", "345", new Date(), new Date()));
        assertEquals("345", userMapper.get("enterprise").getAuthToken());
        assertEquals(null, userMapper.getUserByToken("123"));
        assertEquals("enterprise", userMapper.getUserByToken("345").getName());

        assertEquals(1, userMapper.addToken("enterprise", null, new Date(), new Date()));
        assertEquals(null, userMapper.getUserByToken("345"));
    }

}
