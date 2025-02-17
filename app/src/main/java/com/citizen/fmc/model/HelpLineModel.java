package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-09 <======
 */

public class HelpLineModel implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("contact_no")
    private String contactNumber;

    @SerializedName("unit_name")
    private String contactName;

    @SerializedName("image_name")
    private String imagePath;

    @SerializedName("address")
    private String contactAddress;


    public String getId() {
        return id;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    @Override
    public String toString() {
        return "HelpLineModel{" +
                "id='" + id + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", contactName='" + contactName + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", contactAddress='" + contactAddress + '\'' +
                '}' + "\n";
    }
}
