package com.onlinestore.search.servic.impl;

import com.onlinestore.common.pojo.SearchResult;
import com.onlinestore.search.dao.SearchDao;
import com.onlinestore.search.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchDao searchDao;
    @Override
    public SearchResult search(String queryString, int page, int rows)  {
        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        //设置每次查询的起始索引位置
        if(page < 1){
            page = 0;
        }
        query.setStart((page - 1) * rows);
        //设置每次查询的个数
        if(rows < 1){
            rows = 1;
        }
        query.setRows(rows);
        //设置默认的搜索域
        query.set("df","item_title");
        //设置高亮显示
        query.setHighlight(true);
        query.addHighlightField("item_title");
        query.setHighlightSimplePre("<font color='red'>");
        query.setHighlightSimplePost("</font>");
        //执行查询操作
        SearchResult searchResult = null;
        try {
            searchResult = searchDao.search(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //计算查询结果的总页数
        long recordCount = searchResult.getRecordCount();
        long pages = recordCount / rows;
        //上面那个取整的操作可能出现余数，所有需要判断是否有余数
        if(recordCount % rows > 0){//如果有余数，那么pages还要多加1页
            pages++;
        }
        searchResult.setTotalPages(pages);
        return searchResult;
    }
}
