package com.onlinestore.search.controller;

import com.onlinestore.common.pojo.SearchResult;
import com.onlinestore.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Value("${SEARCH_RESULT_ROWS}")
    private Integer SEARCH_RESULT_ROWS;
    @RequestMapping("/search")
    //@ResponseBody,如果返回json数据，那么就使用这个注解
    //返回视图渲染，就直接返回jsp页面的名字string
    public String search(@RequestParam("q") String queryString, @RequestParam(defaultValue = "1") Integer page, Model model) throws Exception{
        //测试全局异常处理器
        //int i = 1/0;
        //把查询条件先转码
        queryString = new String(queryString.getBytes("iso8859-1"),"utf-8");
        SearchResult searchResult = searchService.search(queryString, page, SEARCH_RESULT_ROWS);
        //把结果传递给页面
        model.addAttribute("query",queryString);
        model.addAttribute("totalPages",searchResult.getTotalPages());
        model.addAttribute("itemList",searchResult.getItemList());
        model.addAttribute("page",page);
        return "search";//返回逻辑视图
    }
}
