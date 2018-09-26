package com.onlinestore.controller;

import com.onlinestore.common.pojo.EasyUIDataGridResult;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.pojo.TbItem;
import com.onlinestore.pojo.TbItemDesc;
import com.onlinestore.pojo.TbItemParamItem;
import com.onlinestore.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable long itemId){
        TbItem tbItem = itemService.getItemById(itemId);
        return tbItem;
    }

    @RequestMapping("/item/list")
    @ResponseBody //响应的是json数据格式
    //page和rows两个参数是由前端EasyUI请求url中提供的
    public EasyUIDataGridResult getItemList(Integer page, Integer rows){
        EasyUIDataGridResult result = itemService.getItemList(page,rows);
        return result;
    }

    @RequestMapping("/item/save")
    @ResponseBody
    public OnlinStoreResult saveItem(TbItem item, String desc){
        return itemService.addItem(item,desc);
    }

    @RequestMapping("/item/desc/{itemId}")
    @ResponseBody
    public OnlinStoreResult getItemDescById(@PathVariable long itemId){
        return itemService.getItemDescById(itemId);
    }

    @RequestMapping("/item/param/{itemId}")
    @ResponseBody
    public OnlinStoreResult getItemParamItemById(@PathVariable long itemId){
        return itemService.getItemParamItemById(itemId);
    }

    @RequestMapping("/item/update")
    @ResponseBody
    public OnlinStoreResult updateItem(TbItem item, String desc, String itemParams){
        return itemService.updateItem(item,desc,itemParams);
    }

    @RequestMapping("/item/delete")
    @ResponseBody
    public OnlinStoreResult deleteItem(long ids){
        return itemService.deleteItem(ids);
    }

    @RequestMapping("/item/instock")
    @ResponseBody
    public OnlinStoreResult instockItem(long ids){//上架
        return itemService.instockItem(ids);
    }

    @RequestMapping("/item/reshelf")
    @ResponseBody
    public OnlinStoreResult reshelfItem(long ids){//下架
        return itemService.reshelfItem(ids);
    }


}
