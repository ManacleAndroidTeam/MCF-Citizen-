package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-30 <======
 */

public class ComplaintStatusModel implements Serializable {
    @SerializedName("id")
    private int statusId;

    @SerializedName("name")
    private String statusName;

    @SerializedName("color_code")
    private String statusColorCode;

    public ComplaintStatusModel(int statusId, String statusName) {
        this.statusId = statusId;
        this.statusName = statusName;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getStatusColorCode() {
        return statusColorCode;
    }

    @Override
    public String toString() {
        return "ComplaintStatusModel{" +
                "statusId=" + statusId +
                ", statusName='" + statusName + '\'' +
                ", statusColorCode='" + statusColorCode + '\'' +
                '}';
    }
}
