package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.domain.UserMapper;
import com.example.authdemo.entity.User;
import com.example.authdemo.exception.AuthException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
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
                hasCount++;
            }
        }


        assertEquals(1, hasCount);


    }



}
