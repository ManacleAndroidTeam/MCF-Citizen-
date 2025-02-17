package com.citizen.fmc.model.GenerateOtpResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenerateOtpResponse {

    @SerializedName("response")
    @Expose
    private Boolean response;
    @SerializedName("OTP")
    @Expose
    private String oTP;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public String getOTP() {
        return oTP;
    }

    public void setOTP(String oTP) {
        this.oTP = oTP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
