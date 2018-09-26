package com.onlinestore.item.pojo;

import com.onlinestore.pojo.TbItem;

/**
 * 因为Tb_Item表中的图片属性不符合前端的需求,所以需要使用一个新的pojo包装一下,数据库中的images是字符串，而前端要求的images是字符串数组
 */
public class Item extends TbItem{

    public Item(TbItem tbItem){//Item继承了TbItem所有拥有TbItem拥有的所有属性
        this.setId(tbItem.getId());
        this.setTitle(tbItem.getTitle());
        this.setSellPoint(tbItem.getSellPoint());
        this.setPrice(tbItem.getPrice());
        this.setNum(tbItem.getNum());
        this.setBarcode(tbItem.getBarcode());
        this.setImage(tbItem.getImage());
        this.setCid(tbItem.getCid());
        this.setStatus(tbItem.getStatus());
        this.setCreated(tbItem.getCreated());
        this.setUpdated(tbItem.getUpdated());
    }

    public String[] getImages(){//前端获取images属性时是通过getImages()方法获取的,所以重写这个方法就行
        if(this.getImage() != null && !"".equals(this.getImage())){
            String image2 = this.getImage();
            String[] strings = image2.split(",");
            return strings;
        }
        return null;
    }

}
