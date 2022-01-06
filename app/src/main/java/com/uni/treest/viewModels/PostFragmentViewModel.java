package com.uni.treest.viewModels;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uni.treest.Entitys.UsersImage;
import com.uni.treest.OnFollowClick;
import com.uni.treest.R;
import com.uni.treest.database.AsyncTaskCallback;
import com.uni.treest.database.asyncRequests.FindUserImageById;
import com.uni.treest.database.asyncRequests.GetAllUsersImagesAsync;
import com.uni.treest.database.asyncRequests.InsertUserImageAsync;
import com.uni.treest.database.asyncRequests.UpdateUserImageAsync;
import com.uni.treest.fragments.PostFragment;
import com.uni.treest.models.Post;
import com.uni.treest.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostFragmentViewModel extends AndroidViewModel {
    public static final String TAG = "PostFragmentTAG";

    private final Application application;
    private MutableLiveData<List<Post>> allPosts = new MutableLiveData<>();
    private List<UsersImage> databaseImages = null;
    RequestQueue queue;

    public PostFragmentViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
        queue = Volley.newRequestQueue(application);
    }

    public void performFollow(int authorId, boolean isFollowing){
        String url = isFollowing ? "https://ewserver.di.unimi.it/mobicomp/treest/unfollow.php" : "https://ewserver.di.unimi.it/mobicomp/treest/follow.php";
        String sid = Preferences.getTheInstance().getSid(application);
        String uid = String.valueOf(authorId);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sid", sid);
            jsonObject.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                for(int i = 0; i< allPosts.getValue().size(); i++){
                    if(allPosts.getValue().get(i).getAuthorID().equals(String.valueOf(authorId))){
                        allPosts.getValue().get(i).setFollowingAuthor(!isFollowing);
                        allPosts.setValue(allPosts.getValue());
                    }
                }
                if (isFollowing) {
                    Toast.makeText(application, "Unfollow utente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(application, "Follow utente", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ERROR:VOLLEY " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    public LiveData<List<Post>> getPosts(int did, int switchedDid, String direction ,String switchDirection){
        Preferences.getTheInstance().setDid(application, did);
        Preferences.getTheInstance().setSwitchedDid(application, switchedDid);
        Preferences.getTheInstance().setDirection(application, direction);
        Preferences.getTheInstance().setSwitchedDirection(application, switchDirection);
        String sid = Preferences.getTheInstance().getSid(application);
        loadPosts(did, sid);
        return allPosts;
    }

    private void loadPosts(int did, String sid){
        //===============================GET ALL IMAGES===================
        new GetAllUsersImagesAsync(application, new AsyncTaskCallback<List<UsersImage>>() {
            @Override
            public void handleResponse(List<UsersImage> response) {
                databaseImages = response;
            }
            @Override
            public void handleError(Exception e) {
                Log.d(TAG, "AN ERROR OCCURED: " + e.getMessage());
                databaseImages = new ArrayList<>();
            }
        }).execute();
        //===============================GET ALL IMAGES===================

        List<Post> posts = new ArrayList<>();
        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getPosts.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sid", sid);
            jsonObject.put("did", did);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("posts");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject lineObject = jsonArray.getJSONObject(i);
                            //Log.d(TAG, ": Element " + lineObject.toString());
                            int delay =  lineObject.isNull("delay") ? 10 : lineObject.getInt("delay");
                            int status = lineObject.isNull("status") ? 10 : lineObject.getInt("status");
                            String comment = lineObject.isNull("comment") ? "" : lineObject.getString("comment");
                            boolean followingAuthor = lineObject.getBoolean("followingAuthor");
                            String datetime = lineObject.getString("datetime");
                            String authorName = lineObject.getString("authorName").equals("unnamed") ? "Senza nome" : lineObject.getString("authorName");
                            String pversion = lineObject.getString("pversion");
                            String author = lineObject.getString("author");
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Post post = new Post(delay, status, comment, followingAuthor, format.parse(datetime), authorName, pversion, author);

                            //Aggiungo le immagini che ci sono in DB
                            for(UsersImage image: databaseImages){
                                if (image.getUid() == Integer.parseInt(author)){
                                    if(!image.getImage().equals("null")) { // se l'immagine non è nulla, aggiungila
                                        byte[] decodedString = Base64.decode(image.getImage(), Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        post.setAuthorImage(decodedByte);
                                    }
                                }
                            }
                            posts.add(post);
                        }catch (Exception e){
                            Log.e(TAG, "ERRORE IN FOR POSTS: " + e.toString());
                            //In caso di errore ricarico il fragment.
                            int did = Preferences.getTheInstance().getLastDid(application);
                            int switchedDid = Preferences.getTheInstance().getSwitchedLastDid(application);
                            String direction = Preferences.getTheInstance().getDirection(application);
                            String switchedDirection = Preferences.getTheInstance().getSwitchedDirection(application);
                            Bundle args = new Bundle();
                            args.putInt("did", did);
                            args.putInt("switchedDid", switchedDid);
                            args.putString("direction", direction);
                            args.putString("switchDirection", switchedDirection);
                            ((FragmentActivity)application.getApplicationContext()).getSupportFragmentManager().beginTransaction()
                                    .setReorderingAllowed(true)
                                    .replace(R.id.containerView, PostFragment.class, args)
                                    .commit();
                        }

                        allPosts.setValue(posts);
                    }
                    getUserPicture();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ERRORE JSON ON POSTS: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ERROR:VOLLEY " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    void getUserPicture() {
        String sid = Preferences.getTheInstance().getSid(application);
        List<Post> posts = allPosts.getValue();
        List<Integer> repeatedImages = new ArrayList<>();

        for (int i = 0; i < allPosts.getValue().size(); i++) {
            int uid = Integer.parseInt(posts.get(i).getAuthorID());
            String url = "https://ewserver.di.unimi.it/mobicomp/treest/getUserPicture.php";
            JSONObject jsonObjectImage = new JSONObject();
            try {
                jsonObjectImage.put("sid", sid);
                jsonObjectImage.put("uid", uid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int finalI = i;
            JsonObjectRequest jsonObjectRequestImage = new JsonObjectRequest(Request.Method.POST, url, jsonObjectImage, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String base64Image = response.getString("picture");
                        int imageVersion = Integer.parseInt(response.getString("pversion"));
                        UsersImage usersImage = new UsersImage(uid, base64Image, imageVersion);
                        new FindUserImageById(application, uid, new AsyncTaskCallback<UsersImage>() {
                            @Override
                            public void handleResponse(UsersImage response) {
                                Log.d(TAG, "Immagein corrente : " + response.getImageVersion() + " Immageine atuale: " + imageVersion);
                                if(response.getImageVersion() != imageVersion){
                                    new UpdateUserImageAsync(application, usersImage, new AsyncTaskCallback<UsersImage>() {
                                        @Override
                                        public void handleResponse(UsersImage response) {
                                            Log.d(TAG, "User updated, image updated");
                                            //Aggiungo alla lista l'ID dell'utente con l'immagine aggiornata così che si aggiornino i prossimi post con l'immagine nuova presente.
                                            repeatedImages.add(response.getUid());
                                            setUserPicture(base64Image, finalI, posts);
                                        }

                                        @Override
                                        public void handleError(Exception e) {
                                            Log.d(TAG, "User update ERROR: " + e.getMessage());

                                        }
                                    }).execute();
                                }else{
                                    //Il caso in cui l'utente è stato inserito ma si presenta più di una volta
                                    if(repeatedImages.contains(response.getUid())){
                                        Log.d(TAG, "CONTAINS: ");
                                        setUserPicture(base64Image, finalI, posts);
                                    }
                                    Log.d(TAG, "Utente: " + uid + " ha la stessa versione di immagine, SKIP");
                                }
                            }

                            @Override
                            public void handleError(Exception e) {
                                new InsertUserImageAsync(application, usersImage, new AsyncTaskCallback<UsersImage>() {
                                    @Override
                                    public void handleResponse(UsersImage response) {
                                        Log.d(TAG, "New User, image inserted");
                                        setUserPicture(base64Image, finalI, posts);
                                        repeatedImages.add(response.getUid());
                                        Log.d(TAG, "Utente: " + response.getUid() + " inserito");
                                    }
                                    @Override
                                    public void handleError(Exception e) {
                                        Log.d(TAG, "Inserimento utente ERRORE: " + e.getMessage());

                                    }
                                }).execute();
                            }
                        }).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "ERRORE JSON IMAGE: " + e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "ERROR WITH IMAGE: " + error.toString());
                }
            });
            queue.add(jsonObjectRequestImage);
        }
    }
    private void setUserPicture(String base64Image, int position, List<Post> posts){
        Log.d(TAG, "IMMAGINE INSERITA DA GETIMAGERS: ");
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        //Quanto scritto male, mi fanno male gli occhi. Prendo il post, lo aggiorno e lo aggiungo. Rimetto nel array di mutableLiveData per essere aggiornato anhce nel adapter
        Post post = allPosts.getValue().get(position);
        post.setAuthorImage(decodedByte);
        posts.set(position, post);
        allPosts.setValue(posts);
    }
}
