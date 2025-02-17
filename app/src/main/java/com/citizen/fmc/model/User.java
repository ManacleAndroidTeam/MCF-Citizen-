package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2017-12-30 <======
 */
public class User implements Serializable {
    @SerializedName("civilian_id")
    private int civilianId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("dob")
    private String dateOfBirth;

    @SerializedName("username")
    private String userName;

    @SerializedName("email")
    private String email;

    @SerializedName("mobile_no")
    private String mobileNum;

    @SerializedName("alter_mobile_no")
    private String alternateMobileNum;

    @SerializedName("gender")
    private int genderId;

    @SerializedName("image_name")
    private String profileImagePath;

    @SerializedName("login_count")
    private long loginCount;

    @SerializedName("water_consumer_no")
    private String waterConsumerNo;

    @SerializedName("electricity_consumer_no")
    private String electricityConsumerNo;


    public String getWaterConsumerNo() {
        return waterConsumerNo;
    }

    public void setWaterConsumerNo(String waterConsumerNo) {
        this.waterConsumerNo = waterConsumerNo;
    }

    public String getElectricityConsumerNo() {
        return electricityConsumerNo;
    }

    public void setElectricityConsumerNo(String electricityConsumerNo) {
        this.electricityConsumerNo = electricityConsumerNo;
    }

    public long getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(long loginCount) {
        this.loginCount = loginCount;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUserName() {
        return userName;
    }

    public int getCivilianId() {
        return civilianId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public String getAlternateMobileNum() {
        return alternateMobileNum;
    }

    public int getGenderId() {
        return genderId;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }
}
