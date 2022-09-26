package com.example.authdemo.domain;

import com.example.authdemo.entity.User;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestDemo {
    @Test
    public void testAdd() {

        for (int i = 0 ; i < 10 ; i++)
            System.out.println(UUID.randomUUID().toString());

        Set<Long> testSet = new HashSet<>();
        testSet.add(1L);
        testSet.add(2L);
        System.out.println(testSet.add(3L));
        System.out.println(testSet.add(3L));
        testSet.remove(null);

        List<Long> newList = new ArrayList<>(testSet);
        newList.set(2,4L);
        for (Long num : newList)
            System.out.println(num);
        System.out.println("========");

        for (Long num : testSet)
            System.out.println(num);


        User userInfo = new User("233");
        HashMap<String, Set<Long>> res1 = new HashMap<>(32);
        Long id = 456L;
        res1.compute(userInfo.getName(), (k, v) -> {
            if (v == null) {
                v = new HashSet<>();
            }

            return v;
        });
        System.out.println(res1);



    }

}
