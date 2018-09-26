package com.onlinestore.content.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinestore.common.pojo.EasyUIDataGridResult;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.content.service.ContentService;
import com.onlinestore.jedis.JedisClient;
import com.onlinestore.mapper.TbContentMapper;
import com.onlinestore.pojo.TbContent;
import com.onlinestore.pojo.TbContentExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper tbContentMapper;
    @Autowired
    private JedisClient jedisClientCluster;
    @Value("$(INDEX_CONTENT)")
    private String INDEX_CONTENT;
    @Override
    public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
        //设置分页信息
        PageHelper.startPage(page,rows);
        //根据categoryId查询Tbcontent表
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
//        List<TbContent> tbContentList = tbContentMapper.selectByExample(example);
        //因为content在tbcontentMapper.xml中是单词进行定义的一种字段，所以需要使用这种方式查询
        List<TbContent> tbContentList = tbContentMapper.selectByExampleWithBLOBs(example);
        PageInfo<TbContent> pageInfo = new PageInfo(tbContentList);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(tbContentList);
        return result;
    }

    @Override
    public OnlinStoreResult addContent(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        tbContentMapper.insert(tbContent);
        //同步缓存，需要将缓存中的对应信息删除
        jedisClientCluster.hdel(INDEX_CONTENT,Long.toString(tbContent.getCategoryId()));
        return OnlinStoreResult.ok();
    }

    @Override
    public OnlinStoreResult editContent(TbContent tbContent) {
        //补全tbcontent的属性
        tbContent.setUpdated(new Date());
        tbContentMapper.updateByPrimaryKeySelective(tbContent);
        //同步缓存中的信息，直接将缓存中的信息直接删除即可
        jedisClientCluster.hdel(INDEX_CONTENT,Long.toString(tbContent.getCategoryId()));
        return OnlinStoreResult.ok();
    }

    @Override
    public OnlinStoreResult deleteContent(Long ids) {
        //先获取cid
        TbContent tbContent = tbContentMapper.selectByPrimaryKey(ids);
        //根据id删除content,同时也要删除缓存中的数据
        tbContentMapper.deleteByPrimaryKey(ids);
        //同步缓存中的信息，直接将缓存中的信息直接删除即可
        jedisClientCluster.hdel(INDEX_CONTENT,Long.toString(tbContent.getCategoryId()));
        return OnlinStoreResult.ok();
    }

    /**
     * 优化：由于首页用户访问量比较大，所以为了减轻数据库服务器的压力，可以使用redis服务器作缓存
     * 向redis中添加缓存：Key=cid,value=内容列表，需要转化成json
     *不设置过期时间是因为首页页面点击率比价高
     * 使用hash对key进行归类：
     *  Hash_Key:"hash"
     *          |-key:value
     *          |-key:value
     *          |-key:value
     *          ....
     *    注意：添加缓存不能影响正常业务逻辑
     * @param cid
     * @return
     */
    @Override
    public List<TbContent> getAdContentByCid(Long cid) {//首页打广告轮播图实现的service
        //根据内容分类ID查询Tbcontent
        try {
            //向数据库查询之前先看缓存中是否存在，加try catch模块是为了不影响正常业务逻辑
            String json = jedisClientCluster.hget(INDEX_CONTENT,Long.toString(cid));
            //判断json是否为空
            if(StringUtils.isNotBlank(json)){//说明缓存中已经存在，则直接从缓存中取
                //将json转换成List<TbContent>返回
                List<TbContent> result = JsonUtils.jsonToList(json,TbContent.class);
                return result;//直接返回查询结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //走到这里说明缓存中不存在，所以就从数据库中查
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);
        List<TbContent> result = tbContentMapper.selectByExample(example);
        //查询完了后，将其添加到缓存中
        try {
            jedisClientCluster.hset(INDEX_CONTENT,Long.toString(cid),JsonUtils.objectToJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
