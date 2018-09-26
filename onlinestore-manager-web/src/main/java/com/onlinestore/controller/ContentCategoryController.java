package com.onlinestore.controller;

import com.onlinestore.common.pojo.EasyUITreeNode;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/content/category")
@Controller
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;
    @RequestMapping("/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCatList(@RequestParam(value = "id",defaultValue = "0") long parentId){
            return contentCategoryService.getContentCategoryList(parentId);
    }

    @RequestMapping("/create")
    @ResponseBody
    public OnlinStoreResult createContentCategory(Long parentId, String name){
        return contentCategoryService.addContentCategory(parentId,name);
    }

    @RequestMapping("/update")
    @ResponseBody
    public OnlinStoreResult updateContentCategory(Long id, String name){
        return contentCategoryService.updateContentCategory(id,name);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public OnlinStoreResult deleteContentCategory(Long id){
        return contentCategoryService.deleteContentCategory(id);
    }
}
