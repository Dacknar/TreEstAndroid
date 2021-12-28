package com.uni.treest.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.uni.treest.database.ImageDatabase;
import com.uni.treest.models.Line;
import com.uni.treest.models.Station;
import com.uni.treest.models.Terminus;
import com.uni.treest.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentViewModel extends AndroidViewModel {
    public static final String TAG = "MapsFragmentTAG";
    private MutableLiveData<List<Station>> stations = new MutableLiveData<>();
    RequestQueue queue;
    private final Application application;


    public MapsFragmentViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        queue = Volley.newRequestQueue(application);
    }

    public MutableLiveData<List<Station>> getStations (int did){
        List<Station> tempStations = new ArrayList<>();
        String sid = Preferences.getTheInstance().getSid(application);
        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getStations.php";
        Log.d(TAG, "SID: " + sid + " DID: " + did);

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
                    JSONArray jsonArray = response.getJSONArray("stations");
                    Log.d(TAG, "array length is : " + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject stationObject = jsonArray.getJSONObject(i);
                        String stationName = stationObject.getString("sname");
                        float stationLat = Float.parseFloat(stationObject.getString("lat"));
                        float stationLon = Float.parseFloat(stationObject.getString("lon"));

                        Station station = new Station(stationName, new LatLng(stationLat, stationLon));
                        tempStations.add(station);
                        Log.d(TAG, "ADDED STATION : " + stationName + " Coordinate LAT: " + stationLat + " LON: " + stationLon);
                    }
                    stations.setValue(tempStations);

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
        return stations;
    }
}
