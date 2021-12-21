package com.uni.treest.database.asyncRequests;

import android.content.Context;
import android.os.AsyncTask;

import com.uni.treest.Entitys.UsersImage;
import com.uni.treest.database.AsyncTaskCallback;
import com.uni.treest.database.ImageDatabase;

public class UpdateUserImageAsync extends AsyncTask<Integer, Void, UsersImage> {

    private Context context;
    private AsyncTaskCallback<UsersImage> callback;
    private Exception exception;
    private UsersImage usersImage;


    public UpdateUserImageAsync(Context context,  UsersImage usersImage, AsyncTaskCallback<UsersImage> callback) {
        this.context = context;
        this.callback = callback;
        this.usersImage = usersImage;
    }

    @Override
    protected UsersImage doInBackground(Integer... integers) {
        exception = null;

        try{
           ImageDatabase.getInstance(context).usersDao().insertUserImage(this.usersImage);
        }catch (Exception e){
            exception = e;
        }
        return this.usersImage;
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
