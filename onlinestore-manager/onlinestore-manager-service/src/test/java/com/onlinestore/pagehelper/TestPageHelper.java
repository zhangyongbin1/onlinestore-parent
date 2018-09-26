package com.onlinestore.pagehelper;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinestore.mapper.TbItemMapper;
import com.onlinestore.pojo.TbItem;
import com.onlinestore.pojo.TbItemExample;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * pagehelper分页插件的unit测试
 */
public class TestPageHelper {
    @Test
    public void testPageHelper(){
        //在mybaits的配置文件配置pagehelper分页插件
        //在执行查询之前设置分页条件。使用pageHelper的静态方法
        PageHelper.startPage(1,10);
        //初始化spring容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
        //获得Mapper的代理对象
        TbItemMapper tbItemMapper = applicationContext.getBean(TbItemMapper.class);
        //创建example对象
        TbItemExample example = new TbItemExample();
        List<TbItem> list = tbItemMapper.selectByExample(example);
        //使用pageinfo封装list数据集,然后取分页信息
        PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
        System.out.println("总记录数： "+pageInfo.getTotal());
        System.out.println("总页数： "+pageInfo.getPages());
        System.out.println("返回的记录数： "+list.size());
    }
}
