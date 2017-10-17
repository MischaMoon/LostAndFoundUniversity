package com.espritmobile.lostandfounduniversity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.espritmobile.lostandfounduniversity.Adapters.MyPostsAdapter;
import com.espritmobile.lostandfounduniversity.Fragments.MyPostsFragment;
import com.espritmobile.lostandfounduniversity.Models.Post;
import com.espritmobile.lostandfounduniversity.Utils.ImagePicker;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class EditPostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private static final int GALLERY_REQUEST = 1;

    StorageReference strRef = FirebaseStorage.getInstance().getReference();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private Post post;
    private Post p;
    private int position;
    private boolean clicked = false;

    private SwipeSelector swipeSelector;
    private Spinner spinnerType, spinnerObject;
    private TextView tvDate, tvTime, tvUniversity;
    private String time, place, type, object, university, date, description, userId, other, uni, d, t;
    private EditText etPlace, etDescription, etObject;
    private ImageView ivPostEditImage;
    private FloatingActionButton fabPostEdit;
    private Button btnPostEdit;
    private Uri postPicUri = null;
    private MaterialTextField mtObject;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Bundle bundle = getIntent().getExtras();
        post = bundle.getParcelable(MyPostsAdapter.POST_EXTRA);
        position = bundle.getInt(MyPostsAdapter.POST_POSITION);


        etPlace = (EditText) findViewById(R.id.et_post_edit_place);
        etDescription = (EditText) findViewById(R.id.et_post_edit_desription);
        etObject = (EditText) findViewById(R.id.et_post_edit_Object);
        tvUniversity = (TextView) findViewById(R.id.tv_post_edit_university);
        tvDate = (TextView) findViewById(R.id.tv_post_edit_date);
        tvTime = (TextView) findViewById(R.id.tv_post_edit_time);
        btnPostEdit = (Button) findViewById(R.id.btn_post_edit);
        fabPostEdit = (FloatingActionButton) findViewById(R.id.fab_post_edit_image);
        ivPostEditImage = (ImageView) findViewById(R.id.iv_post_edit_image);
        mtObject = (MaterialTextField) findViewById(R.id.mt_post_edit_object);
        swipeSelector = (SwipeSelector) findViewById(R.id.ss_edit_post_university);
        swipeSelector.setItems(
                new SwipeItem(0, "ESPRIT", "Ecole supérieure privée d'ingénierie et de technologies"),
                new SwipeItem(1, "FST", "Faculté de science de Tunis."),
                new SwipeItem(2, "INSAT ", "Institut national des sciences appliquées et de technologie"),
                new SwipeItem(3, "ENIT ", "Ecole nationale d'ingénieurs de Tunis"),
                new SwipeItem(4, "IHEC ", "L'institut des hautes Etudes Commerciales de Carthage"),
                new SwipeItem(5, "UNIVERSITY ", "Select this one when you can't find the university you are looking for")
        );


        if (!post.getPostPicture().equals("")) {
            Picasso.with(this).load(post.getPostPicture()).noPlaceholder().centerCrop().fit().into(
                    ivPostEditImage
            );
        }

        spinnerType = (Spinner) findViewById(R.id.sp_edit_post_type);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        spinnerType.setSelection(adapterType.getPosition(post.getType()));

        spinnerObject = (Spinner) findViewById(R.id.sp_edit_post_object);

        ArrayAdapter<CharSequence> adapterObject = ArrayAdapter.createFromResource(this,
                R.array.object, android.R.layout.simple_spinner_item);
        adapterObject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObject.setAdapter(adapterObject);


        String[] objectTab = getResources().getStringArray(R.array.object);
        ArrayList<String> objects = new ArrayList<>(Arrays.asList(objectTab));
        if (objects.contains(post.getObject())) {
            spinnerObject.setSelection(adapterObject.getPosition(post.getObject()));
        } else {
            spinnerObject.setSelection(adapterObject.getPosition("Other"));
        }


        tvUniversity.setText(post.getPlacePost());
        tvUniversity.setVisibility(View.VISIBLE);

        tvDate.setText(post.getDate());
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EditPostActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        tvTime.setText(post.getTime());
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        EditPostActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        etPlace.setText(post.getPlace());
        etDescription.setText(post.getDescription());
        etObject.setText(post.getObject());


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

        tvUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;
                tvUniversity.setVisibility(View.GONE);
                swipeSelector.setVisibility(View.VISIBLE);

            }
        });


        if (clicked) {
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

        } else {
            university = post.getPlacePost();
        }


        spinnerObject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (spinnerObject.getSelectedItem().toString()) {
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

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = spinnerType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnPostEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d = tvDate.getText().toString();
                t = tvTime.getText().toString();
                userId = MainActivity.userId;
                place = etPlace.getText().toString();
                description = etDescription.getText().toString();


                if (type.equals("Type") || other.equals("Object") || place.equals("") || description.equals("")) {

                    Toast.makeText(EditPostActivity.this, "Missing fields", Toast.LENGTH_LONG).show();
                } else {

                    if (other.equals("Other")) {
                        object = etObject.getText().toString();
                    } else {
                        object = other;
                    }
                    userId = MainActivity.userId;

                    ivPostEditImage.setDrawingCacheEnabled(true);
                    ivPostEditImage.buildDrawingCache();

                    progressDialog = new ProgressDialog(EditPostActivity.this);
                    progressDialog.setMessage("Updating data");
                    progressDialog.show();



                    if (postPicUri == null ) {
                        p = new Post(userId, object, "Open", type, d, t, place, post.getTimePost(), university, post.getPostPicture(), description);
                        dbRef.child("posts").child(MyPostsFragment.KEYS.get(position)).removeValue();
                        dbRef.child("posts").push().setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(EditPostActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(EditPostActivity.this, "Post updated", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(EditPostActivity.this, "Error while updating post", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if(postPicUri != null && !post.getPostPicture().equals("")) {
                        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(post.getPostPicture());
                        httpsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String path = "posts/" + UUID.randomUUID() + ".png";
                                StorageReference filepath = strRef.child(path);
                                filepath.putFile(postPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri postPicUrl = taskSnapshot.getDownloadUrl();
                                        p = new Post(userId, object, "Open", type, d, t, place, post.getTimePost(), university, postPicUrl.toString(), description);
                                        dbRef.child("posts").child(MyPostsFragment.KEYS.get(position)).removeValue();
                                        dbRef.child("posts").push().setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(EditPostActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                Toast.makeText(EditPostActivity.this, "Post updated", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditPostActivity.this, "Error while updating post", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }


                                });


                                filepath.putFile(postPicUri).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditPostActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditPostActivity.this, "Failed to delete post image", Toast.LENGTH_LONG).show();
                            }
                        });

                    }else if (postPicUri != null && post.getPostPicture().equals("")){
                        String path = "posts/" + UUID.randomUUID() + ".png";
                        StorageReference filepath = strRef.child(path);
                        filepath.putFile(postPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri postPicUrl = taskSnapshot.getDownloadUrl();

                                p = new Post(userId, object, "Open", type, d, t, place, post.getTimePost(), university, postPicUrl.toString(), description);

                                dbRef.child("posts").child(MyPostsFragment.KEYS.get(position)).removeValue();
                                dbRef.child("posts").push().setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(EditPostActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(EditPostActivity.this, "Post updated", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditPostActivity.this, "Error while upadatio, post", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }


                        });

                        filepath.putFile(postPicUri).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(EditPostActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();

                            }
                        });
                    }

                }

            }
        });

        fabPostEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(EditPostActivity.this);
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
            Picasso.with(EditPostActivity.this).load(postPicUri).noPlaceholder().centerCrop().fit()
                    .into(ivPostEditImage);

        }
    }
}
