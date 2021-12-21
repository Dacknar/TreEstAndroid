package com.uni.treest.database.asyncRequests;

import android.content.Context;
import android.os.AsyncTask;

import com.uni.treest.Entitys.UsersImage;
import com.uni.treest.database.AsyncTaskCallback;
import com.uni.treest.database.ImageDatabase;

import java.util.List;

public class GetAllUsersImagesAsync extends AsyncTask<Integer, Void, List<UsersImage>> {

    private Context context;
    private AsyncTaskCallback<List<UsersImage>> callback;
    private Exception exception;


    public GetAllUsersImagesAsync(Context context, AsyncTaskCallback<List<UsersImage>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected List<UsersImage> doInBackground(Integer... integers) {
        exception = null;
        List<UsersImage> usersImages = null;
        try{
            usersImages = ImageDatabase.getInstance(context).usersDao().getUsersImages();
            if(usersImages.size() == 0){
                throw new Exception("Not data found in database");
            }
        }catch (Exception e){
            exception = e;
        }
        return usersImages;
    }

    @Override
    protected void onPostExecute(List<UsersImage> usersImages) {
        super.onPostExecute(usersImages);
        if(callback != null){
            if(exception == null){
                callback.handleResponse(usersImages);
            }else{
                callback.handleError(exception);
            }
        }
    }
}