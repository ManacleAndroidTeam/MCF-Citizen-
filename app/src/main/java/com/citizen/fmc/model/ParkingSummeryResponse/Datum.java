package com.citizen.fmc.model.ParkingSummeryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("car_out")
    @Expose
    private String carOut;
    @SerializedName("bike_in")
    @Expose
    private String bikeIn;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("car_collection")
    @Expose
    private String carCollection;
    @SerializedName("bike_out")
    @Expose
    private String bikeOut;
    @SerializedName("car_present")
    @Expose
    private String carPresent;
    @SerializedName("bike_collection")
    @Expose
    private String bikeCollection;
    @SerializedName("parking_id")
    @Expose
    private String parkingId;
    @SerializedName("bike_present")
    @Expose
    private String bikePresent;
    @SerializedName("parking_name")
    @Expose
    private String parkingName;
    @SerializedName("car_in")
    @Expose
    private String carIn;

    public String getCarOut() {
        return carOut;
    }

    public void setCarOut(String carOut) {
        this.carOut = carOut;
    }

    public String getBikeIn() {
        return bikeIn;
    }

    public void setBikeIn(String bikeIn) {
        this.bikeIn = bikeIn;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCarCollection() {
        return carCollection;
    }

    public void setCarCollection(String carCollection) {
        this.carCollection = carCollection;
    }

    public String getBikeOut() {
        return bikeOut;
    }

    public void setBikeOut(String bikeOut) {
        this.bikeOut = bikeOut;
    }

    public String getCarPresent() {
        return carPresent;
    }

    public void setCarPresent(String carPresent) {
        this.carPresent = carPresent;
    }

    public String getBikeCollection() {
        return bikeCollection;
    }

    public void setBikeCollection(String bikeCollection) {
        this.bikeCollection = bikeCollection;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getBikePresent() {
        return bikePresent;
    }

    public void setBikePresent(String bikePresent) {
        this.bikePresent = bikePresent;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }

    public String getCarIn() {
        return carIn;
    }

    public void setCarIn(String carIn) {
        this.carIn = carIn;
    }

}
