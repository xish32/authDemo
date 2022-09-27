package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.exception.AuthException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthServiceUtil {
    /***
     * 授权失败场景的验证工具类
     * @param authService
     * @param username
     * @param password
     * @param expectedRes
     */
    public static void checkFailAuthenticate(AuthService authService,
                                       String username,
                                       String password,
                                       AuthResult expectedRes) {
        try {
            authService.authenticate(username, password);
            fail();
        } catch (AuthException ex) {
            assertEquals(expectedRes.getRetCode(), ex.getCode());
            assertEquals(expectedRes.getRetMsg(), ex.getMsg());
        }
    }

    /**
     *
     * @param authService
     * @param token
     * @param roleName
     * @param expectedRes
     */
    public static void checkFailCheckRole(AuthService authService,
                                          String token,
                                          String roleName,
                                          AuthResult expectedRes) {
        try {
            authService.checkRole(token, roleName);
            fail();
        } catch (AuthException ex) {
            assertEquals(expectedRes.getRetCode(), ex.getCode());
            assertEquals(expectedRes.getRetMsg(), ex.getMsg());
        }
    }

    /***
     * 检查getRoles的功能的工具类
     * @param authService
     * @param token
     * @param expectedRes
     */
    public static void checkFailGetRoles(AuthService authService,
                                   String token,
                                   AuthResult expectedRes) {
        try {
            authService.getUserRoles(token);
            fail();
        } catch (AuthException ex) {
            assertEquals(expectedRes.getRetCode(), ex.getCode());
            assertEquals(expectedRes.getRetMsg(), ex.getMsg());
        }
    }

    public static void checkList(List<String> roleList, String ... expected) {
        assertEquals(expected.length, roleList.size());
        Set<String> expSets = Arrays.stream(expected).collect(Collectors.toSet());
        assertEquals(true, expSets.containsAll(roleList));
    }

}
