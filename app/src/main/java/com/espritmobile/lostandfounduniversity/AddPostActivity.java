package com.espritmobile.lostandfounduniversity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.espritmobile.lostandfounduniversity.Models.Post;
import com.espritmobile.lostandfounduniversity.Utils.ImagePicker;
import com.firebase.client.Firebase;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int GALLERY_REQUEST = 1;
    private static final int MY_PERMISSION_REQUEST_ACCESS_LOCATION = 1;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    StorageReference strRef = FirebaseStorage.getInstance().getReference();
    private SwipeSelector swipeSelector;
    private Spinner spinnerType, spinnerObject;
    private TextView tvDate, tvTime;
    private String time, place, type, object, university, date, description, userId, other;
    private EditText etPlace, etDescription,etObject;
    private ImageView ivPostAddImage;
    private FloatingActionButton fabPostAdd;
    private Button btnPostAdd;
    private Uri postPicUri = null;
    private MaterialTextField mtObject;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);


        etPlace = (EditText) findViewById(R.id.et_post_add_place);
        etDescription = (EditText) findViewById(R.id.et_post_add_desription);
        etObject = (EditText) findViewById(R.id.et_post_add_Object);
        btnPostAdd = (Button) findViewById(R.id.btn_post_add);
        fabPostAdd = (FloatingActionButton) findViewById(R.id.fab_post_add_image);
        ivPostAddImage = (ImageView) findViewById(R.id.iv_post_add_image);
        mtObject = (MaterialTextField) findViewById(R.id.mt_post_add_object);




        swipeSelector = (SwipeSelector) findViewById(R.id.ss_add_post_university);
        swipeSelector.setItems(
                new SwipeItem(0, "ESPRIT", "Ecole supérieure privée d'ingénierie et de technologies"),
                new SwipeItem(1, "FST", "Faculté de science de Tunis."),
                new SwipeItem(2, "INSAT ", "Institut national des sciences appliquées et de technologie"),
                new SwipeItem(3, "ENIT ", "Ecole nationale d'ingénieurs de Tunis"),
                new SwipeItem(4, "IHEC ", "L'institut des hautes Etudes Commerciales de Carthage"),
                new SwipeItem(5, "UNIVERSITY ", "Select this one when you can't find the university you are looking for")
        );



        spinnerType = (Spinner) findViewById(R.id.sp_add_post_type);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = spinnerType.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerObject = (Spinner) findViewById(R.id.sp_add_post_object);

        ArrayAdapter<CharSequence> adapterObject = ArrayAdapter.createFromResource(this,
                R.array.object, android.R.layout.simple_spinner_item);
        adapterObject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObject.setAdapter(adapterObject);
        spinnerObject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (spinnerObject.getSelectedItem().toString()){
                    case "Other":
                        mtObject.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mtObject.setVisibility(View.GONE);
                }
                other = spinnerObject.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        tvDate = (TextView) findViewById(R.id.tv_post_add_date);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddPostActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        tvTime = (TextView) findViewById(R.id.tv_post_add_time);
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddPostActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        tvDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        time = hourOfDay + "h" + minute;
        tvTime.setText(time);
    }

    @Override
    protected void onStart() {
        super.onStart();




        SwipeItem selectedItem = swipeSelector.getSelectedItem();

        int value = (Integer) selectedItem.value;

        if (value == 0) {
            university = "ESPRIT";

        }
        if (value == 1) {
            university = "FST";

        }
        if (value == 2) {
            university = "INSAT";

        }
        if (value == 3) {
            university = "ENIT";

        }
        if (value == 4) {
            university = "IHEC";

        }

        btnPostAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                place = etPlace.getText().toString();
                description = etDescription.getText().toString();


                if ( type.equals("Type") || other.equals("Object") || tvDate.getText().toString().equals("Pick date") ||
                        place.equals("") || tvTime.getText().toString().equals("Pick time") || description.equals("")){



                    Toast.makeText(AddPostActivity.this, "Please complete all fields", Toast.LENGTH_LONG).show();
                } else {

                    if (other.equals("Other")) {
                        object = etObject.getText().toString();
                    } else {
                        object = other;
                    }


                    long yourmilliseconds = System.currentTimeMillis();
                    final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm");
                    final Date resultdate = new Date(yourmilliseconds);



                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    ivPostAddImage.setDrawingCacheEnabled(true);
                    ivPostAddImage.buildDrawingCache();

                    progressDialog = new ProgressDialog(AddPostActivity.this);
                    progressDialog.setMessage("Uploading data to server");
                    progressDialog.show();

                    if(postPicUri == null){

                        Post post = new Post(userId, object, "Open", type, date, time, place, sdf.format(resultdate), university, "", description);
                        dbRef.child("posts").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(AddPostActivity.this, "Post added", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddPostActivity.this, "Error while adding post", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {

                        String path = "posts/" + UUID.randomUUID() + ".png";
                        StorageReference filepath = strRef.child(path);
                        filepath.putFile(postPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri postPicUrl = taskSnapshot.getDownloadUrl();

                                Post post = new Post(userId, object, "Open", type, date, time, place, sdf.format(resultdate), university, postPicUrl.toString(), description);


                                dbRef.child("posts").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(AddPostActivity.this, "Post added", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddPostActivity.this, "Error while adding post", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }


                        });

                        filepath.putFile(postPicUri).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddPostActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
            }


        });

        fabPostAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(AddPostActivity.this);
                startActivityForResult(chooseImageIntent, GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
            postPicUri = Uri.parse(path);
            Picasso.with(AddPostActivity.this).load(postPicUri).noPlaceholder().centerCrop().fit()
                    .into(ivPostAddImage);

        }
    }

    @Override
    protected void onStop() {
//        mGoogleApiClient.disconnect();
        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }


//    private void getMyLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//
//            //------------------------------------------------------------------------------
//            ActivityCompat.requestPermissions(AddPostActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSION_REQUEST_ACCESS_LOCATION);
//
//            return;
//        }
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            geocoder = new Geocoder(AddPostActivity.this, Locale.getDefault());
//
//            latitude = mLastLocation.getLatitude();
//            longitude = mLastLocation.getLongitude();
//
//            try {
//                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//
//            Toast.makeText(AddPostActivity.this,
//                    String.valueOf(mLastLocation.getLatitude()) + "\n"
//                            + String.valueOf(mLastLocation.getLongitude()),
//                    Toast.LENGTH_LONG).show();
//
//            System.out.println("AAAAA"+state+"----"+address+"----"+city+"-----"+country+"-----"+postalCode+"-----"+knownName);
//
//
//
//        }else{
//            Toast.makeText(AddPostActivity.this,
//                    "mLastLocation == null",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        switch (requestCode) {
//            case MY_PERMISSION_REQUEST_ACCESS_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(AddPostActivity.this,
//                            "permission was granted, :)",
//                            Toast.LENGTH_LONG).show();
//                    getMyLocation();
//
//                } else {
//                    Toast.makeText(AddPostActivity.this,
//                            "permission denied, ...:(",
//                            Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        getMyLocation();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Toast.makeText(AddPostActivity.this,
//                "onConnectionSuspended: " + String.valueOf(i),
//                Toast.LENGTH_LONG).show();
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(AddPostActivity.this,
//                "onConnectionFailed: \n" + connectionResult.toString(),
//                Toast.LENGTH_LONG).show();
//
//    }
}