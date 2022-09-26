package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.domain.RoleMapper;
import com.example.authdemo.domain.UserMapper;
import com.example.authdemo.domain.UserRoleMapper;
import com.example.authdemo.entity.Role;
import com.example.authdemo.entity.User;
import com.example.authdemo.exception.AuthException;
import com.example.authdemo.util.DateUtil;
import com.example.authdemo.util.DesUtil;
import com.example.authdemo.util.StringUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuthService {

    private final static String PASSWORD_KEY = "20220926";

    private final static int DELAY_TIMES = 2;
    private final static int DELAY_TIMEUNIT = Calendar.HOUR_OF_DAY;


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
    public AuthResult grantRoleToUser(String userName, String roleName) {
        if ((null == userName) || (null == roleName)) return AuthResult.PARAMETER_NULL;

        try {
            //先查角色是否存在
            Role newRole = roleMapper.get(roleName);
            if (null == newRole) {
                return AuthResult.ROLE_NOTEXIST;
            }

            long roleId = newRole.getId();

            //先检查该用户-角色信息是否存在
            // 已经存在的，要按照成功来计算
            if (userRoleMapper.hasRoleFromUser(userName, roleId))
                return AuthResult.SUCCESS;

            userRoleMapper.addRoleToUser(userName, roleId);
            return AuthResult.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthResult.UNKONWN_ERROR;
        }
    }


    /***
     * 登录/用户授权
     * @param userName 用户名
     * @param password 密码
     * @return 返回一个设定好的authToken，暂定UUID
     * 可能会抛出异常
     */
    public String authenticate(String userName, String password) throws AuthException{
        if (null == userName)
            throw new AuthException(AuthResult.PARAMETER_NULL, null);

        try {
            //检查用户是否存在，密码是否正确
            User curUser = userMapper.get(userName);
            if (null == curUser)
                throw new AuthException(AuthResult.USER_NOTEXIST, null);

            String decPassword = DesUtil.decrypt(PASSWORD_KEY, curUser.getPassword());
            if (!StringUtil.equals(decPassword, password)) {
                throw new AuthException(AuthResult.INVALID_PASSWORD, null);
            }

            //生成新的token
            String newToken = java.util.UUID.randomUUID().toString();
            Date updateTime = new Date();
            Date expireTime = DateUtil.getAddTime(updateTime, DELAY_TIMES, DELAY_TIMEUNIT);
            userMapper.addToken(userName, newToken, updateTime, expireTime);
            return newToken;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthException(AuthResult.UNKONWN_ERROR, ex);
        }
    }


    /***
     * 登出/用户授权失效
     * @param authToken token的名字
     */
    public void invalidate(String authToken) {
        if (null == authToken) return ;

        try {
            //检查用户是否存在，密码是否正确
            User curUser = userMapper.getUserByToken(authToken);
            if (null == curUser) return;

            userMapper.addToken(curUser.getName(), null, new Date(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
