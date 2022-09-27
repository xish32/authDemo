package com.example.authdemo;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.domain.RoleMapper;
import com.example.authdemo.domain.UserMapper;
import com.example.authdemo.domain.UserRoleMapper;
import com.example.authdemo.entity.Role;
import com.example.authdemo.entity.User;
import com.example.authdemo.exception.AuthException;
import com.example.authdemo.service.AuthService;
import com.example.authdemo.util.DateUtil;
import com.example.authdemo.util.DesUtil;
import com.example.authdemo.util.StringUtil;

import java.util.*;

public class Demo {

    public static void main(String args[]) {
        AuthService authService = AuthService.getInstance();
        AuthResult result;
        //当然，调度的返回结果也可以不检查
        result = authService.addUser("enterprise", "CV-6");
        if (!isSuccess(result)) {
            System.out.println("无法添加用户：" + result.getRetMsg());
            return;
        }

        authService.addRole("admin");
        authService.addRole("officer");
        authService.grantRoleToUser("enterprise", "admin");
        authService.grantRoleToUser("enterprise", "officer");

        try {
            String token = authService.authenticate("enterprise", "CV-6");
            System.out.println("token: " + token);

            boolean checkResult = authService.checkRole(token, "admin");
            System.out.println("checkResult: " + checkResult);

            List<String> roleList = authService.getUserRoles(token);
            System.out.println("current roles of enterprise");
            for (String roleName : roleList) {
                System.out.println("roleName");
            }
        } catch (AuthException e) {
            e.printStackTrace();
        }

    }

    private static boolean isSuccess(AuthResult result) {
        if (null == result) return false;
        return AuthResult.SUCCESS.getRetCode().equals(result.getRetCode());
    }
}
