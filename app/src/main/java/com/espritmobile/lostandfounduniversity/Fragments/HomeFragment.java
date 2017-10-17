package com.espritmobile.lostandfounduniversity.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.espritmobile.lostandfounduniversity.Adapters.HomeAdapter;
import com.espritmobile.lostandfounduniversity.AddPostActivity;
import com.espritmobile.lostandfounduniversity.LoginActivity;
import com.espritmobile.lostandfounduniversity.MainActivity;
import com.espritmobile.lostandfounduniversity.Models.Post;
import com.espritmobile.lostandfounduniversity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("posts");
    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference().child("users");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<Post> posts;
    private RecyclerView rvAnnonce;
    private HomeAdapter adapter;
    private TextView emptyView;
    private ProgressDialog progressDialog;
    private FloatingActionButton fabHomeAdd;
    private TextView tvMainTitle;
    private String phone = null;
    private String userId;

    ValueEventListener valueListener = null;
    Query queryRef = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading data from server");
        progressDialog.show();






        fabHomeAdd = (FloatingActionButton) view.findViewById(R.id.fab_home_add);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        valueListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                posts = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    posts.add(messageSnapshot.getValue(Post.class));
                }
                rvAnnonce = (RecyclerView) getActivity().findViewById(R.id.rv_annonce);
                emptyView = (TextView) getActivity().findViewById(R.id.empty_view);


                if (posts.isEmpty()) {
                    progressDialog.dismiss();
                    rvAnnonce.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);


                } else {
                    progressDialog.dismiss();
                    emptyView.setVisibility(View.GONE);
                    rvAnnonce.setVisibility(View.VISIBLE);
                    Collections.reverse(posts);
                    adapter = new HomeAdapter(getActivity(), posts);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    rvAnnonce.setAdapter(adapter);
                    rvAnnonce.setLayoutManager(linearLayoutManager);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvMainTitle = (TextView) getActivity().findViewById(R.id.tv_main_title);

        tvMainTitle.setText("Home");


        fabHomeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        dbRef.removeEventListener(valueListener);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
