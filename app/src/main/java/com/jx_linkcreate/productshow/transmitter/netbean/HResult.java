package com.jx_linkcreate.productshow.transmitter.netbean;

public class HResult {
    public int code;
    public String msg;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("HResult:{")
                .append(code).append(",")
                .append(msg).append("}");
        return builder.toString();
    }
}
