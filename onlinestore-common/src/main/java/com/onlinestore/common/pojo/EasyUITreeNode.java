package com.onlinestore.common.pojo;

import java.io.Serializable;

/**
 * 选择类目的树形控件的pojo
 */
public class EasyUITreeNode implements Serializable{

    private long id;
    private String text;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
