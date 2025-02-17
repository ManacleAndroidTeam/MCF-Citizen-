package com.citizen.fmc.fragment;

public class ComplaintCatModel {
    String id;
    String name;
    String icon_unicode;
    String dept_name;
    String dept_id;
    public ComplaintCatModel(String id,String name,String icon_unicode,String dept_name,String dept_id){
        this.id = id;
        this.name = name;
        this.icon_unicode = icon_unicode;
        this.dept_id = dept_id;
        this.dept_name = dept_name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon_unicode() {
        return icon_unicode;
    }

    public String getDept_name() {
        return dept_name;
    }

    public String getDept_id() {
        return dept_id;
    }
}
