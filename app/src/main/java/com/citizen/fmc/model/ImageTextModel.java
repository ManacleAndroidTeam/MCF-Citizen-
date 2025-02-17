package com.citizen.fmc.model;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-11 <======
 */

public class ImageTextModel {
    private int id;
    private String title;
    private int image;
    private int rightIconImage;

    public ImageTextModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public ImageTextModel(String title , int image , int rightIconImage , int id) {
        this.title = title;
        this.image = image;
        this.rightIconImage = rightIconImage;
        this.id = id;
    }

    public int getRightIconImage() {
        return rightIconImage;
    }

    public void setRightIconImage(int rightIconImage) {
        this.rightIconImage = rightIconImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
