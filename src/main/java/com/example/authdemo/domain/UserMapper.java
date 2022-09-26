package com.example.authdemo.domain;

import com.example.authdemo.entity.User;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户信息的Mapper类
 * 因为数据是存在内存里，所以这里就是实际是存储的地方了
 * 按设计要求做成单例
 */
public class UserMapper {
    /** 存储name对应的用户信息，key--用户名，value--对应的用户信息 */
    private Map<String, User> dataMap;

    /** 当前生效的token信息 */
    private Map<String, User> validTokenMap;

    private static UserMapper userMapper = new UserMapper();

    private UserMapper() {
        dataMap = new ConcurrentHashMap<>();
        validTokenMap = new ConcurrentHashMap<>();
    }

    public static UserMapper getInstance() {
        return userMapper;
    }

    /**
     * 向数据中插入一条用户的信息
     * @Param userInfo -- 待插入的用户
     * @Return 成功插入的记录数，正常应该为1
     * */
    public int insert(User userInfo) {
        if (null == userInfo) return 0;
        if (null == userInfo.getName()) return 0;

        if (dataMap.containsKey(userInfo.getName())) {
            return 0;
        }
        dataMap.put(userInfo.getName(), userInfo);
        return 1;
    }

    /**
     * 从库中删除对应userName的信息
     * @Param userName -- 用户名
     * @Return 成功删除的记录数，正常应该是1
     * */
    public int delete(String userName) {
        if (null == userName) return 0;

        //要删除user中对应的token，否则多余的token可能会积压在内存里一直清理不掉，造成内存泄漏
        User user = dataMap.get(userName);
        if (null != user) {
            synchronized(user) {
                validTokenMap.remove(user.getAuthToken());
            }
        }

        return dataMap.remove(userName) != null ? 1 : 0;
    }

    /**
     * 根据userName查找容器内的用户对象
     * @param userName -- 用户名
     * @return 用户对象，没有则返回null
     */
    public User get(String userName) {
        return dataMap.get(userName);
    }

    /***
     * 给指定用户增加token
     * @param userName 用户名
     * @param token token信息，如果为空，相当于取消
     * @param authDate 授权时间，如果传空值，则相当于取消
     * @return token更新是否成功，0--不成功，1--成功
     */
    public int addToken(String userName, String token, Date authDate) {
        if (null == userName) return 0;

        User user = dataMap.get(userName);
        if (null == user) return 0;

        synchronized(user) {
            //后续的操作涉及到对validTokenMap和dataMap中同一个用户信息的操作
            //考虑到在并发场景下处理数据的时候，可能存在删除了旧token，新token还没插入的时候被截胡了的情况
            //  这种情况下会有两个token指向同一个user，如果后续user删除了同时把对应的token删了，剩下的那个就会一直留在validTokenMap中，造成内存泄漏
            //CAS操作不能解决，需要进行同步操作
            //同步粒度暂定是user

            //清除当前的userToken
            validTokenMap.remove(user.getAuthToken());
            //更新新的token
            validTokenMap.put(token, user);
            //更新新的Token和时间
            user.setAuthToken(token);
            user.setLastAuthTime(authDate);
        }

        return 1;
    }

    /***
     * 根据token获取指定用户
     * @param token token信息
     * @return 用户信息对象，如果有异常则返回null
     */
    public User getUserByToken(String token) {
        if (null == token) return null;

        return validTokenMap.get(token);
    }
}
