package com.jx_linkcreate.productshow.transmitter.interfaces;

import com.jx_linkcreate.productshow.transmitter.netbean.HResult;
import com.jx_linkcreate.productshow.transmitter.netbean.HttpResponse;
import com.jx_linkcreate.productshow.transmitter.netbean.Product;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;


public interface ApiService {
    @Multipart
    @POST("/uploadProduct")
    Observable<HResult> uploadProduct(@PartMap Map<String, RequestBody> map,
                                      @Part List<MultipartBody.Part> partList);

    @Multipart
    @POST("/deleteProduct")
    Observable<HResult> deleteProduct(@Query("productId") String productId);

    @GET("/getAllProduct")
    Observable<HttpResponse<List<Product>>> getAllProduct(@Query("appKey") String appKey);
}
