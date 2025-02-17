package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-24 <======
 */
public class WhoServeYouModel implements Serializable {
    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("mobile")
    private String contactNumber;

    @SerializedName("email_id")
    private String email;

    @SerializedName("image_name")
    private String imagePath;

    @SerializedName("role_name")
    private String roleName;

    @SerializedName("role_id")
    private Integer roleId;


    public Integer getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getRoleName() {
        return roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }
}
