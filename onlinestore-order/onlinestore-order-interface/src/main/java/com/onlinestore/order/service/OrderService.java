package com.onlinestore.order.service;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.order.pojo.OrderInfo;

public interface OrderService {
    OnlinStoreResult crateOrder(OrderInfo orderInfo);
}
