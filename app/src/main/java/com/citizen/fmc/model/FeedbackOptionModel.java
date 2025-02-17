package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedbackOptionModel implements Serializable {

    @SerializedName("option_id")
    private int optionId;

    @SerializedName("options")
    private String options;

    @SerializedName("images")
    private String images;

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public String getOptions() {
        return options;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "AllComplaintsModel{" +
                "optionId=" + optionId +
                ", options=" + options +
                '}';
    }
}
