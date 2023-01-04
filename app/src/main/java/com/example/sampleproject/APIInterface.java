package com.example.sampleproject;

import com.example.sampleproject.Model.AllAsset;
import com.example.sampleproject.Model.Asset;
import com.example.sampleproject.Model.Map;
import com.example.sampleproject.Model.User;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("api/master/asset/user/link")
    Call<List<AllAsset>> getAllAsset();

    @GET("api/master/user/user")
    Call<User> getUser();

    @GET("api/master/asset/{assetID}")
    Call<Asset> getAsset(@Path("assetID") String assetID);

    @GET("api/master/map/js")
    Call<Map> getMap();

}
