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
| id | long | 用户id |  
| password | String | 密码（加密形式） |  
| lastAuthTime | Date | 上次授权时间 |  
| authToken | String | 状态授权令牌 |  
| updateTime | Date | 更新时间 |  

+ 用户实体存储在userMap中，key为name角色名，value为User这个对象实例  
+ 需要有一个atomicLong当做自增序列对象来给新增用户授予ID  
+ 调用类UserMapper，单例
+ 如果后续需要持久化，可以用id字段作为主键  
+ 根据现有需求接口，我们还需要设法建立一个Token和用户的关系，

| 容器名 | 类型 | 说明 |  
| ------ | ------ | ------ |  
| dataMap | Map<String, User> | 存储name对应的用户信息，key--用户名，value--对应的用户信息 |  
| validTokenMap | Map<String, User> | 当前生效的token信息，key--token，value--对应的用户信息 |  
  



2. 角色实体Role

| 字段名 | 类型 | 说明 |  
| ------ | ------ | ------ |  
| name | String | **角色名，key** |  
| id | long | 角色id |  
| updateTime | Date | 更新时间 |  

+ 角色实体存储在roleMap中，key为name角色名，value为Role这个对象实例  
+ 需要有一个atomicLong当做自增序列对象来给新增用户授予ID  
+ 调用类RoleMapper，单例
+ 如果后续需要持久化，可以用id字段作为主键  

| 容器名 | 类型 | 说明 |  
| ------ | ------ | ------ |  
| dataMap | Map<String, Role> | 存储name对应的角色信息，key--角色名，value--对应的角色信息 |  


3. 用户-角色之间的关系  
+ 角色和用户之间是多对多的关系，因此构成了两层关系。  
  - 用户对角色  
  - 角色对用户  
+ 根据现有的需求来看，角色对用户的关系可以不维护  
  - 删除角色的时候，可以延迟删除对应用户中的角色信息  
  - 每次查询用户角色的时候，去roleMap中做一下查询即可  

| 容器名 | 类型 | 说明 |  
| ------ | ------ | ------ |  
| userRoleMap | Map<String, Set<Long>> | 该用户共有哪些角色ID，key--用户名，value--角色集合 |  

+ 调用类UserRoleMapper，单例






## 接口设计
+ 对外层接口全部在接口authService中定义，有一个authServiceImpl的实现类拉实现
+ 创建用户addUser，接口原型addUser(String userName, String password)
  - userName, 指代用户名
  - password, 指代密码
  - 异常情况：1. 用户已存在；2. 其他异常情况
+ 删除用户delUser，接口原型delUser(String userName)
  - userName, 指代用户名
  - 异常情况：1. 用户不存在
+ 创建角色addRole，接口原型addRole(String roleName)
  - roleName，指代角色名
  - 异常情况：1. 角色已存在
+ 删除角色delRole，接口原型delRole(String roleName)
  - roleName，指代角色名
  - 异常情况：1. 角色不存在
+ 给用户授角色grantRoleToUser，接口原型grantRoleToUser(String userName, String roleName)
  - userName, 指代用户名
  - roleName，指代角色名
  - 如果用户已经拥有该角色的信息，需要正常返回
  - 异常情况：1. 用户不存在；2. 角色不存在
+ 授权authenticate，接口原型authenticate(String userName, String password)
  - userName，指代用户名
  - password，指代密码
  - 返回一个设定好的authToken，暂定UUID
  - 异常情况：1. 用户名或密码不正确
+ 取消授权invalidate，接口原型invalidate(String authToken);
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
主要分三层


## 测试用数据
| 用户 | 密码 | 角色 |  
| ------ | ------ | ------ |  
| enterprise | CV-6 | admin |  
| yorktown | CV-5 |  |  
| hornet | CV-8 | officer |  

1. 基本逻辑验证
+ 用户-角色的增删查功能

2. 授权令牌和角色的相互确认
+ 场景一：失败授权场景的测试，各种参数不正确
+ 场景二：正常获取令牌然后执行
+ 场景三：失败的获取token过后，旧token依旧可以正常使用
+ 场景四：失败的失效操作后，旧有token依旧可以正常使用
+ 场景五：给现有token失效后，就有的token变成无效的状态
+ 场景六：现有token被顶替后，旧有的token变成无效的状态
+ 场景七：验证超时失效的情况
+ 场景八：如果角色被删除，那么对应的角色信息也定然找不到，即使后面加了同名的角色，也不行
+ 场景九：如果用户被删除，那么对应的token会自动失效
+ 场景十：这里允许空密码的用户登录

3. 获取所有角色
+ 场景一：正常功能验证
+ 场景二：添加几个新角色，能够正常获取全部信息
+ 场景三：删除其中一个角色，能够正常把相关用户的信息都删除
+ 场景四：无角色的用户，返回一个空队列
+ 场景五：新增被删除的同名角色，不会影响到数据

