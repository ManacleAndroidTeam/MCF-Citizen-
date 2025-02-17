package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-25 <======
 */

public class HistoryModel implements Serializable {
    @SerializedName("remarks")
    private String remarks;

    @SerializedName("complaint_status")
    private int complaintStatus;

    @SerializedName("complaint_status_name")
    private String complaintStatusName;

    @SerializedName("color_code")
    private String colorCode;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("assigned_to_image")
    private String assignedToImagePath;

    @SerializedName("assigned_to")
    private int assignedTo;

    @SerializedName("assigned_to_name")
    private String assignedToName;

    @SerializedName("complaint_image")
    private String complaintImagePath;


    public String getRemarks() {
        return remarks;
    }

    public int getComplaintStatus() {
        return complaintStatus;
    }

    public String getComplaintStatusName() {
        return complaintStatusName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getAssignedToImagePath() {
        return assignedToImagePath;
    }

    public int getAssignedTo() {
        return assignedTo;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public String getComplaintImagePath() {
        return complaintImagePath;
    }
}
