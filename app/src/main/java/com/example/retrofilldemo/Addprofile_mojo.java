package com.example.retrofilldemo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Addprofile_mojo {
    @SerializedName("sucess")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
