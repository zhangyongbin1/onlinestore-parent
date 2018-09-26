package com.onlinestore.controller;
/*用于展示后台管理系统的jsp页面*/

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/")
    public String showIndex(){
        return "index";
    }

    @RequestMapping("/{page}")//因为请求url与jsp页面的名字相同,所以请求那个页面就直接返回就行
    public String showPage(@PathVariable String page){
        return page;
    }
}
