package com.example.authdemo.domain;

import com.example.authdemo.entity.User;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestDemo {
    @Test
    public void testAdd() {
        User userInfo = new User("233");
        HashMap<String, Set<Long>> res1 = new HashMap<>(32);
        Long id = 456L;
        res1.compute(userInfo.getName(), (k, v) -> {
//            if (v == null) {
//                v = new HashSet<>();
//            }

            v.add(userInfo.getId());
            return v;
        });
        System.out.println(res1);

    }

}
