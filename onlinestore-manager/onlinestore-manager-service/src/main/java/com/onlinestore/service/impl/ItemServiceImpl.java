package com.onlinestore.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinestore.common.pojo.EasyUIDataGridResult;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.utils.IDUtils;
import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.jedis.JedisClient;
import com.onlinestore.mapper.TbItemDescMapper;
import com.onlinestore.mapper.TbItemMapper;
import com.onlinestore.mapper.TbItemParamItemMapper;
import com.onlinestore.pojo.*;
import com.onlinestore.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "itemAddTopic")
    private Destination destination;
    @Resource(name = "itemUpdateTopic")
    private Destination updateDestinaion;
    @Resource(name = "itemDeleteTopic")
    private Destination deleteDestination;
    @Autowired
    private JedisClient jedisClientCluster;
    @Value("${ITEM_INFO}")
    private String ITEM_INFO;
    @Value("${EXPIRED_TIME}")
    private Integer EXPIRED_TIME;
    @Override
    public TbItem getItemById(long itemId) {
        try {
            //为了减轻数据库的压力，将点击率高的商品添加缓存即设置过期时间即可
            String json = jedisClientCluster.get(ITEM_INFO + ":" + Long.toString(itemId) + ":BASE");
            if(StringUtils.isNotBlank(json)){
                TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
                return tbItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果缓存中没有那么就直接去数据库中查找
        //使用mapper根据itemId进行查询,因为id是主键所以可以使用selectByPrimaryKey
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        try {
            //把查询结果放置到redis中并设置过期时间
            jedisClientCluster.set(ITEM_INFO+":"+tbItem.getId()+":BASE",JsonUtils.objectToJson(tbItem));
            //设置过期时间，提高缓存的利用率
            jedisClientCluster.expire(ITEM_INFO+":"+tbItem.getId()+":BASE",EXPIRED_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbItem;
    }

    @Override
    public OnlinStoreResult getItemParamItemById(long itemId) {//根据商品id查商品参数规格
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> list = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if(list == null || list.size() < 1 ){
            return OnlinStoreResult.build(200,itemId+"该商品没有设置商品参数规格",null);
        }
        return OnlinStoreResult.ok(list.get(0));
    }

    @Override
    public OnlinStoreResult addItem(TbItem tbItem, String desc) {//form表单中提供的字段与接收的pojo中的字段对应上即可正确接收
        //1.生成商品Id
        final long itemId = IDUtils.genItemId();
        //补全TbItem对象的属性
        tbItem.setId(itemId);
        //商品状态,1-正常,2-下架,3-删除
        tbItem.setStatus((byte)1);
        tbItem.setCreated(new Date());
        tbItem.setUpdated(new Date());
        //向数据中Tb_Item表中插入补全之后的商品数据
        itemMapper.insert(tbItem);
        //创建一个TbItemDesc对象
        TbItemDesc tbItemDesc = new TbItemDesc();
        //补全TbItemDesc的属性
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setCreated(new Date());
        tbItemDesc.setUpdated(new Date());
        //向商品详情表中插入插入数据
        itemDescMapper.insert(tbItemDesc);
        //向activeMq发送商品添加信息
        jmsTemplate.send(destination,new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //发送商品id
                TextMessage textMessage = session.createTextMessage(Long.toString(itemId));
                return textMessage;
            }
        });
        //返回结果，有可能还没有add成功，activeMq的消息已经发送成功，所以在consumer端需要等待几秒最好
        return OnlinStoreResult.ok();
    }

    /**
     * update方法也最好用先删除缓存的办法,直接删除缓存,不要更新redis缓存的问题
     * @param tbItem
     * @param desc
     * @param itemParams
     * @return
     */
    @Override
    public OnlinStoreResult updateItem(TbItem tbItem, String desc, String itemParams) {
        final long itemId = tbItem.getId();
        tbItem.setUpdated(new Date());
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(itemId);
        itemMapper.updateByExampleSelective(tbItem,example);
        //更新详情表,每一次都是new一个新的实例,是线程之间不共享的
        TbItemDesc tbItemDesc = new TbItemDesc();
        //补全属性
        tbItemDesc.setUpdated(new Date());
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setItemId(itemId);
        itemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
        //更新tb_item_param_item表
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setUpdated(new Date());
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria1 = tbItemParamItemExample.createCriteria();
        criteria1.andItemIdEqualTo(itemId);
        tbItemParamItemMapper.updateByExampleSelective(tbItemParamItem,tbItemParamItemExample);
        //同步缓存
        jedisClientCluster.set(ITEM_INFO + ":" + Long.toString(itemId) + ":BASE",JsonUtils.objectToJson(tbItem));
        jedisClientCluster.expire(ITEM_INFO + ":" + Long.toString(itemId) + ":BASE",EXPIRED_TIME);
        jedisClientCluster.set(ITEM_INFO + ":" + Long.toString(itemId) + ":DESC",JsonUtils.objectToJson(tbItemDesc));
        jedisClientCluster.expire(ITEM_INFO + ":" + Long.toString(itemId) + ":DESC",EXPIRED_TIME);
        //activeMq发送商品修改消息
        jmsTemplate.send(updateDestinaion, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //发送修改商品的id
                TextMessage textMessage = session.createTextMessage(Long.toString(itemId));
                return textMessage;
            }
        });
        return OnlinStoreResult.ok();
    }

    /**
     * 如果数据要求一致性强,那么redis与mysql之间的同步就需要使用基于订阅binlog的同步机制或者是加入队列或者是使用Mysql的UDF机制
     * UDF自定义函数的方式，面对mysql的API进行编程，利用触发器进行缓存同步，但UDF主要是c/c++语言实现，学习成本高。
     * 这里商品详情和和轮播图由于一致性要求不是那么强，所以就才用先删除缓存的办法
     * @param itemId
     * @return
     */
    @Override
    public OnlinStoreResult deleteItem(final long itemId) {
        //删除tbItem表中的数据
        itemMapper.deleteByPrimaryKey(itemId);
        //删除tbItemDesc商品详情表中的数据
        itemDescMapper.deleteByPrimaryKey(itemId);
        //删除参数规格表
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        tbItemParamItemMapper.deleteByExample(example);
        //同步缓存
        if(jedisClientCluster.exists(ITEM_INFO + ":" + Long.toString(itemId) + ":DESC")){
            jedisClientCluster.del(ITEM_INFO + ":" + Long.toString(itemId) + ":DESC");
        }
        if(jedisClientCluster.exists(ITEM_INFO + ":" + Long.toString(itemId) + ":BASE")){
            jedisClientCluster.del(ITEM_INFO + ":" + Long.toString(itemId) + ":BASE");
        }
        //发送商品删除的消息
        jmsTemplate.send(deleteDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(Long.toString(itemId));
                return textMessage;
            }
        });
        return OnlinStoreResult.ok();
    }

    @Override
    public OnlinStoreResult instockItem(long itemId) {
        //根据商品id查询商品
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        tbItem.setStatus((byte)2);//2为下架商品
        tbItem.setUpdated(new Date());
        itemMapper.updateByPrimaryKeySelective(tbItem);
        if(jedisClientCluster.exists(ITEM_INFO + ":" + Long.toString(itemId) + ":DESC")){
            jedisClientCluster.del(ITEM_INFO + ":" + Long.toString(itemId) + ":DESC");
        }
        if(jedisClientCluster.exists(ITEM_INFO + ":" + Long.toString(itemId) + ":BASE")){
            jedisClientCluster.del(ITEM_INFO + ":" + Long.toString(itemId) + ":BASE");
        }
        return OnlinStoreResult.ok();
    }

    @Override
    public OnlinStoreResult reshelfItem(long itemId) {
        //根据商品id查询商品的状态
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
       tbItem.setStatus((byte)1);//1为上架商品
        tbItem.setUpdated(new Date());
        itemMapper.updateByPrimaryKeySelective(tbItem);
        return OnlinStoreResult.ok();
    }

    @Override
    public OnlinStoreResult getItemDescById(long itemId) {//根据商品id查询商品描述
        try {
            //先查询缓存
            String json = jedisClientCluster.get(ITEM_INFO + ":" + Long.toString(itemId) + ":DESC");
            if(StringUtils.isNotBlank(json)){
                TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json,TbItemDesc.class);
                return OnlinStoreResult.ok(tbItemDesc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //缓存中没有则直接查询数据库
        TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        try {
            //将商品详情添加到缓存中
            jedisClientCluster.set(ITEM_INFO + ":" + tbItemDesc.getItemId() + ":DESC",JsonUtils.objectToJson(tbItemDesc));
            //设置过期时间提高缓存的利用率
            jedisClientCluster.expire(ITEM_INFO+":"+tbItemDesc.getItemId()+":DESC",EXPIRED_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OnlinStoreResult.ok(tbItemDesc);
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        //设置分页信息:page表示当前是第几页,rows表示每一页查询的商品个数
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> list = itemMapper.selectByExample(example);
        //取分页信息
        PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
        //创建返回结果对象 EasyUIDataGridResult保存的信息结构类型被转换成json格式后是PageHelper需要的类型
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list);
        return result;
    }
}
