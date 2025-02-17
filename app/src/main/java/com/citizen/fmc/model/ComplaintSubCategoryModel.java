package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-13 <======
 */

public class ComplaintSubCategoryModel implements Serializable {
    @SerializedName("id")
    private String subCategoryId;

    @SerializedName("name")
    private String subCategoryName;

    @SerializedName("icon_unicode")
    private String iconUnicode;

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getIconUnicode() {
        return iconUnicode;
    }

    @Override
    public String toString() {
        return "ComplaintSubCategoryModel{" +
                "subCategoryId='" + subCategoryId + '\'' +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", iconUnicode='" + iconUnicode + '\'' +
                '}';
    }
}
