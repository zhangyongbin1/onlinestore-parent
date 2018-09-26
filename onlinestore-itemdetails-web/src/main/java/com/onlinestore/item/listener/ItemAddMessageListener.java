package com.onlinestore.item.listener;

import com.onlinestore.item.pojo.Item;
import com.onlinestore.pojo.TbItem;
import com.onlinestore.pojo.TbItemDesc;
import com.onlinestore.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收商品添加时发送的消息，然后使用freemarker进行静态页面的生成
 */
public class ItemAddMessageListener implements MessageListener {
   @Autowired
   private ItemService itemService;
   @Autowired
   private FreeMarkerConfigurer freeMarkerConfigurer;
   @Value("${HTML_OUT_PATH}")
   private String HTML_OUT_PATH;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            long itemId = Long.parseLong(text);
            //等待事务提交
            Thread.sleep(1000);
            //根据商品Id查询商品数据
            TbItem tbItem = itemService.getItemById(itemId);
            Item item = new Item(tbItem);
            TbItemDesc tbItemDesc = (TbItemDesc) itemService.getItemDescById(itemId).getData();
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map data = new HashMap();
            data.put("item",item);
            data.put("itemDesc",tbItemDesc);
            Writer out = new FileWriter(new File(HTML_OUT_PATH+text+".html"));
            template.process(data,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
