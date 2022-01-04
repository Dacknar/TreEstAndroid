package com.uni.treest.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uni.treest.database.ImageDatabase;
import com.uni.treest.models.Line;
import com.uni.treest.models.Terminus;
import com.uni.treest.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LineFragmentViewModel extends AndroidViewModel {
    public static final String TAG = "LineFragmentTAG";
    RequestQueue queue;


    private MutableLiveData<List<Line>> allLines = new MutableLiveData<List<Line>>();
    private final Application application;
    public LineFragmentViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
        queue = Volley.newRequestQueue(application);
    }

    public LiveData<List<Line>> getLines() {
        Log.d(TAG, "FIRST RUN: " + Preferences.getTheInstance().isFirstRun(application));

        if(Preferences.getTheInstance().isFirstRun(application)){
            //Register user. Get SID and load lines.
            registerUser();
        }else{
            //Show lines
            loadLines(Preferences.getTheInstance().getSid(application));
        }
        return allLines;
    }
    private void registerUser(){
        String url = "https://ewserver.di.unimi.it/mobicomp/treest/register.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String sid = response.getString("sid");
                    Preferences.getTheInstance().setFirstRunAndSid(application, sid);
                    loadLines(sid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ERROR: " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void loadLines(String sid){

        List<Line> lines = new ArrayList<>();
        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getLines.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("lines");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject lineObject = jsonArray.getJSONObject(i);
                        Log.d(TAG, ": Element " + lineObject.toString());
                        JSONObject terminusObject1 = lineObject.getJSONObject("terminus1");
                        JSONObject terminusObject2 = lineObject.getJSONObject("terminus2");

                        Line line = new Line(new Terminus(terminusObject1.getString("sname"), terminusObject1.getInt("did"))
                                , new Terminus(terminusObject2.getString("sname"), terminusObject2.getInt("did")));
                        lines.add(line);
                    }
                    allLines.setValue(lines);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "ERRORE: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ERROR: " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }
}
