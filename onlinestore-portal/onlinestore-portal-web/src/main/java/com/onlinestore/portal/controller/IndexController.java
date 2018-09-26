package com.onlinestore.portal.controller;

import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.content.service.ContentService;
import com.onlinestore.pojo.TbContent;
import com.onlinestore.portal.pojo.AD1Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {//在展示首页之前，先对首页需要的数据进行处理
    @Autowired
    private ContentService contentService;
    @Value("${AD1_CATEGORY_ID}")
    private Long AD1_CATEGORY_ID;
    @Value("${AD1_WIDTH}")
    private Integer AD1_WIDTH;
    @Value("${AD1_WIDTH_B}")
    private Integer AD1_WIDTH_B;
    @Value("${AD1_HEIGHT}")
    private Integer AD1_HEIGHT;
    @Value("${AD1_HEIGHT_B}")
    private Integer AD1_HEIGHT_B;
    @RequestMapping("/index")
    public String showIndex(Model model){
        List<TbContent> list = contentService.getAdContentByCid(AD1_CATEGORY_ID);
        List<AD1Node> ad1List = new ArrayList<>();
        for(TbContent tbContent : list){
            AD1Node node = new AD1Node();
            node.setAlt(tbContent.getTitle());
            node.setSrc(tbContent.getPic());
            node.setSrcB(tbContent.getPic2());
            node.setHref(tbContent.getUrl());
            node.setHeight(AD1_HEIGHT);
            node.setHeightB(AD1_HEIGHT_B);
            node.setWidth(AD1_WIDTH);
            node.setWidthB(AD1_WIDTH_B);
            ad1List.add(node);
        }
        //通过modelview传键值对的方式将值传递给前台
        model.addAttribute("ad1", JsonUtils.objectToJson(ad1List));
        return "index";
    }
}
