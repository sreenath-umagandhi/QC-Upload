package com.jianjin.customcamera;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @Headers({"Accept: application/json"})
    @POST("/")
    Call<MyResponse> uploadImage(@Part MultipartBody.Part image, @Part("file") RequestBody name);





}
