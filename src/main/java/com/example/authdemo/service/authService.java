package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.domain.RoleMapper;
import com.example.authdemo.domain.UserMapper;
import com.example.authdemo.domain.UserRoleMapper;
import com.example.authdemo.entity.Role;
import com.example.authdemo.entity.User;
import com.example.authdemo.exception.AuthException;
import com.example.authdemo.util.DesUtil;

import java.util.List;

public class AuthService {

    private final static String PASSWORD_KEY = "20220926";



    private RoleMapper roleMapper;

    private UserMapper userMapper;

    private UserRoleMapper userRoleMapper;

    private static AuthService authService = new AuthService();

    private AuthService() {
        roleMapper = RoleMapper.getInstance();
        userMapper = UserMapper.getInstance();
        userRoleMapper = UserRoleMapper.getInstance();
    }

    public static AuthService getInstance() {
        return authService;
    }


    /**
     * 创建用户addUser
     * @param userName -- 用户名
     * @param password -- 密码，允许密码为空
     * @Return 处理结果信息authResult
     */
    public AuthResult addUser(String userName, String password) {
        if (null == userName) return AuthResult.PARAMETER_NULL;

        //查是否已存在
        User existUser = userMapper.get(userName);
        if (null != existUser) {
            //已存在
            return AuthResult.USER_ALREADY_EXIST;
        }

        //构建新用户
        try {
            User newUser = new User(userName);
            newUser.setPassword(DesUtil.encrypt(PASSWORD_KEY, password));

            userMapper.insert(newUser);
            return AuthResult.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthResult.UNKONWN_ERROR;
        }
    }


    /***
     * 删除用户
     * @param userName -- 用户名
     * @return 处理结果信息authResult
     */
    public AuthResult delUser(String userName) {
        if (null == userName) return AuthResult.PARAMETER_NULL;

        try {
            int res = userMapper.delete(userName);
            if (res == 1) {
                return AuthResult.SUCCESS;
            }
            return AuthResult.USER_NOTEXIST;
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthResult.UNKONWN_ERROR;
        }
    }


    /***
     * 创建角色
     * @param roleName -- 角色名
     * @return 处理结果信息authResult
     */
    public AuthResult addRole(String roleName) {
        if (null == roleName) return AuthResult.PARAMETER_NULL;

        //查是否已存在
        Role existRole = roleMapper.get(roleName);
        if (null != existRole) {
            //已存在
            return AuthResult.ROLE_ALREADY_EXIST;
        }

        //构建新用户
        try {
            Role newRole = new Role(roleName);

            roleMapper.insert(newRole);
            return AuthResult.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthResult.UNKONWN_ERROR;
        }
    }


    /***
     * 删除角色
     * @param roleName -- 角色名
     * @return 处理结果信息authResult
     */
    public AuthResult delRole(String roleName) {
        if (null == roleName) return AuthResult.PARAMETER_NULL;

        try {
            int res = roleMapper.delete(roleName);
            if (res == 1) {
                return AuthResult.SUCCESS;
            }
            return AuthResult.ROLE_NOTEXIST;
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthResult.UNKONWN_ERROR;
        }
    }


    /***
     * 给用户授角色
     * 如果用户已经拥有该角色的信息，需要正常返回
     * @param userName 用户名
     * @param roleName 角色名
     * @return 处理结果信息authResult
     */
    public AuthResult grantRoleTouser(String userName, String roleName) {
        return null;
    }


    /***
     * 登录/用户授权
     * @param userName 用户名
     * @param password 密码
     * @return 返回一个设定好的authToken，暂定UUID
     * 可能会抛出异常
     */
    public String authenticate(String userName, String password) {
        return null;
    }


    /***
     * 登出/用户授权失效
     * @param authToken token的名字
     */
    public void invalidate(String authToken) {

    }


    /***
     * 验证角色，验证指定token对应的角色是否
     * @param authToken token的名字
     * @param roleName 角色的名字
     * @return true--token对应的用户有这个角色，false--token对应的用户没有这个角色
     * @throws AuthException 异常信息，token已经失效的也会在这里抛出异常
     */
    public boolean checkRole(String authToken, String roleName) throws AuthException {
        return false;
    }



    /***
     * 根据用户token，查该用户有哪些list
     * @param authToken
     * @return 返回该用户所有的角色信息，是一个list
     * @throws AuthException 异常信息，token已经失效的也会在这里抛出异常
     */
    public List<String> getUserRole(String authToken) throws AuthException {
        return null;
    }
}
