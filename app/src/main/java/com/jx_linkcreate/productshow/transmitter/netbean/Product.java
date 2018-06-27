package com.jx_linkcreate.productshow.transmitter.netbean;

import java.util.ArrayList;

public class Product {
    public String name;
    public String price;
    public String tags;
    public ArrayList<String> urls = new ArrayList<>();
    public ArrayList<String> localPaths = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Product:{")
                .append(name).append(",")
                .append(price).append(",")
                .append(tags).append(",")
                .append(urls.size()).append(",")
                .append(localPaths.size()).append("}");;
        return builder.toString();
    }
}
