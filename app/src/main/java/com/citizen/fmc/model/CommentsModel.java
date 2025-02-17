package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-02-05 <======
 */

public class CommentsModel implements Serializable {
    @SerializedName("app_type")
    private int appType;

    @SerializedName("comment")
    private String comment;

    @SerializedName("comment_by_image")
    private String complainantImagePath;

    @SerializedName("comment_image")
    private String commentImagePath;

    @SerializedName("created_at")
    private String commentDateTime;

    @SerializedName("id")
    private String designation;

    @SerializedName("comment_by_name")
    private String complainantName;


    public int getAppType() {
        return appType;
    }

    public String getComment() {
        return comment;
    }

    public String getComplainantImagePath() {
        return complainantImagePath;
    }

    public String getCommentImagePath() {
        return commentImagePath;
    }

    public String getCommentDateTime() {
        return commentDateTime;
    }

    public String getDesignation() {
        return designation;
    }

    public String getComplainantName() {
        return complainantName;
    }

    @Override
    public String toString() {
        return "CommentsModel{" +
                "appType=" + appType +
                ", comment='" + comment + '\'' +
                ", complainantImagePath='" + complainantImagePath + '\'' +
                ", commentImagePath='" + commentImagePath + '\'' +
                ", commentDateTime='" + commentDateTime + '\'' +
                ", designation='" + designation + '\'' +
                ", complainantName='" + complainantName + '\'' +
                '}' + "\n";
    }
}
