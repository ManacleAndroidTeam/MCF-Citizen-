package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-13 <======
 */

public class ComplaintCategoryModel implements Serializable {
    @SerializedName("dept_id")
    private int departmentId;

    @SerializedName("department_name")
    private String departmentName;

    @SerializedName("icon_unicode")
    private String iconUnicode;

    @SerializedName("complaint_type")
    private ArrayList<ComplaintSubCategoryModel> subCategoryModelList;

    public int getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getIconUnicode() {
        return iconUnicode;
    }

    public ArrayList<ComplaintSubCategoryModel> getSubCategoryModelList() {
        return subCategoryModelList;
    }

    @Override
    public String toString() {
        return "ComplaintCategoryModel{" +
                "departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", iconUnicode='" + iconUnicode + '\'' +
                ", subCategoryModelList=" + subCategoryModelList +
                '}';
    }
}
