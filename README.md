# authDemo
一个登录认证和授权系统的demo，提供用户和角色的增删改查相关API  

在开发过程中，我们引入了JUnit4.0框架用于单元测试

+ 设计文档请参看本目录下的design.md文档

+ 主服务位于AuthService这个服务方法中
+ 我准备了一个Demo.java，用于启动并执行这个
+ 另外AuthService的测试类AuthServiceTest也可以作为调用时的参考



## 代码结构
+ src/main/java下是代码程序，包名com.example.authdemo
  - Demo类直接在authDemo下
  - constant包中保存了返回用的枚举类型，表示返回码的信息
  - domain包是领域模型，主要是操作数据层面的相关类UserMapper、RoleMapper和UserRoleMapper
  - entity包内是具体的实体pojo，包括Role和User两个类
  - exception包中有一个自定义的异常authException
  - service包中是具体的调用信息
  - util包中是工具类
+ src/test/java下是对应的单元测试代码  

## 提供接口
具体定义可以参见design.md
+ 创建用户addUser(String userName, String password)
+ 删除用户delUser(String userName)
+ 创建角色addRole(String roleName)
+ 删除角色delRole(String roleName)
+ 给用户授角色grantRoleToUser(String userName, String roleName)
+ 授权authenticate(String userName, String password)
+ 取消授权invalidate(String authToken);
+ 验证角色checkRole(String authToken，String roleName)
+ 查所有角色getUserRole(String authToken)

此外，工具类中还有三个参数，有对应的getter和setter可以获取
+ encryptKey，密码加密用的秘钥，要求八位以上的字符串
+ delayTimes，延迟时间，值
+ delayTimeunit，延迟时间的单位，用Calendar中的对应单位

## 返回码
+ 000000 成功  
+ 000001 用户不存在  
+ 000002 角色不存在  
+ 000003 用户名或密码不正确  
+ 000004 TOKEN不正确或已过期  
+ 000005 输入参数为空  

