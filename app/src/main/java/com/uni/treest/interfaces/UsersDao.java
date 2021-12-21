package com.uni.treest.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.uni.treest.Entitys.UsersImage;

import java.util.List;

@Dao
public interface UsersDao {

    @Query("SELECT * FROM UsersImage")
    List<UsersImage> getUsersImages();

    @Query("SELECT * FROM UsersImage WHERE uid = :id")
    UsersImage getUsersImage(int id);

    @Insert
    void insertUserImage(UsersImage user);

    @Update
    void updateUserImage(UsersImage user);
}
