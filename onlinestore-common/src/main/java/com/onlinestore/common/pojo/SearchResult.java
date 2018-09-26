package com.onlinestore.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索功能实现时，需要接收两个返回值，所有封装一个pojo来保存
 */
public class SearchResult implements Serializable{
    private Long totalPages;
    private Long recordCount;
    private List<SearchItem> itemList;

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public List<SearchItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<SearchItem> itemList) {
        this.itemList = itemList;
    }
}
