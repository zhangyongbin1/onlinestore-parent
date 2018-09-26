package com.onlinestore.order.interceptor;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.utils.CookieUtils;
import com.onlinestore.pojo.TbUser;
import com.onlinestore.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在展示订单页面之前需要确认用户是否登录，使用springmvc自带的拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${TOKEN_KEY}")
    private String TOKEN_KEY;
    @Value("${SSO_URL}")
    private String SSO_URL;
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 执行handler之前执行的方法
        //请求URL中包含order那么就会被这个拦截器拦截,执行这个方法
        //1.从cookie中取token信息
        String token = CookieUtils.getCookieValue(request, TOKEN_KEY);
        //如果取不到token说明用户没有登录，需要跳转到sso登录页面，需要把当前请求的url作为参数传递给sso,sso登录成功后跳转回请求的页面
        if(StringUtils.isBlank(token)){
            //获取当前请求的url
            String requestURL = request.getRequestURL().toString();
            //跳转到登录页面
            response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
            //返回false表示进行拦截
            return false;
        }
        //调用sso 服务判断用户的登录状态,因为用户的登录信息放在redis中并设置了半小时的过期时间
        OnlinStoreResult result = userService.getUserByToken(token);
        if(result.getStatus() != 200){
            //获取当前请求的url
            String requestURL = request.getRequestURL().toString();
            //跳转到登录页面
            response.sendRedirect(SSO_URL+"/page/login?url="+requestURL);
            //返回false表示进行拦截
            return false;
        }
        //如果取到用户信息，则放行
        TbUser user = (TbUser) result.getData();
        request.setAttribute("user",user);
        //返回true表示放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //handler执行之后，modelAndView返回之前
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        // 在ModelAndView返回之后，异常处理
    }
}
