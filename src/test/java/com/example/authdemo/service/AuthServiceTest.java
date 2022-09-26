package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}
