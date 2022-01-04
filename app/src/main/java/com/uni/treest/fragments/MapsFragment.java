package com.uni.treest.fragments;


import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.uni.treest.R;
import com.uni.treest.models.Station;
import com.uni.treest.utils.Preferences;
import com.uni.treest.viewModels.MapsFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "MapFragment";
    private static final String DID = "did";
    private GoogleMap map;
    private MapsFragmentViewModel viewModel;
    private FloatingActionButton fab;

    private int did;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            did = getArguments().getInt(DID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fab = fragmentView.findViewById(R.id.fab_back);
        viewModel = new ViewModelProvider(this).get(MapsFragmentViewModel.class);
        Log.d(TAG, "THE DID IS : " + did);
        mapFragment.getMapAsync(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment();
            }
        });
        // Inflate the layout for this fragment
        return fragmentView;
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
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        viewModel.getStations(did).observe(getViewLifecycleOwner(), new Observer<List<Station>>() {
            @Override
            public void onChanged(List<Station> stations) {
                LatLng[] lineCoordinates = new LatLng[stations.size()];

                for (int i = 0; i < stations.size(); i++) {
                    map.addMarker(new MarkerOptions()
                            .position(stations.get(i).getLatLng())
                            .title(stations.get(i).getsName()));
                    lineCoordinates[i] = stations.get(i).getLatLng();
                }
                Polyline connectionLine = googleMap.addPolyline(new PolylineOptions().clickable(true).add(lineCoordinates));
                connectionLine.setEndCap(new RoundCap());
                connectionLine.setWidth(16);
                connectionLine.setColor(0xff1A8D18);
                connectionLine.setJointType(JointType.DEFAULT);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(stations.get(0).getLatLng(), 15));
            }
        });

    }
}