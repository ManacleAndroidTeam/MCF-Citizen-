package com.citizen.fmc.model.ParkingSummeryResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParkingSummeryResponse {

    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("sts")
    @Expose
    private String sts;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}
