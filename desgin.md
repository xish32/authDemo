## 设计目标

设计一个登录认证和授权系统的demo，提供用户和角色的增删改查相关API  
不需要数据库设计，所有的用户权限数据信息都在内存中  
涉及的接口  
+ 用户的创建和删除  
+ 角色的创建和删除  
+ 给用户授予指定的角色  
+ 用户的登录和登出  
+ 验证用户是否拥有某个角色  
+ 展示用户的所有角色  


## 数据设计  
系统的主要实体有两个，用户和角色  
对应关系是多对多的，一个用户可能有多个角色，反过来也是如此  
因为不存储到数据库层面，所以计划将数据暂存于内存的Map中


1. 用户实体User

| 字段名 | 类型 | 说明 |  
| ------ | ------ | ------ |  
| name | String | **用户名（登录名），key** |  
| id | Long | 用户id |  
| password | String | 密码（加密形式） |  
| lastAuthTime | Date | 上次授权时间 |  
| token | String | 状态授权令牌 |  
| updateTime | Date | 更新时间 |  

+ 用户实体存储在userMap中，key为name角色名，value为User这个对象实例  
+ 需要有一个atomicLong当做自增序列对象来给新增用户授予ID  
+ 如果后续需要持久化，可以用id字段作为主键  


2. 角色实体Role

| 字段名 | 类型 | 说明 |  
| ------ | ------ | ------ |  
| name | String | **角色名，key** |  
| id | Long | 角色ID |  
| updateTime | Date | 更新时间 |  

+ 角色实体存储在roleMap中，key为name角色名，value为Role这个对象实例  
+ 需要有一个atomicLong当做自增序列对象来给新增用户授予ID  
+ 如果后续需要持久化，可以用id字段作为主键  

3. 用户-角色之间的关系  
+ 角色和用户之间是多对多的关系，因此需要有两个map来维护这层关系。

| 容器名 | 类型 | 说明 |  
| ------ | ------ | ------ |  
| userRoleMap | Map<String, Set<Long>> | 该用户共有哪些角色ID，key--用户名，value--角色集合 |  
| roleUserMap | Map<String, Set<Long>> | 该角色下有哪些用户ID，key--角色名，value--用户集合 |  

+ 补充：可以考虑延迟加载



## 接口设计
+ 创建用户addUser，接口原型addUser(String userName, String password)
  - userName, 指代用户名
  - password, 指代密码
  - 异常情况：1. 用户已存在
+ 删除用户delUser，接口原型delUser(String userName)
  - userName, 指代用户名
  - 异常情况：1. 用户不存在
+ 创建角色addRole，接口原型addRole(String roleName)
  - roleName，指代角色名
  - 异常情况：1. 角色已存在
+ 删除角色delRole，接口原型delRole(String roleName)
  - roleName，指代角色名
  - 异常情况：1. 角色不存在
+ 给用户授角色grantRoleToUser，接口原型grantRoleTouser(String userName, String roleName)
  - userName, 指代用户名
  - roleName，指代角色名
  - 如果用户已经拥有该角色的信息，需要正常返回
  - 异常情况：1. 用户不存在；2. 角色不存在
+ 登录login，接口原型login(String userName, String password)
  - userName，指代用户名
  - password，指代密码
  - 返回一个设定好的authToken，暂定UUID
  - 异常情况：1. 用户名或密码不正确
+ 登出logout，接口原型logout(String authToken);
  - authToken，指代token的名字
  - 异常情况：token不正确
  - returns nothing, the token is no longer valid after the call.  Handles correctly the case of invalid token given as input
+ 验证角色checkRole，接口原型checkRole(String authToken，String roleName)
  - authToken，指代token
  - roleName，指代角色名
  - 返回一个true或false的值
  - returns true if the user, identified by the token, belongs to the role, false otherwise; error if token is invalid expired etc.
+ 查所有角色getUserRole，接口原型getUserRole(String authToken)
  - authToken，指代token
  - 返回该用户所有的角色信息
  - 异常情况：1. token异常
  - returns all roles for the user, error if token is invalid.




## 调用逻辑设计  



## 测试场景
1. 添加用户->用户登录->
2. 角色的添加和删除
3. 
