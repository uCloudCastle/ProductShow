package com.jx_linkcreate.productshow.transmitter;

public interface NetworkCallback<T> {
    void onNext(T t);
    void onError(Throwable e);
    void onComplete();
}
