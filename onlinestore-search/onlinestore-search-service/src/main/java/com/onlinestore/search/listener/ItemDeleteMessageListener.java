package com.onlinestore.search.listener;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 接收商品删除的消息
 */
public class ItemDeleteMessageListener implements MessageListener {
    @Autowired
    private SolrServer solrServer;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            long itemId = Long.parseLong(text);
            //根据id将索引库中的document删除
            solrServer.deleteById(Long.toString(itemId));
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
