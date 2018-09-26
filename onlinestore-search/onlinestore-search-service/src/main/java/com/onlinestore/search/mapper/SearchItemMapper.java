package com.onlinestore.search.mapper;


import com.onlinestore.common.pojo.SearchItem;

import java.util.List;

public interface SearchItemMapper {

	List<SearchItem> getItemList();
	SearchItem getItemById(long itemId);
}
