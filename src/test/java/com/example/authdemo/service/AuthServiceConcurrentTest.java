package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.domain.RoleMapper;
import com.example.authdemo.domain.UserMapper;
import com.example.authdemo.domain.UserRoleMapper;
import com.example.authdemo.entity.User;
import com.example.authdemo.exception.AuthException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthServiceConcurrentTest {

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

        authService.addUser("enterprise", "CV-6");
        authService.addUser("yorktown", "CV-5");
        authService.addUser("hornet", "CV-8");

        assertEquals(AuthResult.SUCCESS, authService.addRole("admin"));
        assertEquals(AuthResult.SUCCESS, authService.addRole("officer"));
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
    public void testConcurrentToken() throws ExecutionException, InterruptedException {
        //场景一：验证多个线程并发请求token，最后只有一个有效token，其余均无效
        Callable newTokenCall = new Callable() {
            @Override
            public String call() throws Exception {
                AuthService authService = AuthService.getInstance();
                String token = authService.authenticate("enterprise", "CV-6");

                System.out.println(Thread.currentThread().getName() + " get token " + token);
                return token;
            }
        };
        FutureTask futureTasks[] = new FutureTask[10];
        for (int i = 0 ; i < futureTasks.length ; i++) {
            futureTasks[i] = new FutureTask(newTokenCall);
        }

        for (FutureTask task : futureTasks) {
            task.run();
        }

        List<String> tokens = new ArrayList<>();
        for (FutureTask task : futureTasks) {
            String curToken = (String)task.get();
            tokens.add(curToken);
        }

        int hasCount = 0;
        for (String token : tokens) {
            UserMapper userMapper = UserMapper.getInstance();
            User curUser = userMapper.getUserByToken(token);
            if (null != curUser) {
                assertEquals(token, curUser.getAuthToken());
                hasCount++;
            }
        }


        assertEquals(1, hasCount);
    }

    @Test
    public void testConcurrentTokenInvalidate() throws ExecutionException, InterruptedException {
        //场景二：验证多个线程并发请求token（含下线），最后所有token均失效
        boolean isFirst = false;
        Callable newTokenCall = new Callable() {
            @Override
            public String call() throws Exception {
                AuthService authService = AuthService.getInstance();
                String token = authService.authenticate("enterprise", "CV-6");
                authService.invalidate(token);
                System.out.println(Thread.currentThread().getName() + " get token " + token);
                return token;
            }
        };
        FutureTask futureTasks[] = new FutureTask[10];
        for (int i = 0 ; i < futureTasks.length ; i++) {
            futureTasks[i] = new FutureTask(newTokenCall);
        }

        for (FutureTask task : futureTasks) {
            task.run();
        }

        List<String> tokens = new ArrayList<>();
        for (FutureTask task : futureTasks) {
            String curToken = (String)task.get();
            tokens.add(curToken);
        }

        int hasCount = 0;
        for (String token : tokens) {
            UserMapper userMapper = UserMapper.getInstance();
            User curUser = userMapper.getUserByToken(token);
            if (null != curUser) {
                hasCount++;
            }
        }


        assertEquals(0, hasCount);
    }

    @Test
    public void testConcurrentUserRole() throws ExecutionException, InterruptedException {
        AuthService authServiceAll = AuthService.getInstance();
        authServiceAll.grantRoleToUser("enterprise", "admin");
        authServiceAll.grantRoleToUser("enterprise", "officer");

        //场景三：验证多个线程并发创建角色，新增角色后删除原角色，检查最后剩余的角色信息
        AtomicInteger countNum = new AtomicInteger(1);
        Callable newTokenCall = new Callable() {
            @Override
            public String call() throws Exception {
                AuthService authService = AuthService.getInstance();
                String roleName = "test" + countNum.getAndIncrement();
                authService.addRole(roleName);
                authService.grantRoleToUser("enterprise", roleName);
                authService.delRole(roleName);

                return roleName;
            }
        };
        FutureTask futureTasks[] = new FutureTask[10];
        for (int i = 0 ; i < futureTasks.length ; i++) {
            futureTasks[i] = new FutureTask(newTokenCall);
        }

        for (FutureTask task : futureTasks) {
            task.run();
        }

        List<String> roleNames = new ArrayList<>();
        for (FutureTask task : futureTasks) {
            String curRoleName = (String)task.get();
            roleNames.add(curRoleName);
        }


        String token = null;
        try {
            token = authServiceAll.authenticate("enterprise", "CV-6");
            List<String> roleNameList = authServiceAll.getUserRoles(token);
            AuthServiceUtil.checkList(roleNameList, "admin", "officer");
        } catch (AuthException e) {
            fail();
        }



        for (String roleName : roleNames) {
            RoleMapper roleMapper = RoleMapper.getInstance();
            assertEquals(null, roleMapper.get(roleName));
        }

        List<Long> idList = UserRoleMapper.getInstance().getAllRolesFromUser("enterprise");


        assertEquals(2, idList.size());


    }


}
