package com.espritmobile.lostandfounduniversity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.espritmobile.lostandfounduniversity.Models.User;
import com.espritmobile.lostandfounduniversity.Utils.ImagePicker;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_register_username, et_register_email, et_register_Password, et_register_telephone;
    private Spinner spRegisterUniversity;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAthListener;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Button btn_register_signup;
    private ImageView ib_register_userpic;
    private static final int PICK_IMAGE_ID = 234;
    Context context = this;
    int index = 0, deleteIndex = 0;
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth;
    private String university;
    private Uri userPicUri;
    String token = FirebaseInstanceId.getInstance().getToken();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        et_register_username = (EditText) findViewById(R.id.et_register_username);
        et_register_email = (EditText) findViewById(R.id.et_register_email);
        et_register_Password = (EditText) findViewById(R.id.et_register_Password);
        et_register_telephone = (EditText) findViewById(R.id.et_register_telephone);
        btn_register_signup = (Button) findViewById(R.id.btn_register_signup);

        initCustomSpinner();
//        btn_register_signup.setOnClickListener(this);
        ib_register_userpic = (ImageView) findViewById(R.id.ib_register_userpic);



        mProgress = new ProgressDialog(this);


        mAthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        };


    }




    private void initCustomSpinner() {

        Spinner spinnerCustom = (Spinner) findViewById(R.id.sp_register_university);
        // Spinner Drop down elements
        ArrayList<String> languages = new ArrayList<String>();
        languages.add("ESPRIT");
        languages.add("FST");
        languages.add("INSAT");
        languages.add("ENIT ");
        languages.add("IHEC ");
        languages.add("UNIVERSITY");


        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(RegisterActivity.this, languages);
        spinnerCustom.setAdapter(customSpinnerAdapter);
        spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                university = item;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//    @Override
//    public void onClick(View view) {
//        startRegister();
//    }

//    private void startRegister() {
//
//
//
//
//
//    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context, ArrayList<String> asr) {
            this.asr = asr;
            activity = context;
        }

        public int getCount() {
            return asr.size();
        }

        public Object getItem(int i) {
            return asr.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(RegisterActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(RegisterActivity.this);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(10, 0, 0, 0);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.hat, 0, 0, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return txt;
        }


    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAthListener);


        btn_register_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = et_register_username.getText().toString().trim();
                final String email = et_register_email.getText().toString().trim();
                final String password = et_register_Password.getText().toString();
                final String telephone = et_register_telephone.getText().toString().trim();


                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(telephone)) {

                    mProgress.setMessage("Registering, please wait  ");
                    mProgress.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if (task.isSuccessful()) {

                                if (userPicUri == null) {

                                    User user = new User(email, name, password, telephone, university, "", token);
                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid()+"Gadour");
                                            mProgress.dismiss();
                                            mAuth.signInWithEmailAndPassword(email, password);
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(RegisterActivity.this, "Welcome" + " " + name, Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mProgress.dismiss();
                                            System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid()+"Gadour1");
                                            Toast.makeText(RegisterActivity.this, "Error while registring1", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {

                                    String path = "profilepics/" + UUID.randomUUID() + ".png";
                                    StorageReference filepath = mStorage.child(path);
                                    filepath.putFile(userPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid()+"Gadour2");
                                            Uri userPicUrl = taskSnapshot.getDownloadUrl();

                                            User user = new User(email, name, password, telephone, university, userPicUrl.toString(), token);


                                            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProgress.dismiss();
                                                    System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid()+"Gadour3");
                                                    mAuth.signInWithEmailAndPassword(email, password);
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                    Toast.makeText(RegisterActivity.this, "Welcome" + " " + name, Toast.LENGTH_LONG).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mProgress.dismiss();
                                                    System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid()+"Gadour4");
                                                    Toast.makeText(RegisterActivity.this, "Error while registring2", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }


                                    });

                                    filepath.putFile(userPicUri).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mProgress.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();

                                        }
                                    });

                                }



                            } else
                                Toast.makeText(RegisterActivity.this, "error registering", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(RegisterActivity.this, "Please full fill all fields", Toast.LENGTH_LONG).show();
                }






            }
        });

        ib_register_userpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(RegisterActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK) {
            Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
            userPicUri = Uri.parse(path);
            Picasso.with(RegisterActivity.this).load(userPicUri).noPlaceholder().centerCrop().fit()
                    .into(ib_register_userpic);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAthListener != null) {
            mAuth.removeAuthStateListener(mAthListener);
        }
    }

}
