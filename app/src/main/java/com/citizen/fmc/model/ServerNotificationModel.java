package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-06-14 <======
 */

public class ServerNotificationModel implements Serializable {
    @SerializedName("notification_id")
    private Integer notificationID;

    @SerializedName("title_name")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("image_name")
    private String imagePath;

    @SerializedName("fcm_token")
    private String fcmToken;

    @SerializedName("fcm_for_id")
    private Integer fcmForId;

    @SerializedName("department_id")
    private Object departmentId;

    @SerializedName("app_type")
    private Integer appType;

    @SerializedName("circular_type")
    private Integer circularType;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("view_status")
    private Integer readStatus;

    @SerializedName("status")
    private Integer status;

    public Integer getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(Integer notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public Integer getFcmForId() {
        return fcmForId;
    }

    public Object getDepartmentId() {
        return departmentId;
    }

    public Integer getAppType() {
        return appType;
    }

    public Integer getCircularType() {
        return circularType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ServerNotificationModel{" +
                "notificationId=" + notificationID +
                ", titleName='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", fcmForId=" + fcmForId +
                ", departmentId=" + departmentId +
                ", appType=" + appType +
                ", circularType=" + circularType +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", readStatus=" + readStatus +
                ", status=" + status +
                '}' + "\n";
    }
}
