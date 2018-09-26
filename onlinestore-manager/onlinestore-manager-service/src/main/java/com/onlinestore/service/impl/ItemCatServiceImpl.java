package com.onlinestore.service.impl;

import com.onlinestore.common.pojo.EasyUITreeNode;
import com.onlinestore.mapper.TbItemCatMapper;
import com.onlinestore.pojo.TbItemCat;
import com.onlinestore.pojo.TbItemCatExample;
import com.onlinestore.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    TbItemCatMapper itemCatMapper;

    @Override
    public List<EasyUITreeNode> getItemCatList(long parentId) {
        //根据parentId查询节点列表
        TbItemCatExample example = new TbItemCatExample();
        //设置查询条件
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        //将list转换成EasyUITreeNode列表
        List<EasyUITreeNode> resultList = new ArrayList<EasyUITreeNode>();
        for(TbItemCat tbItemCat : list){
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbItemCat.getId());
            node.setText(tbItemCat.getName());
            //如果是父节点，那么就是closed，如果是子节点，那么就是open
            node.setState(tbItemCat.getIsParent()? "closed" : "open");
            resultList.add(node);
        }

        return resultList;
    }
}
