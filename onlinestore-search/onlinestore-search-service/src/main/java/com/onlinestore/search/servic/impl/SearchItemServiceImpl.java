package com.onlinestore.search.servic.impl;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.pojo.SearchItem;
import com.onlinestore.search.mapper.SearchItemMapper;
import com.onlinestore.search.service.SearchItemService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SearchItemServiceImpl implements SearchItemService {

    @Autowired
    private SearchItemMapper searchItemMapper;
    @Autowired
    private SolrServer solrServer;
    @Override//根据sql语句查询出来的所有的结果放入到索引库中
    public OnlinStoreResult importItemIntoIndex() {
        try {
            List<SearchItem> itemList = searchItemMapper.getItemList();
            for(SearchItem searchItem : itemList){
                SolrInputDocument solrDocument = new SolrInputDocument();
                solrDocument.addField("id", searchItem.getId());
                solrDocument.addField("item_title", searchItem.getTitle());
                solrDocument.addField("item_sell_point",searchItem.getSell_point());
                solrDocument.addField("item_price",searchItem.getPrice());
                solrDocument.addField("item_image",searchItem.getImage());
                solrDocument.addField("item_category_name",searchItem.getCategory_name());
                solrDocument.addField("item_desc",searchItem.getItem_desc());
                solrServer.add(solrDocument);
            }
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return OnlinStoreResult.build(500,"导入索引库失败!");
        }
        return OnlinStoreResult.ok();
    }
}
