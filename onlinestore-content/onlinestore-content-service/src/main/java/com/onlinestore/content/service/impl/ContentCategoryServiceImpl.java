package com.onlinestore.content.service.impl;

import com.onlinestore.common.pojo.EasyUITreeNode;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.content.service.ContentCategoryService;
import com.onlinestore.mapper.TbContentCategoryMapper;
import com.onlinestore.pojo.TbContentCategory;
import com.onlinestore.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;//因为id是AUTO_INCREMENT，所以mapper.xml中需要添加selectKey
    @Override
    public List<EasyUITreeNode> getContentCategoryList(Long parentId) {
        //设置查询条件：根据parentId查询所有的子节点
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //根据查询条件进行查询
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        List<EasyUITreeNode> result = new ArrayList<EasyUITreeNode>();
        for(TbContentCategory tbContentCategory : list){
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbContentCategory.getId());
            node.setText(tbContentCategory.getName());
            node.setState(tbContentCategory.getIsParent()?"closed":"open");
            result.add(node);
        }
        return result;
    }

    @Override
    public OnlinStoreResult addContentCategory(Long parentId, String name) {
        //创建TbContentCategory
        TbContentCategory tbContentCategory = new TbContentCategory();
        //补全TbContentCategory的属性,id为自增长可以不用管
        tbContentCategory.setParentId(parentId);
        tbContentCategory.setName(name);
        tbContentCategory.setStatus(1);//'状态。可选值:1(正常),2(删除)'
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setUpdated(new Date());
        //插入数据库操作
        tbContentCategoryMapper.insert(tbContentCategory);
        //设置当前加入节点的父节点的closed open状态
        TbContentCategory parentNode = tbContentCategoryMapper.selectByPrimaryKey(parentId);
        if(!parentNode.getIsParent()){
            parentNode.setIsParent(true);//将父节点的isParent属性设置为true
            tbContentCategoryMapper.updateByPrimaryKey(parentNode);
        }
        return OnlinStoreResult.ok(tbContentCategory);
    }

    @Override
    public OnlinStoreResult updateContentCategory(Long id, String name) {
        //根据id更新TbContentCategory表
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
        tbContentCategory.setName(name);
        tbContentCategory.setUpdated(new Date());
        tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
        return OnlinStoreResult.ok();
    }

    @Override
    public OnlinStoreResult deleteContentCategory(Long id) {
        //根据id删除tbcontentcategory中的数据,要注意父节点的closed 与 open 情况
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
        if(tbContentCategory.getIsParent()){//如果当前删除的是父节点，那么需要递归删除，把所有子节点也删除
            List<TbContentCategory> temp = getChildNodes(id);
            for(TbContentCategory son : temp){
                if(son.getIsParent()){//递归删除
                    deleteContentCategory(son.getId());
                }else{
                    delete(son.getId(),tbContentCategoryMapper.selectByPrimaryKey(son.getId()));
                }
            }
            //遍历完子节点之后,需要把父节点给删除了
            delete(id,tbContentCategory);

        }else{
            delete(id,tbContentCategory);
        }
        return OnlinStoreResult.ok();
    }

    private List<TbContentCategory> getChildNodes(Long id){
            //获取该节点下的所有子节点
            TbContentCategoryExample example = new TbContentCategoryExample();
            TbContentCategoryExample.Criteria criteria = example.createCriteria();
            criteria.andParentIdEqualTo(id);
            return tbContentCategoryMapper.selectByExample(example);
    }

    private void delete(Long id, TbContentCategory tbContentCategory){
        //删除之前获取一些父节点的id
        Long parentId = tbContentCategory.getParentId();
        tbContentCategoryMapper.deleteByPrimaryKey(id);
        //查询父节点下是否还有其他叶子节点
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        int nums = tbContentCategoryMapper.countByExample(example);
        if(nums <= 0){//说明父节点中没有其他子节点了，需要将当前父节点的isParent设置为false
            TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
            parent.setIsParent(false);
            parent.setUpdated(new Date());
            tbContentCategoryMapper.updateByPrimaryKeySelective(parent);
        }
    }
}
