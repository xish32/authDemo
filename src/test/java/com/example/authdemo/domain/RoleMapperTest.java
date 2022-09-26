package com.example.authdemo.domain;

import com.example.authdemo.entity.Role;
import com.example.authdemo.entity.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RoleMapperTest {
    @Test
    public void testAdd() {
        RoleMapper roleMapper = RoleMapper.getInstance();
        assertEquals(1, roleMapper.insert(new Role("admin")));
        assertEquals(0, roleMapper.insert(new Role(null)));
        assertEquals(0, roleMapper.insert(null));
        assertEquals("admin", roleMapper.get("admin").getName());
        assertEquals(0, roleMapper.delete(null));
        assertEquals(0, roleMapper.delete("client"));
        assertEquals(1, roleMapper.delete("admin"));
    }

}
