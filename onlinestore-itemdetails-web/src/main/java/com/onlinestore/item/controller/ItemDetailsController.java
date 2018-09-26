package com.onlinestore.item.controller;

import com.onlinestore.item.pojo.Item;
import com.onlinestore.pojo.TbItem;
import com.onlinestore.pojo.TbItemDesc;
import com.onlinestore.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemDetailsController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    public String showItem(@PathVariable long itemId, Model model){
        //根据商品Id查询商品
        TbItem tbItem = itemService.getItemById(itemId);
        Item item = new Item(tbItem);
        //获取商品详情
        TbItemDesc itemDesc =(TbItemDesc) itemService.getItemDescById(itemId).getData();
        model.addAttribute("item",item);
        model.addAttribute("itemDesc",itemDesc);
        //返回逻辑视图
        return "item";
    }
}
