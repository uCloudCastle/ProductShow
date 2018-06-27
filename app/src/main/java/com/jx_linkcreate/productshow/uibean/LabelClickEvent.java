package com.jx_linkcreate.productshow.uibean;

public class LabelClickEvent {
    public int filterType = 0;          // 0：选中 1：取消选中
    public String value = "";

    public LabelClickEvent(int type, String v) {
        filterType = type;
        value = v;
    }
}
