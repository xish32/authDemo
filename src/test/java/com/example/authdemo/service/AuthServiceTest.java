package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.domain.UserRoleMapper;
import com.example.authdemo.exception.AuthException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthServiceTest {

    @Before
    public void init() {
        //清理现场
        AuthService authService = AuthService.getInstance();
        authService.delRole("admin");
        authService.delRole("officer");
        authService.delUser("enterprise");
        authService.delUser("yorktown");
        authService.delUser("hornet");
        authService.delUser("ranger");
        authService.delUser("wasp");

        //恢复基本配置
        authService.setDelayTimeunit(Calendar.HOUR_OF_DAY);
        authService.setDelayTimes(2);
        authService.setEncryptKey("12345678");
    }


    @After
    public void teardown() {
        //清理现场
        AuthService authService = AuthService.getInstance();
        authService.delRole("admin");
        authService.delRole("officer");
        authService.delUser("enterprise");
        authService.delUser("yorktown");
        authService.delUser("hornet");
        authService.delUser("ranger");
        authService.delUser("wasp");

        //恢复基本配置
        authService.setDelayTimeunit(Calendar.HOUR_OF_DAY);
        authService.setDelayTimes(2);
        authService.setEncryptKey("12345678");
    }

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
        assertEquals(AuthResult.SUCCESS, authService.addUser("hornet", "CV-8"));
        assertEquals(AuthResult.SUCCESS, authService.addUser("wasp", null));

        assertEquals(AuthResult.SUCCESS, authService.addRole("admin"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("officer"));
        //加角色
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("enterprise", "admin"));
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("hornet", "officer"));
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("wasp", "admin"));

        //授权场景验证
        //场景一：失败授权场景的测试，各种参数不正确
        AuthServiceUtil.checkFailAuthenticate(authService, null, null, AuthResult.PARAMETER_NULL);//各种授权失败的测试
        AuthServiceUtil.checkFailAuthenticate(authService, "enterprise", null, AuthResult.INVALID_USERNAME_PASSWORD);
        AuthServiceUtil.checkFailAuthenticate(authService, "enterprise", "CV-5", AuthResult.INVALID_USERNAME_PASSWORD);
        AuthServiceUtil.checkFailAuthenticate(authService, "ranger", null, AuthResult.INVALID_USERNAME_PASSWORD);

        AuthServiceUtil.checkFailCheckRole(authService, null, "admin", AuthResult.PARAMETER_NULL);

        try {
            //场景二：正常获取令牌然后执行
            String token = authService.authenticate("enterprise", "CV-6");
            assertEquals(true, authService.checkRole(token, "admin"));
            assertEquals(false, authService.checkRole(token, "officer"));
            //不存在的角色也属于false
            assertEquals(false, authService.checkRole(token, "taskforce"));

            //场景三：失败的获取token过后，旧token依旧可以正常使用
            AuthServiceUtil.checkFailAuthenticate(authService, "enterprise", "CV-5", AuthResult.INVALID_USERNAME_PASSWORD);
            assertEquals(true, authService.checkRole(token, "admin"));
            assertEquals(false, authService.checkRole(token, "officer"));

            //场景四：失败的失效操作后，旧有token依旧可以正常使用
            authService.invalidate("doTest");
            assertEquals(true, authService.checkRole(token, "admin"));
            assertEquals(false, authService.checkRole(token, "officer"));

            //场景五：给现有token失效后，就有的token变成无效的状态
            authService.invalidate(token);
            AuthServiceUtil.checkFailCheckRole(authService, token, "admin", AuthResult.INVALID_TOKEN);
            AuthServiceUtil.checkFailGetRoles(authService, token, AuthResult.INVALID_TOKEN);

            //场景六：现有token被顶替后，旧有的token变成无效的状态
            //获得一个新token
            token = authService.authenticate("enterprise", "CV-6");
            assertEquals(true, authService.checkRole(token, "admin"));
            assertEquals(false, authService.checkRole(token, "officer"));
            //尝试新拉取一个token后的生效失效情况
            String oldToken = token;
            token = authService.authenticate("enterprise", "CV-6");
            assertEquals(true, authService.checkRole(token, "admin"));
            assertEquals(false, authService.checkRole(token, "officer"));
            AuthServiceUtil.checkFailCheckRole(authService, oldToken, "admin", AuthResult.INVALID_TOKEN);
            AuthServiceUtil.checkFailGetRoles(authService, oldToken, AuthResult.INVALID_TOKEN);


            //场景七：验证超时失效的情况
            authService.setDelayTimeunit(Calendar.SECOND);
            authService.setDelayTimes(2);
            token = authService.authenticate("enterprise", "CV-6");
            assertEquals(true, authService.checkRole(token, "admin"));
            assertEquals(false, authService.checkRole(token, "officer"));

            try {
                Thread.sleep(2050); //稍微拉长点时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AuthServiceUtil.checkFailCheckRole(authService, token, "admin", AuthResult.INVALID_TOKEN);
            AuthServiceUtil.checkFailGetRoles(authService, token, AuthResult.INVALID_TOKEN);
            authService.setDelayTimeunit(Calendar.HOUR_OF_DAY);

            //场景八：如果角色被删除，那么对应的角色信息也定然找不到
            token = authService.authenticate("hornet", "CV-8");
            assertEquals(true, authService.checkRole(token, "officer"));
            authService.delRole("officer");
            assertEquals(false, authService.checkRole(token, "officer"));
            //即使后面加了同名的角色，也不行
            authService.addRole("officer");
            assertEquals(false, authService.checkRole(token, "officer"));

            //场景九：如果用户被删除，那么对应的token会自动失效
            token = authService.authenticate("enterprise", "CV-6");
            assertEquals(true, authService.checkRole(token, "admin"));
            authService.delUser("enterprise");
            AuthServiceUtil.checkFailCheckRole(authService, token, "admin", AuthResult.INVALID_TOKEN);
            AuthServiceUtil.checkFailGetRoles(authService, token, AuthResult.INVALID_TOKEN);

            //场景十：这里允许空密码的用户登录
            String waspToken = authService.authenticate("wasp", null);
            assertEquals(true, authService.checkRole(waspToken, "admin"));
        } catch (AuthException e) {
            fail();
        }
    }

    @Test
    public void testAllRoles() {
        //测试token
        //准备初始数据
        AuthService authService = AuthService.getInstance();
        assertEquals(AuthResult.SUCCESS, authService.addUser("enterprise", "CV-6"));
        assertEquals(AuthResult.SUCCESS, authService.addUser("yorktown", "CV-5"));
        assertEquals(AuthResult.SUCCESS, authService.addUser("hornet", "CV-8"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("admin"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("officer"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("maintainer"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("pilot"));
        //加角色
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("enterprise", "admin"));
        assertEquals(AuthResult.SUCCESS, authService.grantRoleToUser("hornet", "officer"));

        //根据代码可以知道，这部分逻辑实际上和checkRoles是共用部分代码的
        //因此token失效的测试用例可以共用，已经在之前的场景中验证过了
        AuthServiceUtil.checkFailGetRoles(authService, null, AuthResult.PARAMETER_NULL);

        try {
            //授权获取token
            String token = authService.authenticate("enterprise", "CV-6");
            List<String> roleList;

            //场景一：正常功能验证
            roleList = authService.getUserRoles(token);
            AuthServiceUtil.checkList(roleList, "admin");

            //场景二：添加几个新角色，能够正常展示
            authService.grantRoleToUser("enterprise", "officer");
            authService.grantRoleToUser("enterprise", "pilot");

            AuthServiceUtil.checkList(authService.getUserRoles(token), "admin", "officer", "pilot");

            //场景三：删除其中一个角色，能够正常把相关用户的信息都删除
            authService.grantRoleToUser("hornet", "pilot");
            String hToken = authService.authenticate("hornet", "CV-8");
            AuthServiceUtil.checkList(authService.getUserRoles(hToken), "officer", "pilot");
            AuthServiceUtil.checkList(authService.getUserRoles(token), "admin", "officer", "pilot");

            authService.delRole("pilot");
            AuthServiceUtil.checkList(authService.getUserRoles(hToken), "officer");
            AuthServiceUtil.checkList(authService.getUserRoles(token), "admin", "officer");

            //场景四：无角色的用户，返回一个空队列
            authService.delRole("officer");
            AuthServiceUtil.checkList(authService.getUserRoles(hToken));

            //场景五：新增被删除的同名角色，不会影响到数据
            authService.addRole("officer");
            authService.addRole("pilot");
            AuthServiceUtil.checkList(authService.getUserRoles(token), "admin");
            //到数据段验证下是不是清理掉了
            List<Long> eIdList = UserRoleMapper.getInstance().getAllRolesFromUser("enterprise");
            assertEquals(1, eIdList.size());

        } catch (AuthException e) {
            e.printStackTrace();
            fail();
        }
    }

}
