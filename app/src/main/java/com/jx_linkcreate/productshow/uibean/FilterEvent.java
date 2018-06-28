package com.jx_linkcreate.productshow.uibean;

public class FilterEvent {
    public int filterType = 0;          // 0：新增 1：删除 2:全部删除
    public String value = "";

    public FilterEvent(int type, String v) {
        filterType = type;
        value = v;
    }
}
