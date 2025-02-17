package com.citizen.fmc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationModal implements Parcelable {
    private String latitude;
    private String longitude;
    private String address;

    public LocationModal(String latitude, String longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    protected LocationModal(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
        address = in.readString();
    }

    public static final Creator<LocationModal> CREATOR = new Creator<LocationModal>() {
        @Override
        public LocationModal createFromParcel(Parcel in) {
            return new LocationModal(in);
        }

        @Override
        public LocationModal[] newArray(int size) {
            return new LocationModal[size];
        }
    };

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(address);
    }
}
