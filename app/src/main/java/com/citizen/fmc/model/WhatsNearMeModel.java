package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

public class WhatsNearMeModel implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("sub_module_id")
    private int subModuleId;

    @SerializedName("unit_no")
    private String unitNumber;

    @SerializedName("location_2_id")
    private int circleId;

    @SerializedName("address")
    private String address;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lng")
    private String longitude;

    @SerializedName("image_name")
    private String imagePath;

    @SerializedName("status")
    private int status;
    @SerializedName("mobile_no")
    private String  mobile_no;


    public String getMobile_no() {
        return mobile_no;
    }
    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setSubModuleId(int subModuleId) {
        this.subModuleId = subModuleId;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getSubModuleId() {
        return subModuleId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public int getCircleId() {
        return circleId;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "WhatsNearMeModel{" +
                "id=" + id +
                ", subModuleId=" + subModuleId +
                ", unitNumber='" + unitNumber + '\'' +
                ", circleId=" + circleId +
                ", address='" + address + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", status=" + status +
                ", mobile_no=" + mobile_no +
                '}' + "\n";
    }
}
