package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.exception.AuthException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthServiceTest {
    @Test
    public void testUserRoleAddDel() {

        AuthService authService = AuthService.getInstance();
        assertEquals(AuthResult.SUCCESS, authService.addUser("enterprise", "CV-6"));
        assertEquals(AuthResult.SUCCESS, authService.addUser("yorktown", null));
        assertEquals(AuthResult.PARAMETER_NULL, authService.addUser(null, "CV-1"));
        assertEquals(AuthResult.USER_ALREADY_EXIST, authService.addUser("yorktown", null));

        assertEquals(AuthResult.SUCCESS, authService.delUser("yorktown"));
        assertEquals(AuthResult.PARAMETER_NULL, authService.delUser(null));
        assertEquals(AuthResult.USER_NOTEXIST, authService.delUser("yorktown"));



        assertEquals(AuthResult.SUCCESS, authService.addRole("admin"));
        assertEquals(AuthResult.PARAMETER_NULL, authService.addRole(null));
        assertEquals(AuthResult.ROLE_ALREADY_EXIST, authService.addRole("admin"));

        assertEquals(AuthResult.SUCCESS, authService.delRole("admin"));
        assertEquals(AuthResult.PARAMETER_NULL, authService.delRole(null));
        assertEquals(AuthResult.ROLE_NOTEXIST, authService.delRole("admin"));
    }

    @Test
    public void testGrantRole() {
        //测试token
        //准备初始数据
        AuthService authService = AuthService.getInstance();
        assertEquals(AuthResult.SUCCESS, authService.addUser("enterprise", "CV-6"));
        assertEquals(AuthResult.SUCCESS, authService.addUser("yorktown", "CV-5"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("admin"));

        //加角色
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("enterprise", "admin"));
        //重复加角色效果不变
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("enterprise", "admin"));
        //加不知名的角色报角色不存在
        assertEquals(AuthResult.ROLE_NOTEXIST, authService.grantRoleToUser("enterprise", "taskforce"));
        //用户不存在的验证用户不存在
        assertEquals(AuthResult.USER_NOTEXIST, authService.grantRoleToUser("ranger", "admin"));

        //参数校验，角色名和用户名都不能为空
        assertEquals(AuthResult.PARAMETER_NULL, authService.grantRoleToUser("enterprise", null));
        assertEquals(AuthResult.PARAMETER_NULL, authService.grantRoleToUser(null, "taskforce"));
    }


    @Test
    public void testToken() {
        //测试token
        //准备初始数据
        AuthService authService = AuthService.getInstance();
        assertEquals(AuthResult.SUCCESS, authService.addUser("enterprise", "CV-6"));
        assertEquals(AuthResult.SUCCESS, authService.addUser("yorktown", "CV-5"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("admin"));
        //加角色
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("enterprise", "admin"));



        //失败场景的测试
        checkFailAuthenticate(authService, null, null, AuthResult.PARAMETER_NULL);//各种授权失败的测试
        checkFailAuthenticate(authService, "enterprise", null, AuthResult.INVALID_USERNAME_PASSWORD);
        checkFailAuthenticate(authService, "ranger", null, AuthResult.INVALID_USERNAME_PASSWORD);

        try {
            String token = authService.authenticate("enterprise", "CV-6");
            assertEquals(true, authService.checkRole(token, "admin"));
        } catch (AuthException e) {
            fail();
        }


    }

    private void checkFailAuthenticate(AuthService authService,
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


}
