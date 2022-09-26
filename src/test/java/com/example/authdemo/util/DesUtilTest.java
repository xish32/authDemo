package com.example.authdemo.util;

import com.example.authdemo.domain.UserMapper;
import com.example.authdemo.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DesUtilTest {
    @Test
    public void testDesUtil() {

        assertEquals("NoeY1UM4g34=", DesUtil.encrypt("1233333333333","123"));
        assertEquals("123", DesUtil.decrypt("1233333333333","NoeY1UM4g34="));


        assertEquals(null, DesUtil.encrypt("1233333333333",null));
        assertEquals(null, DesUtil.decrypt("1233333333333",null));

        try {
            DesUtil.decrypt(null, "123");
            Assert.fail();
        } catch (Exception ex) {
            assertEquals("解密失败，password不能小于8位", ex.getMessage());
        }

        try {
            DesUtil.encrypt(null, "123");
            Assert.fail();
        } catch (Exception ex) {
            assertEquals("加密失败，password不能小于8位", ex.getMessage());
        }
    }

}
