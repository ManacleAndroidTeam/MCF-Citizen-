package com.citizen.fmc.model.CheckAccountResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckAccountResponse {

    @SerializedName("response")
    @Expose
    private Boolean response;
    @SerializedName("reg_mobile_no")
    @Expose
    private String regMobileNo;
    @SerializedName("reg_email_id")
    @Expose
    private String regEmailId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public String getRegMobileNo() {
        return regMobileNo;
    }

    public void setRegMobileNo(String regMobileNo) {
        this.regMobileNo = regMobileNo;
    }

    public String getRegEmailId() {
        return regEmailId;
    }

    public void setRegEmailId(String regEmailId) {
        this.regEmailId = regEmailId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}