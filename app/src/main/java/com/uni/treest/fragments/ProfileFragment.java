package com.uni.treest.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.uni.treest.R;
import com.uni.treest.database.ImageDatabase;
import com.uni.treest.models.Line;
import com.uni.treest.models.Terminus;
import com.uni.treest.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";



    private TextView mSelectImage;
    private Button mDone;
    private CircleImageView mImageView;
    private TextInputEditText mName;
    private String newImage = null;
    private String lastKnownName = null;

    public ProfileFragment() {
        // Required empty public constructor
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                try {
                    if(uri != null) {
                        Log.d(TAG, "The URI is : " + uri.toString());
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        Bitmap resizedImage = getResizedBitmap(bitmap, 230);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        resizedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        newImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        mImageView.setImageBitmap(resizedImage);
                    }
                } catch (IOException e) {
                    Log.d(TAG, "ERRORE PER BITMAT: " + e.getMessage());
                    e.printStackTrace();
                }

            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        mName = fragmentView.findViewById(R.id.profileName);
        mDone = fragmentView.findViewById(R.id.profileDone);
        mImageView = fragmentView.findViewById(R.id.profileImageView);
        mSelectImage = fragmentView.findViewById(R.id.profileUpdateImage);
        lastKnownName = Preferences.getTheInstance().getUserName(getContext());

        String base64Image = Preferences.getTheInstance().getUserPicture(getContext());
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        mImageView.setImageBitmap(decodedByte);


        Log.d(TAG, "SID: " + Preferences.getTheInstance().getSid(getContext()) + " UID: " + Preferences.getTheInstance().getUserID(getContext()));

        if(lastKnownName.equals("")){
            mName.setText("");
        }else {
            mName.setText(lastKnownName);
        }

        RequestQueue queue = Volley.newRequestQueue(getContext());

        View.OnClickListener listener = v -> PermissionX.init(getActivity())
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(deniedList, "Per caricare l'immagine l'applicazione ha bisogno di questo permesso", "OK", "Anulla"))
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        Log.d(TAG, "Permission Granted");
                        mGetContent.launch("image/*");
                    } else {
                        Toast.makeText(getContext(), "Accesso alla memoria non consentito", Toast.LENGTH_LONG).show();
                    }
                });

        mImageView.setOnClickListener(listener);
        mSelectImage.setOnClickListener(listener);


        mDone.setOnClickListener(view -> {
            if(newImage != null || !mName.getText().toString().equals(lastKnownName)){
                loadUser(queue);
            }else{
                Toast.makeText(getContext(), "ERRORE: Modificare uno dei due parametri per aggiornare", Toast.LENGTH_SHORT).show();

            }
        });

        return fragmentView;
    }
    private void loadUser(RequestQueue requestQueue){
        String url = "https://ewserver.di.unimi.it/mobicomp/treest/setProfile.php";
        String sid = Preferences.getTheInstance().getSid(getContext());
        String name = mName.getText().toString();
        JSONObject jsonObjectUser = new JSONObject();
        try {
            jsonObjectUser.put("sid", sid);
            jsonObjectUser.put("name", name);
            jsonObjectUser.put("picture", newImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectUser, response -> {
            Preferences.getTheInstance().setUserName(getContext(),name);
            if(newImage != null){
                Preferences.getTheInstance().setUserPicture(getContext(),newImage);
            }
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            View currVierw = getActivity().getCurrentFocus();
            if(currVierw != null){
                imm.hideSoftInputFromWindow(currVierw.getWindowToken(), 0);
                currVierw.clearFocus();
            }
            Toast.makeText(getContext(), "Dati aggiornati", Toast.LENGTH_SHORT).show();
        }, error -> Log.d(TAG, "ERROR: " + error.toString()));
        requestQueue.add(jsonObjectRequest);

    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}