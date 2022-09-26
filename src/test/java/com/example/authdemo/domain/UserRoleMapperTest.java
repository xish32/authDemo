package com.example.authdemo.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UserRoleMapperTest {
    @Test

    public void testAdd() {
        UserRoleMapper userRoleMapper = UserRoleMapper.getInstance();

        //addRoleToUser
        assertEquals(1, userRoleMapper.addRoleToUser("enterprise", 1L));
        assertEquals(0, userRoleMapper.addRoleToUser("enterprise", 1L));
        assertEquals(1, userRoleMapper.addRoleToUser("enterprise", 2L));
        assertEquals(1, userRoleMapper.addRoleToUser("yorktown", 2L));

        //hasRoleFromUser
        assertEquals(true, userRoleMapper.hasRoleFromUser("enterprise", 1L));
        assertEquals(true, userRoleMapper.hasRoleFromUser("enterprise", 2L));
        assertEquals(true, userRoleMapper.hasRoleFromUser("yorktown", 2L));
        assertEquals(false, userRoleMapper.hasRoleFromUser("yorktown", 1L));
        assertEquals(false, userRoleMapper.hasRoleFromUser("hornet", 2L));

        //getAllRolesFromUser
        checkListLong(userRoleMapper.getAllRolesFromUser("enterprise"), 1L, 2L);
        checkListLong(userRoleMapper.getAllRolesFromUser("yorktown"), 2L);
        checkListLong(userRoleMapper.getAllRolesFromUser("hornet"));

        //delRoleFromUser
        assertEquals(0, userRoleMapper.delRoleFromUser("yorktown", 1L));
        checkListLong(userRoleMapper.getAllRolesFromUser("yorktown"), 2L);
        assertEquals(1, userRoleMapper.delRoleFromUser("yorktown", 2L));
        checkListLong(userRoleMapper.getAllRolesFromUser("yorktown"));

        assertEquals(1, userRoleMapper.delRoleFromUser("enterprise", 2L));
        assertEquals(1, userRoleMapper.addRoleToUser("enterprise", 3L));
        checkListLong(userRoleMapper.getAllRolesFromUser("enterprise"), 1L, 3L);

        //delMultiRolesFromUser
        List<Long> removeList = new ArrayList<>();
        removeList.add(1L);
        removeList.add(2L);
        assertEquals(1, userRoleMapper.delMultiRolesFromUser("enterprise", removeList));
        checkListLong(userRoleMapper.getAllRolesFromUser("enterprise"), 3L);

        //delUser
        assertEquals(1, userRoleMapper.delUser("enterprise"));
        checkListLong(userRoleMapper.getAllRolesFromUser("enterprise"));
        assertEquals(0, userRoleMapper.delUser("hornet"));
    }

    private void checkListLong(List<Long> resList, long ...nums) {
        Set<Long> numSet = new HashSet<>();
        for (long num : nums)
            numSet.add(num);

        int hitCount = 0;
        for (Long num : resList) {
            if (numSet.contains(num)) {
                numSet.remove(num);
            } else {
                Assert.fail();
            }
        }

        assertEquals(numSet.size(), hitCount);
    }

}
