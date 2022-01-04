package com.uni.treest.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.uni.treest.OnFollowClick;
import com.uni.treest.R;
import com.uni.treest.adapters.MainAdapter;
import com.uni.treest.adapters.PostsAdapter;
import com.uni.treest.utils.Preferences;
import com.uni.treest.viewModels.LineFragmentViewModel;
import com.uni.treest.viewModels.PostFragmentViewModel;

public class PostFragment extends Fragment implements OnFollowClick{

    public static final String TAG = "PostFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DID_PARAM = "did";
    private static final String SWITCHED_DID_PARAM = "switchedDid";
    private static final String DIRECTION = "direction";
    private static final String SWITCH_DIRECTION = "switchDirection";




    RecyclerView recyclerView;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton mPost, mMap, mSwitch;

    // TODO: Rename and change types of parameters
    private int did;
    private int switchedDid;
    private String direction;
    private String switchDirection;
    private PostFragmentViewModel viewModel;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            did = getArguments().getInt(DID_PARAM);
            switchedDid = getArguments().getInt(SWITCHED_DID_PARAM);
            direction = getArguments().getString(DIRECTION);
            switchDirection = getArguments().getString(SWITCH_DIRECTION);
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_item_direction:
                    switchDirection();
                    break;
                case R.id.menu_item_map:
                    setMap();
                    break;
                case R.id.menu_item_write:
                    loadAdPost();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_post, container, false);

        recyclerView = fragmentView.findViewById(R.id.postRecycler);
        floatingActionMenu = fragmentView.findViewById(R.id.floatingActionMenu);
        mMap = fragmentView.findViewById(R.id.menu_item_map);
        mPost = fragmentView.findViewById(R.id.menu_item_write);
        mSwitch = fragmentView.findViewById(R.id.menu_item_direction);

        mMap.setOnClickListener(clickListener);
        mPost.setOnClickListener(clickListener);
        mSwitch.setOnClickListener(clickListener);

        //Log.d(TAG, "LOADED DID : " + did + " SWITCHED DID SI: " + switchedDid + " AND DIRECTION IS: " + direction);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(direction);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = new ViewModelProvider(this).get(PostFragmentViewModel.class);


        PostsAdapter adapter = new PostsAdapter(Preferences.getTheInstance().getUserID(getContext()), this);
        recyclerView.setAdapter(adapter);

        viewModel.getPosts(did, switchedDid, direction,switchDirection).observe(getViewLifecycleOwner(), posts -> {
            adapter.setPosts(posts);
        });

        return fragmentView;
    }
    void switchDirection(){
        int did = Preferences.getTheInstance().getLastDid(getContext());
        int switchedDid = Preferences.getTheInstance().getSwitchedLastDid(getContext());
        Bundle args = new Bundle();
        args.putInt(DID_PARAM, switchedDid);
        args.putInt(SWITCHED_DID_PARAM, did);
        args.putString(DIRECTION, switchDirection);
        args.putString(SWITCH_DIRECTION, direction);
        getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.containerView, PostFragment.class, args)
                .commit();
    }
    void setMap(){
            Bundle args = new Bundle();
            args.putInt("did", did);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.containerView, MapsFragment.class, args)
                .commit();
    }
    void loadAdPost(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.containerView, AddPostFragment.class, null)
                .commit();
    }

    @Override
    public void OnFollowClick(int authorId, boolean isFollowing) {
        viewModel.performFollow(authorId, isFollowing);
    }
}