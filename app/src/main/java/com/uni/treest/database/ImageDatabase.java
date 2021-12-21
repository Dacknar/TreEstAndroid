package com.uni.treest.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.uni.treest.Entitys.UsersImage;
import com.uni.treest.interfaces.UsersDao;

@Database(entities = {UsersImage.class}, version = 2)
public abstract class ImageDatabase extends RoomDatabase {

   private static volatile ImageDatabase INSTANCE;

   public abstract UsersDao usersDao();

   public static ImageDatabase getInstance(Context context) {
      if (INSTANCE == null) {
         synchronized (ImageDatabase.class) {
            if (INSTANCE == null) {
               INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                       ImageDatabase.class, "Images.db")
                       .fallbackToDestructiveMigration()
                       .build();
            }
         }
      }
      return INSTANCE;
   }
}
