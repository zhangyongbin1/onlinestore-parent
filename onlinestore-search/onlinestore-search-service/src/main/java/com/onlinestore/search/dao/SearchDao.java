package com.onlinestore.search.dao;

import com.onlinestore.common.pojo.SearchItem;
import com.onlinestore.common.pojo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现搜索功能时访问的是solr索引库，所以需要另起dao层
 */
@Repository
public class SearchDao {
    @Autowired
    private SolrServer solrServer;
    public SearchResult search(SolrQuery query) throws Exception {
        List<SearchItem> itemList = new ArrayList<>();
        SearchResult result = new SearchResult();
        //根据查询条件query进行查询
        QueryResponse response = solrServer.query(query);
        //得到查询结果
        SolrDocumentList solrDocumentList = response.getResults();
        //获取查询的总记录数
        long numFound = solrDocumentList.getNumFound();
        result.setRecordCount(numFound);
        //遍历结果集进行封装
        for(SolrDocument solrDocument : solrDocumentList){
            SearchItem searchItem = new SearchItem();
            searchItem.setCategory_name((String) solrDocument.getFieldValue("item_category_name"));
            searchItem.setId((String) solrDocument.get("id"));
            String image = (String) solrDocument.get("item_image");
            if(StringUtils.isNotBlank(image)){
                image = image.split(",")[0];
            }
            searchItem.setImage(image);
            searchItem.setPrice((long) solrDocument.get("item_price"));
            searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
            //取高亮显示
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String title = null;
            if(list != null && list.size() > 0){
                title = list.get(0);
            }else{
                title = (String) solrDocument.get("item_title");
            }
            searchItem.setTitle(title);
            itemList.add(searchItem);
        }
        result.setItemList(itemList);
        return result;
    }
}
