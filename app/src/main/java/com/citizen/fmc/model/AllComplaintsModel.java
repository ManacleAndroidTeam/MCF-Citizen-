package com.citizen.fmc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-30 <======
 */

public class AllComplaintsModel implements Serializable {
    @SerializedName("current_status")
    private int currentStatus;

    @SerializedName("priority")
    private int priority;

    @SerializedName("complaint_no")
    private String compNum;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lng")
    private String longitude;

    @SerializedName("complaint_type")
    private String compType;

    @SerializedName("date")
    private String compDate;

    @SerializedName("time")
    private String compTime;

    @SerializedName("duration_days")
    private long days;

    @SerializedName("duration_hours")
    private long hours;

    @SerializedName("duration_minutes")
    private long minutes;

    @SerializedName("circle")
    private String compCircle;

    @SerializedName("custom_address")
    private String compAdd;

    @SerializedName("geo_address")
    private String compGeoAdd;

    @SerializedName("comp_description")
    private String compDesc;

    @SerializedName("fe_remarks")
    private String fe_remarks;

    @SerializedName("first_image")
    private String firstImageComplaint;

    @SerializedName("complaint_image")
    private String compImageURL;

    @SerializedName("options")
    private String options;

    @SerializedName("images")
    private String images;

    @SerializedName("is_upvotes_done")
    @Expose
    private boolean isUpVotesDone;

    @SerializedName("upvotes_count")
    @Expose
    private Integer upVotesCount;



    public boolean isUpVotesDone() {
        return isUpVotesDone;
    }

    public void setUpVotesDone(boolean upVotesDone) {
        isUpVotesDone = upVotesDone;
    }

    public Integer getUpVotesCount() {
        return upVotesCount;
    }

    public void setUpVotesCount(Integer upVotesCount) {
        this.upVotesCount = upVotesCount;
    }

    public long getDays() {
        return days;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public String getCompAdd() {
        return compAdd;
    }

    public String getCompTime() {
        return compTime;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public int getPriority() {
        return priority;
    }

    public String getCompNum() {
        return compNum;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCompType() {
        return compType;
    }

    public String getCompDate() {
        return compDate;
    }

    public String getCompCircle() {
        return compCircle;
    }

    public String getCompGeoAdd() {
        return compGeoAdd;
    }

    public String getCompDesc() {
        return compDesc;
    }

    public String getFirstImageComplaint(){
        return firstImageComplaint;
    }
    public String getCompImageURL() {
        return compImageURL;
    }


    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
    public String getFe_remarks() {
        return fe_remarks;
    }

    public void setFe_remarks(String fe_remarks) {
        this.fe_remarks = fe_remarks;
    }

    @Override
    public String toString() {
        return "AllComplaintsModel{" +
                "currentStatus=" + currentStatus +
                ", priority=" + priority +
                ", compNum='" + compNum + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", compType='" + compType + '\'' +
                ", compDate='" + compDate + '\'' +
                ", compTime='" + compTime + '\'' +
                ", compCircle='" + compCircle + '\'' +
                ", compGeoAdd='" + compGeoAdd + '\'' +
                ", compDesc='" + compDesc + '\'' +
                ", compImageURL='" + compImageURL + '\'' +
                '}';
    }
}
