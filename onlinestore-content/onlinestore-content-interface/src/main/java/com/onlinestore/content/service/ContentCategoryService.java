package com.onlinestore.content.service;

import java.util.List;

import com.onlinestore.common.pojo.EasyUITreeNode;
import com.onlinestore.common.pojo.OnlinStoreResult;

public interface ContentCategoryService {

	List<EasyUITreeNode> getContentCategoryList(Long parentId);
	OnlinStoreResult addContentCategory(Long parentId, String name);
	OnlinStoreResult updateContentCategory(Long id, String name);
	OnlinStoreResult deleteContentCategory(Long id);
}
