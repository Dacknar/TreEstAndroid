package com.uni.treest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.uni.treest.R;
import com.uni.treest.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class AddPostFragment extends Fragment {
    public static final String TAG = "AddPostFragment";

    private int did;
    private String sid;
    private AutoCompleteTextView autoCompleteDelay, autoCompleteState;
    private ArrayAdapter<String> adapterItemsDelay, adapterItemsState;
    private TextInputEditText mComment;
    private Button mDone;
    private FloatingActionButton mFab;
    private Integer delay, state = null;
    public AddPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_add_post, container, false);

        did = Preferences.getTheInstance().getLastDid(getContext());
        sid = Preferences.getTheInstance().getSid(getContext());

        String [] delays = {"In orario", "Ritardo di pochi minuti", "Ritardo oltre i 15 minuti", "Treni soppressi"};
        String [] states = {"Situazione ideale", "Accetabile", "Gravi problemi per i passeggeri"};

        autoCompleteDelay = fragmentView.findViewById(R.id.auto_complete_textDelay);
        autoCompleteState = fragmentView.findViewById(R.id.auto_complete_textState);
        mDone = fragmentView.findViewById(R.id.addPostDone);
        mFab = fragmentView.findViewById(R.id.fab_back);
        mComment = fragmentView.findViewById(R.id.addPostComment);

        adapterItemsState = new ArrayAdapter<String>(getContext(), R.layout.delay_list, states);
        adapterItemsDelay = new ArrayAdapter<String>(getContext(), R.layout.delay_list, delays);

        autoCompleteState.setAdapter(adapterItemsState);
        autoCompleteDelay.setAdapter(adapterItemsDelay);

        autoCompleteState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                state = Arrays.asList(states).indexOf(item);
                Log.d(TAG, "Current index: " + state + " With item: " + item);
            }
        });

        autoCompleteDelay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                delay = Arrays.asList(delays).indexOf(item);
                Log.d(TAG, "Current index: " + delay + " With item: " + item);
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "VALIDATION : " + validateInputs());
                if (validateInputs()) {
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    String url = "https://ewserver.di.unimi.it/mobicomp/treest/addPost.php";
                    String comment = mComment.getText().toString();
                    JSONObject jsonObjectUser = new JSONObject();
                    try {
                        jsonObjectUser.put("sid", sid);
                        jsonObjectUser.put("did", did);
                        jsonObjectUser.put("delay", delay);
                        jsonObjectUser.put("status", state);
                        jsonObjectUser.put("comment", comment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectUser, response -> {
                        Toast.makeText(getContext(), "Post inserito con successo", Toast.LENGTH_SHORT).show();
                        loadFragment();
                    }, error -> Log.d(TAG, "ERROR: " + error.toString()));
                    queue.add(jsonObjectRequest);
                }
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment();
            }
        });
        return fragmentView;
    }
    private Boolean validateInputs(){
        return delay != null || state != null || !mComment.getText().toString().equals("");
    }

    void loadFragment(){
        int switchedDid = Preferences.getTheInstance().getSwitchedLastDid(getContext());
        String direction = Preferences.getTheInstance().getDirection(getContext());
        String switchedDirection = Preferences.getTheInstance().getSwitchedDirection(getContext());
        Bundle args = new Bundle();
        args.putInt("did", did);
        args.putInt("switchedDid", switchedDid);
        args.putString("direction", direction);
        args.putString("switchDirection", switchedDirection);
        getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.containerView, PostFragment.class, args)
                .commit();
    }
}