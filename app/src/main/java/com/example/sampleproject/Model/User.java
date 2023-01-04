package com.example.sampleproject.Model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("realm")
    public String realm;
    @SerializedName("realmId")
    public String realmId;
    @SerializedName("id")
    public String id;
    @SerializedName("enabled")
    public Boolean enabled;
    @SerializedName("createdOn")
    public String createdOn;
    @SerializedName("serviceAccount")
    public boolean serviceAccount;
    @SerializedName("username")
    public String username;
}
