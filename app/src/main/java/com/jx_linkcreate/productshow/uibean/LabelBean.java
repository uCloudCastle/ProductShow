package com.jx_linkcreate.productshow.uibean;

public class LabelBean {
    public String label = "";
    public String parent = "";
    public int type = 0;              // 0：正常  1：显示删除icn  2：显示新增icn

    public LabelBean(int type) {
        this.type = type;
    }

    public LabelBean(String str) {
        this.label = str;
    }

    public LabelBean(String str, String parent) {
        this.label = str;
        this.parent = parent;
    }

    public LabelBean(String str, String parent, int type) {
        this.label = str;
        this.parent = parent;
        this.type = type;
    }
}
