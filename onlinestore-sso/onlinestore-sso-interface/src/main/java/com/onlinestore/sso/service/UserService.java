package com.onlinestore.sso.service;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.pojo.TbUser;

/**
 * 单点登录系统的用户服务
 */
public interface UserService {
    //根据前端传递过来的参数值和参数的类型对数据进行动态地判断是否可用
    OnlinStoreResult checkData(String data, int type);
    //用户注册功能
    OnlinStoreResult register(TbUser tbUser);
    //用户登录
    OnlinStoreResult login(String username, String password);
    //通过token查询用户信息,用于使用Ajxax进行跨域请求显示用户登录信息
    OnlinStoreResult getUserByToken(String token);
    //用户退出登录
    OnlinStoreResult logout(String token);
}
