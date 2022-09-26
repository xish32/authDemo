package com.example.authdemo.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {
    @Test
    public void testStringUtil() {

        assertEquals(true, StringUtil.equals(null, null));
        assertEquals(false, StringUtil.equals("a", null));
        assertEquals(false, StringUtil.equals(null, "b"));

        assertEquals(true, StringUtil.equals("a", "a"));
        assertEquals(false, StringUtil.equals("a", "b"));

        assertEquals(true, StringUtil.equals(new String("a"), new String("a")));
        assertEquals(false, StringUtil.equals(new String("a"), new String("b")));

    }

}
