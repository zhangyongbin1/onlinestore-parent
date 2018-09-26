package com.onlinestore.search.listener;

import com.onlinestore.common.pojo.SearchItem;
import com.onlinestore.search.mapper.SearchItemMapper;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 接收商品修改的消息
 */
public class ItemUpdateMessageListener implements MessageListener {
    @Autowired
    private SearchItemMapper searchItemMapper;
    @Autowired
    private SolrServer solrServer;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            long itemId = Long.parseLong(text);
            //根据商品Id进行索引的对应商品的更新操作
            //等待事务提交
            Thread.sleep(1000);
            SearchItem searchItem = searchItemMapper.getItemById(itemId);
            //将searchItem同步到索引库中
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id",searchItem.getId());
            document.addField("item_title",searchItem.getTitle());
            document.addField("item_sell_point",searchItem.getSell_point());
            document.addField("item_price",searchItem.getPrice());
            document.addField("item_image",searchItem.getImage());
            document.addField("item_category_name",searchItem.getCategory_name());
            document.addField("item_desc",searchItem.getItem_desc());
            solrServer.add(document);
            //提交
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
