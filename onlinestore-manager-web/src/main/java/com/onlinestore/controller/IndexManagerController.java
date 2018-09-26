package com.onlinestore.controller;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.search.service.SearchItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class IndexManagerController {

    @Autowired
    private SearchItemService searchItemService;
    @RequestMapping("/index/import")
    @ResponseBody
    public OnlinStoreResult importIndex(){
        return searchItemService.importItemIntoIndex();
    }
}
