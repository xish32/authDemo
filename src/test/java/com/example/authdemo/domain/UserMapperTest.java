package com.example.authdemo.domain;

import com.example.authdemo.entity.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserMapperTest {
    @Test
    public void testAdd() {
        UserMapper userMapper = UserMapper.getInstance();
        assertEquals(1, userMapper.insert(new User("enterprise")));
        assertEquals(0, userMapper.insert(new User(null)));
        assertEquals(0, userMapper.insert(null));
        assertEquals("enterprise", userMapper.get("enterprise").getName());
        assertEquals(0, userMapper.delete(null));
        assertEquals(0, userMapper.delete("yorktown"));
        assertEquals(1, userMapper.delete("enterprise"));
    }

}
