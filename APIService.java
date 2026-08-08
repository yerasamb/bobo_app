package com.bobo.app.api;

import com.bobo.app.model.LoginRequest;
import com.bobo.app.model.LoginResponse;
import com.bobo.app.model.PhotoResponse;
import com.bobo.app.model.Child;
import com.bobo.app.model.User;

import java.util.Map;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService{
    @Multipart
    @POST("upload")
    Call<String> uploadPhoto(
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part child,
            @Part MultipartBody.Part teacher,
            @Part MultipartBody.Part mealType
    );
    @GET("/photos/{child}")
    Call<List<PhotoResponse>> getPhotos(@Path("child") String child);

    @GET("children/group/{group}")
    Call<List<Child>> getChildrenByGroup(@Path("group") String group);

    @GET("children")
    Call<List<Child>> getAllChildren();


    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body Map<String, String> request);
    @POST("auth/register")
    Call<Map<String, String>> register(@Body User user);
}