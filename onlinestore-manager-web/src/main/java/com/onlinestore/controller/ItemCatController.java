package com.onlinestore.controller;

import com.onlinestore.common.pojo.EasyUITreeNode;
import com.onlinestore.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 商品类型选择属性控件controller
 */
@Controller
public class ItemCatController {
    @Autowired
    private ItemCatService itemCatService;
    @RequestMapping("/item/cat/list")
    @ResponseBody
    //@RequestParam(value="id",defaultValue = "0")Long parentId,其中id是EasyUI树形控件传过来的,因为变量名字不一样，所以需要使用这种方式
    public List<EasyUITreeNode> getItemCatList(@RequestParam(value="id",defaultValue = "0")Long parentId){
        List<EasyUITreeNode> list = itemCatService.getItemCatList(parentId);
        return list;
    }
}
