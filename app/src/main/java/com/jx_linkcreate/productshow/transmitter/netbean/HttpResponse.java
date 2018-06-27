package com.jx_linkcreate.productshow.transmitter.netbean;

public class HttpResponse<T> {
    public int code;
    public String msg;
    public T result;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("HttpResponse:{")
                .append(code).append(",")
                .append(msg).append(",")
                .append(result).append("}");
        return builder.toString();
    }
}
