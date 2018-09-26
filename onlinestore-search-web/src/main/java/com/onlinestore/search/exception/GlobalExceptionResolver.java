package com.onlinestore.search.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalExceptionResolver implements HandlerExceptionResolver {
    //加载logger对象
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        logger.info("进入全局异常处理器......");
        logger.debug("测试handler的类型"+handler.getClass());
        //控制台打印异常
        e.printStackTrace();
        //向日志文件中写入异常
        logger.error("系统发生异常",e);
        //发邮件 jmail工具
        //发短信
        //展示错误页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message","您的电脑有问题,请稍后再试");
        modelAndView.setViewName("error/exception");
        return modelAndView;
    }
}
