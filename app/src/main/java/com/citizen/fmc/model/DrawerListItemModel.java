package com.citizen.fmc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-23 <======
 */

public class DrawerListItemModel implements Serializable, Parcelable {
    @SerializedName("id")
    private int moduleId;

    @SerializedName("title_name")
    private String moduleTitleName;

    @SerializedName("icon_unicode")
    private String moduleIconUniCode;

    @SerializedName("url_status")
    private int moduleURLStatus;

    @SerializedName("url")
    private String moduleURL;

    @SerializedName("url_hindi")
    private String hindiURL;

    @SerializedName("title_hindi")
    private String titleHindi;

    @SerializedName("sub_module")

    private ArrayList<DrawerListItemModel> subItemModelList;


    public int getModuleId() {
        return moduleId;
    }

    public String getModuleTitleName() {
        return moduleTitleName;
    }

    public String getModuleIconUniCode() {
        return moduleIconUniCode;
    }

    public int getModuleURLStatus() {
        return moduleURLStatus;
    }

    public String getModuleURL() {
        return moduleURL;
    }

    public String getHindiURL() {
        return hindiURL;
    }

    public String getTitleHindi() {
        return titleHindi;
    }

    public ArrayList<DrawerListItemModel> getSubItemModelList() {
        return subItemModelList;
    }

    public static Creator<DrawerListItemModel> getCREATOR() {
        return CREATOR;
    }

    protected DrawerListItemModel(Parcel in) {
        moduleId = in.readInt();
        moduleTitleName = in.readString();
        moduleIconUniCode = in.readString();
        moduleURLStatus = in.readInt();
        moduleURL = in.readString();
        hindiURL = in.readString();
        titleHindi = in.readString();
        subItemModelList = in.createTypedArrayList(DrawerListItemModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(moduleId);
        dest.writeString(moduleTitleName);
        dest.writeString(moduleIconUniCode);
        dest.writeInt(moduleURLStatus);
        dest.writeString(moduleURL);
        dest.writeString(hindiURL);
        dest.writeString(titleHindi);
        dest.writeTypedList(subItemModelList);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<DrawerListItemModel> CREATOR = new Creator<DrawerListItemModel>() {
        @Override
        public DrawerListItemModel createFromParcel(Parcel in) {
            return new DrawerListItemModel(in);
        }

        @Override
        public DrawerListItemModel[] newArray(int size) {
            return new DrawerListItemModel[size];
        }
    };

    @Override
    public String toString() {
        return "DrawerListItemModel{" +
                "moduleId=" + moduleId +
                ", moduleTitleName='" + moduleTitleName + '\'' +
                ", moduleIconUniCode='" + moduleIconUniCode + '\'' +
                ", moduleURLStatus=" + moduleURLStatus +
                ", moduleURL='" + moduleURL + '\'' +
                ", hindiUrl='" + hindiURL + '\'' +
                ", titleHindi='" + titleHindi + '\'' +
                ", subItemModelList=" + subItemModelList +
                '}' + "\n";
    }
}
