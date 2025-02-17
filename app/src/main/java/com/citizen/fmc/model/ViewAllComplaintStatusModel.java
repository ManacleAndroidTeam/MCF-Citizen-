package com.citizen.fmc.model; ;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewAllComplaintStatusModel implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("color_code")
    @Expose
    private String colorCode;
    @SerializedName("sequence")
    @Expose
    private Integer sequence;
    @SerializedName("status")
    @Expose
    private String status;

    public ViewAllComplaintStatusModel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private final static long serialVersionUID = -2593577794878878240L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}