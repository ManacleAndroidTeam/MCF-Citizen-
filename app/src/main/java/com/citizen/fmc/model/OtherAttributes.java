package com.citizen.fmc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OtherAttributes {

    @SerializedName("TreeID")
    @Expose
    private Integer treeID;
    @SerializedName("CommonTreeID")
    @Expose
    private Integer commonTreeID;
    @SerializedName("BotanicalName")
    @Expose
    private String botanicalName;
    @SerializedName("MarathiName")
    @Expose
    private String marathiName;
    @SerializedName("HindiName")
    @Expose
    private String hindiName;
    @SerializedName("Family")
    @Expose
    private String family;
    @SerializedName("Origin")
    @Expose
    private String origin;
    @SerializedName("Category")
    @Expose
    private String category;
    @SerializedName("Use")
    @Expose
    private String use;
    @SerializedName("NaturalSurvivalCapacity")
    @Expose
    private String naturalSurvivalCapacity;
    @SerializedName("CrownShape")
    @Expose
    private String crownShape;
    @SerializedName("FloweringSeason")
    @Expose
    private String floweringSeason;
    @SerializedName("FloweringColor")
    @Expose
    private String floweringColor;
    @SerializedName("FruitingSeason")
    @Expose
    private String fruitingSeason;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("G1")
    @Expose
    private Object g1;
    @SerializedName("G2")
    @Expose
    private Object g2;
    @SerializedName("G3")
    @Expose
    private Object g3;
    @SerializedName("OtherAttributes")
    @Expose
    private List<Object> otherAttributes = null;

    public Integer getTreeID() {
        return treeID;
    }

    public void setTreeID(Integer treeID) {
        this.treeID = treeID;
    }

    public Integer getCommonTreeID() {
        return commonTreeID;
    }

    public void setCommonTreeID(Integer commonTreeID) {
        this.commonTreeID = commonTreeID;
    }

    public String getBotanicalName() {
        return botanicalName;
    }

    public void setBotanicalName(String botanicalName) {
        this.botanicalName = botanicalName;
    }

    public String getMarathiName() {
        return marathiName;
    }

    public void setMarathiName(String marathiName) {
        this.marathiName = marathiName;
    }

    public String getHindiName() {
        return hindiName;
    }

    public void setHindiName(String hindiName) {
        this.hindiName = hindiName;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getNaturalSurvivalCapacity() {
        return naturalSurvivalCapacity;
    }

    public void setNaturalSurvivalCapacity(String naturalSurvivalCapacity) {
        this.naturalSurvivalCapacity = naturalSurvivalCapacity;
    }

    public String getCrownShape() {
        return crownShape;
    }

    public void setCrownShape(String crownShape) {
        this.crownShape = crownShape;
    }

    public String getFloweringSeason() {
        return floweringSeason;
    }

    public void setFloweringSeason(String floweringSeason) {
        this.floweringSeason = floweringSeason;
    }

    public String getFloweringColor() {
        return floweringColor;
    }

    public void setFloweringColor(String floweringColor) {
        this.floweringColor = floweringColor;
    }

    public String getFruitingSeason() {
        return fruitingSeason;
    }

    public void setFruitingSeason(String fruitingSeason) {
        this.fruitingSeason = fruitingSeason;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Object getG1() {
        return g1;
    }

    public void setG1(Object g1) {
        this.g1 = g1;
    }

    public Object getG2() {
        return g2;
    }

    public void setG2(Object g2) {
        this.g2 = g2;
    }

    public Object getG3() {
        return g3;
    }

    public void setG3(Object g3) {
        this.g3 = g3;
    }

    public List<Object> getOtherAttributes() {
        return otherAttributes;
    }

    public void setOtherAttributes(List<Object> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }

}
