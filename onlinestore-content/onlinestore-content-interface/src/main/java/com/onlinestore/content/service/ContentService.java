package com.onlinestore.content.service;

import com.onlinestore.common.pojo.EasyUIDataGridResult;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.pojo.TbContent;

import java.util.List;

public interface ContentService {

    EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows);
    OnlinStoreResult addContent(TbContent tbContent);
    OnlinStoreResult editContent(TbContent tbContent);
    OnlinStoreResult deleteContent(Long ids);
    List<TbContent> getAdContentByCid(Long cid);

}
