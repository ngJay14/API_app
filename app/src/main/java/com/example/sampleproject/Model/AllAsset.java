package com.example.sampleproject.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class AllAsset {
    @SerializedName("id")
    public JsonObject id;
    @SerializedName("createdOn")
    public String createdOn;
    @SerializedName("assetName")
    public String assetName;
    @SerializedName("parentAssetName")
    public  String parentAssetName;
}
