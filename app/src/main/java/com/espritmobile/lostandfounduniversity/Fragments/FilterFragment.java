package com.espritmobile.lostandfounduniversity.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.espritmobile.lostandfounduniversity.Models.Post;
import com.espritmobile.lostandfounduniversity.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {


    private ArrayList<Post> posts;
    private RecyclerView rvAnnonce;
    private HomeAdapter adapter;
    private TextView emptyView;
    private ProgressDialog progressDialog;
    private FloatingActionButton fabHomeAdd;
    private TextView tvMainTitle;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
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
        View view = inflater.inflate(R.layout.fragment_filter, container, false);





        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Searching,please wait...");
        progressDialog.show();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        rvAnnonce = (RecyclerView) getActivity().findViewById(R.id.rv_annonce);
        posts = SearchFragment.filtredPosts;
        progressDialog.dismiss();
        adapter = new HomeAdapter(getActivity(), posts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvAnnonce.setAdapter(adapter);
        rvAnnonce.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvMainTitle = (TextView) getActivity().findViewById(R.id.tv_main_title);
        tvMainTitle.setText("Search result");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        getFragmentManager().beginTransaction().remove(FilterFragment.this).commit();
    }
}
