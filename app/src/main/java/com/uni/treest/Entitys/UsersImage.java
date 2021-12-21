package com.uni.treest.Entitys;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UsersImage {

    @PrimaryKey
    int uid;

    @ColumnInfo(name = "image")
    String image;

    @ColumnInfo(name = "imageVersion")
    int imageVersion;

    public UsersImage(int uid, String image, int imageVersion) {
        this.uid = uid;
        this.image = image;
        this.imageVersion = imageVersion;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageVersion() {
        return imageVersion;
    }

    public void setImageVersion(int imageVersion) {
        this.imageVersion = imageVersion;
    }
}
