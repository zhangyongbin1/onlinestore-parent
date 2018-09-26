package com.onlinestore.sso.controller;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.utils.CookieUtils;
import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.pojo.TbUser;
import com.onlinestore.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Value("${OLS_TOKEN}")
    private String OLS_TOKEN;
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public OnlinStoreResult checkData(@PathVariable String param,@PathVariable int type){
        return userService.checkData(param,type);
    }
    @RequestMapping("/user/register")
    @ResponseBody
    public OnlinStoreResult register(TbUser tbUser){
        return userService.register(tbUser);
    }

    @RequestMapping("/user/login")
    @ResponseBody
    public OnlinStoreResult login(String username, String password, HttpServletRequest request, HttpServletResponse response){
        OnlinStoreResult result = userService.login(username, password);
        //从返回结果中取出token放入到cookie中
        //登录成功后写入cookie
        if(result.getStatus() == 200){
            CookieUtils.setCookie(request,response,OLS_TOKEN,result.getData().toString());
        }
        return result;
    }

    /*@RequestMapping("/user/token/{token}")
    @ResponseBody
    public String getUserByToken(@PathVariable String token,String callback){
        OnlinStoreResult result = userService.getUserByToken(token);
        //判断是否为jsonp请求
        if(StringUtils.isNotBlank(callback)){
            return callback+"("+ JsonUtils.objectToJson(result)+");";
        }
        return JsonUtils.objectToJson(result);
    }*/
    //Js跨域请求的第二种方式
    @RequestMapping("/user/token/{token}")
    @ResponseBody
    public Object getUserByToken(@PathVariable String token,String callback){
        OnlinStoreResult result = userService.getUserByToken(token);
        //判断是否是jsonp请求
        if(StringUtils.isNotBlank(callback)){
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            //设置回调方法
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return result;
    }
    @RequestMapping("/user/logout/{token}")
    @ResponseBody
    public Object logout(@PathVariable String token,String callback){
        OnlinStoreResult result = userService.logout(token);
        if(StringUtils.isNotBlank(callback)){
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return result;
    }

}
