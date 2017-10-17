package com.espritmobile.lostandfounduniversity.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.espritmobile.lostandfounduniversity.Adapters.HomeAdapter;
import com.espritmobile.lostandfounduniversity.AddPostActivity;
import com.espritmobile.lostandfounduniversity.MainActivity;
import com.espritmobile.lostandfounduniversity.Models.Post;
import com.espritmobile.lostandfounduniversity.R;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private Spinner spSearchType,spSearchObject,spSearchDate;
    private MaterialTextField mtSearchObject;
    private EditText etSearchObject;
    private CheckBox swSearchObject,cbSearchDate;
    private Button btnSearch;
    private String other,type,object,d,date,firstDate,secondDate;
    private Boolean objChecked,dateChecked,spinSelected;
    private TextView tvFirstDate,tvSecondDate,tvDate,tvMainTitle;
    private int dd;
    private ArrayList<Post> posts;
    private ArrayList<Post> filterObjet ;
    private ArrayList<Post> filterDate;
    private ArrayList<Post> filterObjDate;
    private ArrayList<Post> filterO ;
    private ArrayList<Post> filterD ;
    boolean dateSetted = true;

    private ProgressDialog progressDialog;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("posts");
    ValueEventListener valueListener=null;
    Query queryRef = null;

    public static ArrayList<Post> filtredPosts;








    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        spSearchType = (Spinner) view.findViewById(R.id.sp_search_type);
        spSearchObject = (Spinner) view.findViewById(R.id.sp_search_post_object);
        spSearchDate = (Spinner) view.findViewById(R.id.sp_search_date);
        cbSearchDate = (CheckBox) view.findViewById(R.id.cb_search_date);
        etSearchObject = (EditText) view.findViewById(R.id.et_search_object);
        mtSearchObject = (MaterialTextField) view.findViewById(R.id.mt_search_object);
        swSearchObject = (CheckBox) view.findViewById(R.id.sw_search_object);
        tvDate =(TextView) view.findViewById(R.id.tv_search_date);
        tvFirstDate =(TextView) view.findViewById(R.id.tv_search_date_1);
        tvSecondDate =(TextView) view.findViewById(R.id.tv_search_date_2);
        btnSearch = (Button) view.findViewById(R.id.btn_search);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(getActivity(),
                R.array.type, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchType.setAdapter(adapterType);

        ArrayAdapter<CharSequence> adapterObject = ArrayAdapter.createFromResource(getActivity(),
                R.array.object, android.R.layout.simple_spinner_item);
        adapterObject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchObject.setAdapter(adapterObject);

        ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(getActivity(),
                R.array.search, android.R.layout.simple_spinner_item);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchDate.setAdapter(adapterDate);

        mtSearchObject.setVisibility(View.GONE);
        spSearchObject.setVisibility(View.GONE);
        tvDate.setVisibility(View.GONE);
        tvFirstDate.setVisibility(View.GONE);
        tvSecondDate.setVisibility(View.GONE);
        spSearchDate.setVisibility(View.GONE);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SearchFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                dd = 0;
            }
        });
        tvFirstDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SearchFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "FirstDatepickerdialog");
                dd = 1;
            }
        });
        tvSecondDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SearchFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "SecondDatepickerdialog");
                dd = 2;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvMainTitle = (TextView) getActivity().findViewById(R.id.tv_main_title);
        tvMainTitle.setText("Customized search");

    }








    @Override
    public void onStart() {
        super.onStart();

        spSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = spSearchType.getSelectedItem().toString();
                queryRef = dbRef.orderByChild("type").equalTo(type);
                valueListener = queryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        posts = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            posts.add(messageSnapshot.getValue(Post.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (swSearchObject.isChecked() == false){
            objChecked = false;
        }else if (swSearchObject.isChecked() == true){
            objChecked = true;
        }
        swSearchObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    objChecked = true;
                    spSearchObject.setVisibility(View.VISIBLE);
                    spSearchObject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            switch (spSearchObject.getSelectedItem().toString()){
                                case "Other":
                                    mtSearchObject.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    mtSearchObject.setVisibility(View.GONE);
                            }
                            other = spSearchObject.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                }else{
                    objChecked = false;
                    mtSearchObject.setVisibility(View.GONE);
                    spSearchObject.setVisibility(View.GONE);
                }
            }
        });
        if(cbSearchDate.isChecked() == true){
            dateChecked = true;
        }else if (cbSearchDate.isChecked() == false){
            dateChecked = false;
        }
        cbSearchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    dateChecked = true;
                    spSearchDate.setVisibility(View.VISIBLE);
                    spSearchDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            switch (spSearchDate.getSelectedItem().toString()){
                                case "Single Date":
                                    dateSetted = true;
                                    tvDate.setVisibility(View.VISIBLE);
                                    tvFirstDate.setVisibility(View.GONE);
                                    tvSecondDate.setVisibility(View.GONE);
                                    spinSelected = true;
                                    break;
                                case "Period":
                                    dateSetted = true;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Important");
                                    builder.setMessage("To ensure an accurate search result second date must be greater than first date");
                                    builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            tvDate.setVisibility(View.GONE);
                                            tvFirstDate.setVisibility(View.VISIBLE);
                                            tvSecondDate.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    builder.show();
                                    spinSelected = false;
                                    break;
                                case "Search by":

                                    dateSetted = false;
                                    break;
                                default:
                                    tvDate.setVisibility(View.GONE);
                                    tvFirstDate.setVisibility(View.GONE);
                                    tvSecondDate.setVisibility(View.GONE);

                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                }else{
                    dateChecked = false;
                    tvDate.setVisibility(View.GONE);
                    tvFirstDate.setVisibility(View.GONE);
                    tvSecondDate.setVisibility(View.GONE);
                    spSearchDate.setVisibility(View.GONE);
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("Type")){

                    Toast.makeText(getActivity(), "Please select a type", Toast.LENGTH_LONG).show();
                }else {

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Searching...");
                    progressDialog.show();

                    if(posts.size() == 0){
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();

                    }else {
                        if (objChecked && !dateChecked) {
                            if (other.equals("Other")) {
                                object = etSearchObject.getText().toString();
                            } else {
                                object = other;
                            }

                            filterObjet = new ArrayList<>();
                            filtredPosts = new ArrayList<>();


                            for (Post p : posts) {
                                if (p.getObject().equals(object)) {
                                    filterObjet.add(p);

                                }
                            }
                            filtredPosts = filterObjet;
                            if (filtredPosts.size() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                            } else {
                                progressDialog.dismiss();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new FilterFragment()).commit();

                            }


                        } else if (dateChecked && !objChecked) {
                            filterDate = new ArrayList<>();
                            filtredPosts = new ArrayList<>();
                            if(dateSetted) {
                                if (spinSelected) {
                                    for (Post pp : posts) {
                                        if (pp.getDate().equals(date)) {
                                            filterDate.add(pp);


                                        }
                                    }

                                    filtredPosts = filterDate;
                                    if (filtredPosts.size() == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new FilterFragment()).commit();

                                    }
                                } else {
                                    filterDate = new ArrayList<>();

                                    List<Date> dates = new ArrayList<Date>();

                                    String str_date = firstDate;
                                    String end_date = secondDate;

                                    DateFormat formatter;

                                    formatter = new SimpleDateFormat("d/M/yyyy");
                                    Date startDate = null;
                                    try {
                                        startDate = (Date) formatter.parse(str_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        System.out.println("EROR HERE");
                                    }
                                    Date endDate = null;
                                    try {
                                        endDate = (Date) formatter.parse(end_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        System.out.println("ERROR NOT HERE");
                                    }
                                    long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
                                    long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
                                    long curTime = startDate.getTime();
                                    while (curTime <= endTime) {
                                        dates.add(new Date(curTime));
                                        curTime += interval;
                                    }
                                    for (int i = 0; i < dates.size(); i++) {
                                        Date lDate = (Date) dates.get(i);
                                        String ds = formatter.format(lDate);
                                        System.out.println(" Date is ..." + ds);
                                        for (Post ppp : posts) {
                                            if (ppp.getDate().equals(ds)) {
                                                filterDate.add(ppp);
                                            }
                                        }
                                    }

                                    filtredPosts = filterDate;
                                    if (filtredPosts.size() == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new FilterFragment()).commit();

                                    }


                                }
                            }else if(!dateSetted){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Select date search type", Toast.LENGTH_LONG).show();
                            }
                        } else if (objChecked && dateChecked) {
                            filterObjDate = new ArrayList<>();
                            filterObjet = new ArrayList<>();
                            filterDate = new ArrayList<>();
                            filterO = new ArrayList<>();
                            filterD = new ArrayList<>();

                            //ntala3 lista éli féha les objets similaires lél recherche
                            if (other.equals("Other")) {
                                object = etSearchObject.getText().toString();
                            } else if (!other.equals("Other")) {
                                object = other;
                            }
                            for (Post p : posts) {
                                if (p.getObject().equals(object)) {
                                    filterObjet.add(p);

                                }
                            }
                            filterO = filterObjet;
                            //ntala3 lista eli féha naffess él date ta3 él rechereche
                            if(dateSetted) {
                                if (spinSelected) {
                                    for (Post pp : posts) {
                                        if (pp.getDate().equals(date)) {
                                            filterDate.add(pp);
                                        }
                                    }

                                    filterD = filterDate;
                                } else if (!spinSelected) {
                                    filterDate = new ArrayList<>();

                                    List<Date> dates = new ArrayList<Date>();

                                    String str_date = firstDate;
                                    String end_date = secondDate;

                                    DateFormat formatter;

                                    formatter = new SimpleDateFormat("d/M/yyyy");
                                    Date startDate = null;
                                    try {
                                        startDate = (Date) formatter.parse(str_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        System.out.println("EROR HERE");
                                    }
                                    Date endDate = null;
                                    try {
                                        endDate = (Date) formatter.parse(end_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        System.out.println("ERROR NOT HERE");
                                    }
                                    long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
                                    long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
                                    long curTime = startDate.getTime();
                                    while (curTime <= endTime) {
                                        dates.add(new Date(curTime));
                                        curTime += interval;
                                    }
                                    for (int i = 0; i < dates.size(); i++) {
                                        Date lDate = (Date) dates.get(i);
                                        String ds = formatter.format(lDate);
                                        for (Post ppp : posts) {
                                            if (ppp.getDate().equals(ds)) {
                                                filterDate.add(ppp);
                                            }
                                        }
                                    }

                                    filterD = filterDate;

                                }
                            }else if(!dateSetted){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Select date search type", Toast.LENGTH_LONG).show();
                            }








                            int i = filterO.size();
                            int j = filterD.size();
                            if(i == 0 || j == 0){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                            }else if (i != 0 && j != 0)  {
                                if (i < j) {
                                    for (int k = 0; k < i; k++) {
                                        if (filterD.get(k).equals(filterO.get(k))) {
                                            filterObjDate.add(filterO.get(k));

                                        }
                                    }
                                    filtredPosts = filterObjDate;
                                    if (filtredPosts.size() == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new FilterFragment()).commit();

                                    }
                                } else if (i > j) {
                                    for (int h = 0; h < j; h++) {
                                        if (filterD.get(h).equals(filterO.get(h))) {
                                            filterObjDate.add(filterD.get(h));

                                        }
                                    }
                                    filtredPosts = filterObjDate;
                                    if (filtredPosts.size() == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new FilterFragment()).commit();

                                    }
                                } else if (i == j){
                                    for (int h = 0; h < j; h++) {
                                        if (filterD.get(h).equals(filterO.get(h))) {
                                            filterObjDate.add(filterD.get(h));

                                        }
                                    }
                                    filtredPosts = filterObjDate;
                                    if (filtredPosts.size() == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new FilterFragment()).commit();

                                    }

                                }
                            }
                        } else if (!objChecked && !dateChecked) {
                            filtredPosts = posts;
                            if (filtredPosts.size() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "No posts matching your search", Toast.LENGTH_LONG).show();
                            } else {
                                progressDialog.dismiss();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new FilterFragment()).commit();

                            }
                        }

                    }
                }
            }
        });

    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {


        if (dd == 0) {
            date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            tvDate.setText(date);
        }else if (dd == 1){
            firstDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            tvFirstDate.setText(firstDate);
        }else if (dd == 2){
            secondDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            tvSecondDate.setText(secondDate);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        queryRef.removeEventListener(valueListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        getFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
    }
}
