package com.onlinestore.service;

import com.onlinestore.common.pojo.EasyUIDataGridResult;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.pojo.TbItem;
import com.onlinestore.pojo.TbItemDesc;
import com.onlinestore.pojo.TbItemParamItem;

/*
* 商品服务的接口
*/
public interface ItemService {
//    定义一个接口方法,根据商品id查询商品信息,返回的对象是TbItem
    TbItem getItemById(long itemId);
    //定义一个接口方法,查询所有的商品信息,并且进行分页,返回的对象的EasyUIDataResult
    EasyUIDataGridResult getItemList(int page, int rows);//page代表当前是第几页,rows代表每页有多少条数据
    OnlinStoreResult addItem(TbItem tbItem, String desc);
    OnlinStoreResult getItemDescById(long itemId);//根据商品id查询商品详情
    OnlinStoreResult getItemParamItemById(long itemId);//根据商品id查询商品参数规格
    OnlinStoreResult updateItem(TbItem tbItem, String desc, String itemParams);
    OnlinStoreResult deleteItem(long itemId);
    OnlinStoreResult instockItem(long itemId);
    OnlinStoreResult reshelfItem(long itemId);
}
