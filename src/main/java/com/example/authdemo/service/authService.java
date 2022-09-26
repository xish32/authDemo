package com.example.authdemo.service;

import com.example.authdemo.constant.AuthResult;
import com.example.authdemo.exception.AuthException;

import java.util.List;

/**
 * 授权服务接口
 * 提供以下功能
 *  - 用户的新增，删除、查询功能
 *  - 角色的新增、删除、查询功能
 *  - 用户添加指定角色的功能
 */
public interface authService {
  /**
   * 创建用户addUser
   * @param userName -- 用户名
   * @param password -- 密码
   * @Return 处理结果信息authResult
   */
  public AuthResult addUser(String userName, String password);

  /***
   * 删除用户
   * @param userName -- 用户名
   * @return 处理结果信息authResult
   */
  public AuthResult delUser(String userName);

  /***
   * 创建角色
   * @param roleName -- 角色名
   * @return 处理结果信息authResult
   */
  public AuthResult addRole(String roleName);

  /***
   * 删除角色
   * @param roleName -- 角色名
   * @return 处理结果信息authResult
   */
  public AuthResult delRole(String roleName);

  /***
   * 给用户授角色
   * 如果用户已经拥有该角色的信息，需要正常返回
   * @param userName 用户名
   * @param roleName 角色名
   * @return 处理结果信息authResult
   */
  public AuthResult grantRoleTouser(String userName, String roleName);

  /***
   * 登录/用户授权
   * @param userName 用户名
   * @param password 密码
   * @return 返回一个设定好的authToken，暂定UUID
   * 可能会抛出异常
   */
  public String authenticate(String userName, String password);

  /***
   * 登出/用户授权失效
   * @param authToken token的名字
   */
  public void invalidate(String authToken);

  /***
   * 验证角色，验证指定token对应的角色是否
   * @param authToken token的名字
   * @param roleName 角色的名字
   * @return true--token对应的用户有这个角色，false--token对应的用户没有这个角色
   * @throws AuthException 异常信息，token已经失效的也会在这里抛出异常
   */
  public boolean checkRole(String authToken, String roleName) throws AuthException;

  /***
   * 根据用户token，查该用户有哪些list
   * @param authToken
   * @return 返回该用户所有的角色信息，是一个list
   * @throws AuthException 异常信息，token已经失效的也会在这里抛出异常
   */
  public List<String> getUserRole(String authToken) throws AuthException;
}
