package com.jx_linkcreate.productshow.transmitter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jx_linkcreate.productshow.transmitter.interfaces.ApiService;
import com.jx_linkcreate.productshow.transmitter.netbean.HResult;
import com.jx_linkcreate.productshow.transmitter.netbean.HttpResponse;
import com.jx_linkcreate.productshow.transmitter.netbean.Product;
import com.randal.aviana.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    //public static final String REMOTE_ADDR = "http://192.168.0.106:8080/";
    public static final String REMOTE_ADDR = "http://39.105.105.86:8080/";

    private Retrofit mRetrofit;

    private Context mContext;
    private volatile static NetworkManager sNetworkManager;

    private NetworkManager(Context context) {
        mContext = context;

        // Log 支持
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.d("Retrofit Msg = " + message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .build();

        // Gson 支持
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")                // 统一时间格式
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(REMOTE_ADDR)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static NetworkManager getInstance(Context context) {
        if (sNetworkManager == null) {
            synchronized (NetworkManager.class) {
                if (sNetworkManager == null) {
                    sNetworkManager = new NetworkManager(context);
                }
            }
        }
        return sNetworkManager;
    }

    public String getBaseUrl() {
        return REMOTE_ADDR;
    }

    private RequestBody convertToRequestBody(String param){
        if (param == null) {
            param = "";
        }
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }

    /* ********************************************************** 点检员端接口 ************************************************ */

    public void uploadProduct(Product product, final NetworkCallback<HResult> callback) {
        Map<String,RequestBody> params = new HashMap<>();
        params.put("appKey", convertToRequestBody("027001"));
        params.put("name", convertToRequestBody(product.name));
        params.put("price",convertToRequestBody(product.price));
        params.put("tags",convertToRequestBody(product.tags));

        ArrayList<MultipartBody.Part> parts = new ArrayList<>();
        if (product.localPaths != null) {
            for (int i = 0; i < product.localPaths.size(); ++i) {
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), new File(product.localPaths.get(i)));
                parts.add(MultipartBody.Part.createFormData("images", "image" + i, body));
            }
        }

        ApiService service = mRetrofit.create(ApiService.class);
        service.uploadProduct(params, parts)                                        // 返回 Observable, 进入RxJava 流程
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HResult>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(HResult response) {
                        callback.onNext(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        callback.onComplete();
                    }
                });
    }

    public void getAllProduct(String appKey, final NetworkCallback<HttpResponse<List<Product>>> callback) {
        ApiService service = mRetrofit.create(ApiService.class);
        service.getAllProduct(appKey)                                        // 返回 Observable, 进入RxJava 流程
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResponse<List<Product>>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(HttpResponse<List<Product>> response) {
                        callback.onNext(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        callback.onComplete();
                    }
                });

    }
}
