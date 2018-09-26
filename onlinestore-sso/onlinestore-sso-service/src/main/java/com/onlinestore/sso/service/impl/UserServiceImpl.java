package com.onlinestore.sso.service.impl;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.mapper.TbUserMapper;
import com.onlinestore.pojo.TbUser;
import com.onlinestore.pojo.TbUserExample;
import com.onlinestore.sso.jedis.JedisClient;
import com.onlinestore.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper tbUserMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${USER_SESSION}")
    private String USER_SESSION;
    @Value("${EXPIRE_TIME}")
    private Integer EXPIRE_TIME;
    @Override
    public OnlinStoreResult checkData(String data, int type) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        if(type == 1){//说明data是username
            criteria.andUsernameEqualTo(data);
        }else if(type == 2){//说明data是phone
            criteria.andPhoneEqualTo(data);
        }else if (type == 3){//说明data是email
            criteria.andEmailEqualTo(data);
        }else{
            return OnlinStoreResult.build(400,"非法参数");
        }
        //开始执行查询
        List<TbUser> tbUsers = tbUserMapper.selectByExample(example);
        if(tbUsers != null && tbUsers.size() > 0){
            return OnlinStoreResult.ok(false);
        }
        return OnlinStoreResult.ok(true);
    }

    @Override
    public OnlinStoreResult register(TbUser tbUser) {
        //使用TbUser POJO对象来接收post表单提交过来的数据
        //然后进行是否为空的判断
        if(StringUtils.isBlank(tbUser.getUsername())){
            return OnlinStoreResult.build(400,"用户名不能为空");
        }
        if (StringUtils.isBlank(tbUser.getPassword())){
            return OnlinStoreResult.build(400,"密码不能为空");
        }
        //校验数据是否可能
        OnlinStoreResult result = checkData(tbUser.getUsername(), 1);
        if(!(boolean)result.getData()){
            return OnlinStoreResult.build(400,"此用户名已被使用");
        }
        //校验电话是否可用
        if(StringUtils.isNotBlank(tbUser.getPhone())){
            result = checkData(tbUser.getPhone(),2);
            if(!(boolean)result.getData()){
                return OnlinStoreResult.build(400,"此电话号码已被使用");
            }
        }
        //校验email是否可用
        if(StringUtils.isNotBlank(tbUser.getEmail())){
            result = checkData(tbUser.getEmail(),3);
            if(!(boolean) result.getData()){
                return OnlinStoreResult.build(400,"此邮箱已被使用");
            }
        }
        //到这里说明数据都是可用的，所有进行属性补全然后添加到数据库中
        tbUser.setCreated(new Date());
        tbUser.setUpdated(new Date());
        //需要对password进行MD5加密存储
        String md5Password = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(md5Password);
        tbUserMapper.insert(tbUser);
        return OnlinStoreResult.ok();
    }

    @Override
    public OnlinStoreResult login(String username, String password) {
        //判断用户名,密码是否正确
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> users = tbUserMapper.selectByExample(example);
        if(users == null || users.size() == 0){
            return OnlinStoreResult.build(400,"用户名或密码错误");
        }
        //走到这里说明用户名正确
        TbUser user = users.get(0);
        if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
            return OnlinStoreResult.build(400,"用户名或密码错误");
        }
        //走到这里说名登录成功，使用UUID生产token
        String token= UUID.randomUUID().toString();
        //为了安全把密码设置为null后再保存到redis中
        user.setPassword(null);
        //登录成功后,把token值和用户信息保存到redis中
        jedisClient.set(USER_SESSION+":"+token, JsonUtils.objectToJson(user));
        //设置过期时间
        jedisClient.expire(USER_SESSION+":"+token,EXPIRE_TIME);
        //返回token
        return OnlinStoreResult.ok(token);
    }

    @Override
    public OnlinStoreResult getUserByToken(String token) {//网页头部显示登录状态是需要用到.taotao.js中
        //根据token去redis中查询用户信息
        String json = jedisClient.get(USER_SESSION + ":" + token);
        if(StringUtils.isBlank(json)){
            return OnlinStoreResult.build(400,"用户登录已过期,请重新登录");
        }
        //如果查询到数据说明用户已经登录
        //重新设置过期时间
        jedisClient.expire(USER_SESSION+":"+token,EXPIRE_TIME);
        //把json数据转换成TbUser对象，然后使用OnlinStoreResult.ok(user);返回
        //如果直接返回redis中获取的json,则会多出转移字符"\"
        TbUser user = JsonUtils.jsonToPojo(json,TbUser.class);
        return OnlinStoreResult.ok(user);
    }

    @Override
    public OnlinStoreResult logout(String token) {
        //直接从redis中删除token对应的用户信息即可
        jedisClient.del(USER_SESSION+":"+token);
        return OnlinStoreResult.ok();
    }
}
