package com.onlinestore.order.service.impl;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.jedis.JedisClient;
import com.onlinestore.mapper.TbOrderItemMapper;
import com.onlinestore.mapper.TbOrderMapper;
import com.onlinestore.mapper.TbOrderShippingMapper;
import com.onlinestore.order.pojo.OrderInfo;
import com.onlinestore.order.service.OrderService;
import com.onlinestore.pojo.TbOrderItem;
import com.onlinestore.pojo.TbOrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private JedisClient jedisClient;
    @Value("${ORDER_ID_GEN_KEY}")
    private String ORDER_ID_GEN_KEY;
    @Value("${ORDER_ID_BEGIN_VALUE}")
    private String ORDER_ID_BEGIN_VALUE;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Value("${ORDER_ITEM_ID_GEN_KEY}")
    private String ORDER_ITEM_ID_GEN_KEY;
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;
    @Override
    public OnlinStoreResult crateOrder(OrderInfo orderInfo) {
        //生成订单号，可以使用redis 的incr生成
        if(!jedisClient.exists(ORDER_ID_GEN_KEY)){
            //设置初始值
            jedisClient.set(ORDER_ID_GEN_KEY,ORDER_ID_BEGIN_VALUE);
        }
        //说明redis中已经生成过订单号了
        String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
        //向订单表中添加数据，需要补全pojo属性
        orderInfo.setOrderId(orderId);
        //免邮费
        orderInfo.setPostFee("0");
        //1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        orderInfo.setStatus(1);
        //创建订单的时间
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        //向订单表中添加数据
        orderMapper.insert(orderInfo);
        //向订单明细表中添加数据
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        for(TbOrderItem orderItem : orderItems){
            //获得明细的主键
            String oid = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY).toString();
            orderItem.setOrderId(oid);
            orderItem.setId(orderId);
            //插入明细数据表
            orderItemMapper.insert(orderItem);
        }
        //向订单物流表中插入数据
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        orderShipping.setOrderId(orderId);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());
        orderShippingMapper.insert(orderShipping);
        //返回订单号
        return OnlinStoreResult.ok(orderId);
    }
}
