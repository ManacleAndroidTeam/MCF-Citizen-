package com.citizen.fmc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class NearByTreeListModel {

    @SerializedName("TreeID")
    @Expose
    private Integer treeID;
    @SerializedName("TreeName")
    @Expose
    private String treeName;
    @SerializedName("AddedBy")
    @Expose
    private String addedBy;
    @SerializedName("WardNo")
    @Expose
    private Integer wardNo;
    @SerializedName("Ownership")
    @Expose
    private String ownership;
    @SerializedName("Condition")
    @Expose
    private String condition;
    @SerializedName("Remark")
    @Expose
    private String remark;
    @SerializedName("Landmark")
    @Expose
    private String landmark;
    @SerializedName("Diameter")
    @Expose
    private Integer diameter;
    @SerializedName("Girth")
    @Expose
    private Double girth;
    @SerializedName("Height")
    @Expose
    private Integer height;
    @SerializedName("Age")
    @Expose
    private Integer age;
    @SerializedName("SurveyDate")
    @Expose
    private String surveyDate;
    @SerializedName("Comment")
    @Expose
    private Object comment;
    @SerializedName("Photo")
    @Expose
    private String photo;
    @SerializedName("CarbonSeq")
    @Expose
    private Double carbonSeq;
    @SerializedName("ProjectID")
    @Expose
    private Integer projectID;
    @SerializedName("TreeNo")
    @Expose
    private String treeNo;
    @SerializedName("Latitude")
    @Expose
    private Integer latitude;
    @SerializedName("Longitude")
    @Expose
    private Integer longitude;
    @SerializedName("Photos")
    @Expose
    private List<String> photos = null;
    @SerializedName("OtherAttributes")
    @Expose
    private OtherAttributes otherAttributes;
    @SerializedName("UniqueTreeID")
    @Expose
    private String uniqueTreeID;

    public Integer getTreeID() {
        return treeID;
    }

    public void setTreeID(Integer treeID) {
        this.treeID = treeID;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Integer getWardNo() {
        return wardNo;
    }

    public void setWardNo(Integer wardNo) {
        this.wardNo = wardNo;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public Integer getDiameter() {
        return diameter;
    }

    public void setDiameter(Integer diameter) {
        this.diameter = diameter;
    }

    public Double getGirth() {
        return girth;
    }

    public void setGirth(Double girth) {
        this.girth = girth;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(String surveyDate) {
        this.surveyDate = surveyDate;
    }

    public Object getComment() {
        return comment;
    }

    public void setComment(Object comment) {
        this.comment = comment;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Double getCarbonSeq() {
        return carbonSeq;
    }

    public void setCarbonSeq(Double carbonSeq) {
        this.carbonSeq = carbonSeq;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public String getTreeNo() {
        return treeNo;
    }

    public void setTreeNo(String treeNo) {
        this.treeNo = treeNo;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public OtherAttributes getOtherAttributes() {
        return otherAttributes;
    }

    public void setOtherAttributes(OtherAttributes otherAttributes) {
        this.otherAttributes = otherAttributes;
    }

    public String getUniqueTreeID() {
        return uniqueTreeID;
    }

    public void setUniqueTreeID(String uniqueTreeID) {
        this.uniqueTreeID = uniqueTreeID;
    }

}
