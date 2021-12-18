package com.uni.treest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.uni.treest.OnTerminusClick;
import com.uni.treest.R;
import com.uni.treest.adapters.MainAdapter;
import com.uni.treest.adapters.PostsAdapter;
import com.uni.treest.utils.Preferences;
import com.uni.treest.viewModels.LineFragmentViewModel;


public class LinesFragment extends Fragment implements OnTerminusClick {

    RecyclerView recyclerView;
    Button selectDid;

    public static final String TAG = "LineFragmentTAG";

    public LinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_lines, container, false);
        //TODO: If user wants to see al lines from POSTS before navigating to this fragment set DID to -1
        if(Preferences.getTheInstance().getLastDid(getContext()) == -1){
            setUpView(fragmentView);
            Log.d(TAG, "Registered but DID not defined DID SI: " + Preferences.getTheInstance().getLastDid(getContext()));
        }else{
            loadFragment();
            Log.d(TAG, "DID is already defined, load fragment");
        }

        return fragmentView;
    }
    public void setUpView(View view){
        recyclerView = view.findViewById(R.id.mainRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MainAdapter adapter = new MainAdapter(this);
        recyclerView.setAdapter(adapter);

        LineFragmentViewModel viewModel = new ViewModelProvider(this).get(LineFragmentViewModel.class);

        viewModel.getLines().observe(getViewLifecycleOwner(), lines -> {
            adapter.setLines(lines);
        });
    }

    @Override
    public void onTerminusClick(int did, int switchedDid, String direction, String switchedDirection) {
        Bundle args = new Bundle();
        args.putInt("did", did);
        args.putInt("switchedDid", switchedDid);
        args.putString("direction", direction);
        args.putString("switchDirection", switchedDirection);
        Log.d(TAG, "DIR IS : " + direction + " TO: " + switchedDirection);

        getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.containerView, PostFragment.class, args)
                .commit();
    }
    void loadFragment(){
        int did = Preferences.getTheInstance().getLastDid(getContext());
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