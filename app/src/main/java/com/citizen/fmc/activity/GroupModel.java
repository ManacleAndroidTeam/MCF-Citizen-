package com.citizen.fmc.activity;

public class GroupModel {

    private String groupName;
    private String parking_id;






    public GroupModel(String groupName,String parking_id) {
        this.groupName = groupName;
        this.parking_id = parking_id;
    }



    public String getParking_id() {
        return parking_id;
    }

    public void setParking_id(String parking_id) {
        this.parking_id = parking_id;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
