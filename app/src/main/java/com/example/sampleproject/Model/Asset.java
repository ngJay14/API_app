package com.example.sampleproject.Model;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Asset {
    @SerializedName("id")
    public String id;
    @SerializedName("version")
    public String version;
    @SerializedName("createdOn")
    public String createdOn;
    @SerializedName("name")
    public String name;
    @SerializedName("accessPublicRead")
    public String accessPublicRead;
    @SerializedName("parentId")
    public String parentId;
    @SerializedName("realm")
    public String realm;
    @SerializedName("type")
    public String type;
    @SerializedName("path")
    public Object path;
    @SerializedName("attributes")
    public JsonObject attributes;

}




