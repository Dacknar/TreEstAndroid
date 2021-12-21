package com.uni.treest.database.asyncRequests;

import android.content.Context;
import android.os.AsyncTask;

import com.uni.treest.Entitys.UsersImage;
import com.uni.treest.database.AsyncTaskCallback;
import com.uni.treest.database.ImageDatabase;

public class FindUserImageById extends AsyncTask<Integer, Void, UsersImage> {

    private Context context;
    private AsyncTaskCallback<UsersImage> callback;
    private Exception exception;
    private int id;


    public FindUserImageById(Context context,  int id, AsyncTaskCallback<UsersImage> callback) {
        this.context = context;
        this.callback = callback;
        this.id = id;
    }

    @Override
    protected UsersImage doInBackground(Integer... integers) {
        exception = null;
        UsersImage usersImage = null;
        try{
            usersImage = ImageDatabase.getInstance(context).usersDao().getUsersImage(this.id);
            if(usersImage == null){
                throw new Exception("User does not exist");
            }
        }catch (Exception e){
            exception = e;
        }
        return usersImage;
    }

    @Override
    protected void onPostExecute(UsersImage usersImage) {
        super.onPostExecute(usersImage);
        if(callback != null){
            if(exception == null){
                callback.handleResponse(usersImage);
            }else{
                callback.handleError(exception);
            }
        }
    }
}

