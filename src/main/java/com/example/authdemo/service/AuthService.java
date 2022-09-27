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

import java.util.*;

public class AuthService {

    /** 密码加密用的秘钥 */
    private String encryptKey = "20220926";

    /** 延迟时间 */
    private int delayTimes = 2;
    /** 延迟时间的单位 */
    private int delayTimeunit = Calendar.HOUR_OF_DAY;


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
            newUser.setPassword(DesUtil.encrypt(encryptKey, password));

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
            int userRes = userMapper.delete(userName);
            if (userRes != 1) {
                return AuthResult.USER_NOTEXIST;
            }
            userRoleMapper.delUser(userName);
            return AuthResult.SUCCESS;
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

            //查用户是否存在
            User curUser = userMapper.get(userName);
            if (null == curUser) {
                return AuthResult.USER_NOTEXIST;
            }

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

        //检查用户是否存在，密码是否正确
        User curUser = userMapper.get(userName);
        //找不到用户，也应该回报用户名密码错误
        if (null == curUser)
            throw new AuthException(AuthResult.INVALID_USERNAME_PASSWORD, null);

        String decPassword = DesUtil.decrypt(encryptKey, curUser.getPassword());
        if (!StringUtil.equals(decPassword, password)) {
            throw new AuthException(AuthResult.INVALID_USERNAME_PASSWORD, null);
        }
        try {

            //生成新的token
            String newToken = java.util.UUID.randomUUID().toString();
            Date updateTime = new Date();
            Date expireTime = DateUtil.getAddTime(updateTime, delayTimes, delayTimeunit);
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
            //检查用户是否存在
            User curUser = userMapper.getUserByToken(authToken);
            if (null == curUser) return;

            userMapper.cleanToken(authToken);
            userMapper.addToken(curUser.getName(), null, new Date(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /***
     * 验证角色，验证指定token对应的角色是否
     * @param authToken token的名字
     * @param roleName 角色的名字
     * @return true--token对应的用户有这个角色，false--token对应的用户没有这个角色，角色不存在等
     * @throws AuthException 异常信息，token已经失效的也会在这里抛出异常
     */
    public boolean checkRole(String authToken, String roleName) throws AuthException {
        if ((null == authToken) || (null == roleName))
            throw new AuthException(AuthResult.PARAMETER_NULL, null);

        // 检查角色是否存在
        Role curRole = roleMapper.get(roleName);
        if (null == curRole) return false;

        //检查用户是否存在
        User curUser = checkTokenUser(authToken);

        try {
            return userRoleMapper.hasRoleFromUser(curUser.getName(), curRole.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthException(AuthResult.UNKONWN_ERROR, ex);
        }
    }



    /***
     * 根据用户token，查该用户有哪些list
     * @param authToken
     * @return 返回该用户所有的角色信息，是一个list
     * @throws AuthException 异常信息，token已经失效的也会在这里抛出异常
     */
    public List<String> getUserRole(String authToken) throws AuthException {
        if ((null == authToken))
            throw new AuthException(AuthResult.PARAMETER_NULL, null);

        //检查用户是否存在
        User curUser = checkTokenUser(authToken);

        try {
            List<Long> roleIds = userRoleMapper.getAllRolesFromUser(curUser.getName());
            List<Long> roleIdToDelete = new ArrayList<>();
            List<String> roleNameList = new ArrayList<>();
            for (Long roleId : roleIds) {
                //要顺便清理
                String roleName = roleMapper.getRoleNameById(roleId);
                if (null != roleName) {
                    roleNameList.add(roleName);
                } else {
                    roleIdToDelete.add(roleId);
                }
            }
            userRoleMapper.delMultiRolesFromUser(curUser.getName(), roleIdToDelete);

            return roleNameList;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthException(AuthResult.UNKONWN_ERROR, ex);
        }
    }

    private User checkTokenUser(String authToken) throws AuthException {
        User curUser = userMapper.getUserByToken(authToken);
        if (null == curUser) throw new AuthException(AuthResult.INVALID_TOKEN, null);

        if (!StringUtil.equals(authToken, curUser.getAuthToken())) {
            //提供的token的指向不正确，清理token
            //但是不需要清理userMapper
            System.out.println("TOKEN指向了错误的用户");
            userMapper.cleanToken(authToken);
            throw new AuthException(AuthResult.INVALID_TOKEN, null);
        }

        //检查token是否过期
        Date now = new Date();
        Date expireDate = curUser.getTokenExpireTime();
        if ((null == expireDate) || (now.after(expireDate))) {
            userMapper.cleanToken(authToken);
            userMapper.addToken(curUser.getName(), null, now, null);
            throw new AuthException(AuthResult.INVALID_TOKEN, null);
        }
        return curUser;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    /***
     * 设置加密秘钥
     * @param encryptKey 加密密钥
     */
    public void setEncryptKey(String encryptKey) {
        //小于8的数据是无效的
        if (encryptKey.length() >= 8)
            this.encryptKey = encryptKey;
    }

    public int getDelayTimes() {
        return delayTimes;
    }

    public void setDelayTimes(int delayTimes) {
        this.delayTimes = delayTimes;
    }

    public int getDelayTimeunit() {
        return delayTimeunit;
    }

    public void setDelayTimeunit(int delayTimeunit) {
        this.delayTimeunit = delayTimeunit;
    }

}
