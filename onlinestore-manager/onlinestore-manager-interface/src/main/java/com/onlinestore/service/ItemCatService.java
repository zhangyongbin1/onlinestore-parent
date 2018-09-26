package com.onlinestore.service;

import com.onlinestore.common.pojo.EasyUITreeNode;

import java.util.List;

/**
 * 选择类目时需要的树形控件的请求数据服务接口
 */
public interface ItemCatService {
    List<EasyUITreeNode> getItemCatList(long parentId);
}
