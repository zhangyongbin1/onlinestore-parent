import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 测试solrJ的API方法
 */
public class TestSolrJ {
    @Test
    public void addDocument() throws IOException, SolrServerException {
        //第一步把solrJ的jar包添加到工程中
        //第二步创建一个solrserver,使用httpSolrServer创建对象
        SolrServer solrServer = new HttpSolrServer("http://192.168.26.131:8180/solr/collection2_shard1_replica2");
        //创建一个文档对象SolrInputDocument
        SolrInputDocument document = new SolrInputDocument();
        //向文档中添加域。必须有id域，域的名称必须在schema.xml中定义
        document.addField("id","test00002");
        document.addField("item_title","测试商品22");
        document.addField("item_price","2002");
        //把文档对象添加到索引库中
        solrServer.add(document);
        solrServer.commit();
    }
    @Test
    public void deleteDocumentById() throws Exception{
        SolrServer solrServer = new HttpSolrServer("http://192.168.26.131:8180/solr");
        solrServer.deleteById("test002");
        solrServer.commit();
    }

    @Test
    public void queryDocument() throws Exception {
        SolrServer solrServer = new HttpSolrServer("http://192.168.26.131:8180/solr");
        SolrQuery query = new SolrQuery();
        //向solrquery对象中添加查询条件、过滤条件
        query.setQuery("*:*");
        //执行查询条件
        QueryResponse response = solrServer.query(query);
        //取查询结果
        SolrDocumentList solrDocumentList = response.getResults();
        System.out.println(solrDocumentList.getNumFound());
        for(SolrDocument solrDocument : solrDocumentList){
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("item_title"));
            System.out.println(solrDocument.get("item_price"));
        }

    }

    @Test
    public void queryDocumentWithHighLight() throws Exception {
        SolrServer solrServer = new HttpSolrServer("http://192.168.26.131:8080/solr");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("商品");
        //指定默认搜索域
        solrQuery.set("df","item_keywords");
        //开启高亮显示
        solrQuery.setHighlight(true);
        //高亮显示的域
        solrQuery.addHighlightField("item_title");
        solrQuery.addHighlightField("<em>");
        solrQuery.addHighlightField("</em>");
        //执行查询
        QueryResponse response = solrServer.query(solrQuery);
        //获取查询结果
        SolrDocumentList solrDocumentList= response.getResults();
        System.out.println(solrDocumentList.getNumFound());
        for(SolrDocument solrDocument : solrDocumentList){
            System.out.println(solrDocument.get("id"));
            //取高亮显示结果
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String itemTitle = null;
            if(list != null && list.size() > 0){
                itemTitle = list.get(0);
            }else{
                itemTitle =(String) solrDocument.get("item_title");
            }

            System.out.println(itemTitle);
            System.out.println(solrDocument.get("item_price"));

        }
    }

}
