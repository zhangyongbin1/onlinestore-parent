package com.onlinestore.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 封装TbItem的pojo用于满足EasyUIDataGrid需要的json数据格式,因为很多地方可能用到,所以将其放置到common包中
 */
public class EasyUIDataGridResult implements Serializable{
    private Long total;
    private List<?> rows;

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
