package com.citizen.fmc.model;

;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewAllComplaintsModel implements Serializable {

    @SerializedName("complaint_no")
    @Expose
    private String complaintNo;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("complaint_type")
    @Expose
    private String complaintType;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("circle")
    @Expose
    private String circle;
    @SerializedName("geo_address")
    @Expose
    private String geoAddress;
    @SerializedName("current_status")
    @Expose
    private Integer currentStatus;
    @SerializedName("current_status_name")
    @Expose
    private String currentStatusName;
    @SerializedName("color_code")
    @Expose
    private String colorCode;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("comp_description")
    @Expose
    private String compDescription;
    @SerializedName("complaint_image")
    @Expose
    private String complaintImage;

    @SerializedName("duration_days")
    @Expose
    private Integer durationDays;

    @SerializedName("duration_hours")
    @Expose
    private Integer durationHours;

    @SerializedName("duration_minutes")
    @Expose
    private Integer durationMinutes;

    @SerializedName("is_upvotes_done")
    @Expose
    private boolean isUpVotesDone;

    @SerializedName("upvotes_count")
    @Expose
    private Integer upVotesCount;


    @Override
    public String toString() {
        return "ViewAllComplaintsModel{" +
                "complaint_no=" + complaintNo +
                ", lat=" + lat +
                ", lng='" + lng + '\'' +
                ", complaint_type=" + complaintType +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", circle='" + circle + '\'' +
                ", geo_address='" + geoAddress + '\'' +
                ", current_status=" + currentStatus +
                ", color_code=" + colorCode +
                ", priority=" + priority +
                ", comp_description=" + compDescription +
                ", complaint_image=" + complaintImage +
                ", duration_days=" + durationDays +
                ", duration_hours=" + durationHours +
                ", duration_minutes=" + durationMinutes +
                ", is_upvotes_done=" + isUpVotesDone +
                ", upvotes_count=" + upVotesCount +
                '}' + "\n";
    }

    private final static long serialVersionUID = 1267944391415721991L;

    public String getComplaintNo() {
        return complaintNo;
    }

    public void setComplaintNo(String complaintNo) {
        this.complaintNo = complaintNo;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getGeoAddress() {
        return geoAddress;
    }

    public void setGeoAddress(String geoAddress) {
        this.geoAddress = geoAddress;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStatusName() {
        return currentStatusName;
    }

    public void setCurrentStatusName(String currentStatusName) {
        this.currentStatusName = currentStatusName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCompDescription() {
        return compDescription;
    }

    public void setCompDescription(String compDescription) {
        this.compDescription = compDescription;
    }

    public String getComplaintImage() {
        return complaintImage;
    }

    public void setComplaintImage(String complaintImage) {
        this.complaintImage = complaintImage;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

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
}