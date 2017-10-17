package com.espritmobile.lostandfounduniversity.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.espritmobile.lostandfounduniversity.LoginActivity;
import com.espritmobile.lostandfounduniversity.Models.User;
import com.espritmobile.lostandfounduniversity.R;
import com.espritmobile.lostandfounduniversity.RegisterActivity;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Noor on 0712/2016.
 */

public class ProfileFragment extends Fragment {
    private DatabaseReference mFirebaseRef;

    private FloatingActionButton btn_logout;
    private Button btn_update;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView user_profile_photo;
    private DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private Uri userPicUri;
    private static final int GALLERY_INTENT = 2;
    private EditText tv_username, tv_email, tv_university, tv_phone;
    private ImageButton btnUpdateProfileName, btnUpdatePhone, btnUpdateUniversity, btnUpdateMail, btnConfirmUpdate, btnRefusUpdate;
    private LinearLayout confirmRefusLayout;
    private String phone;
    Button btnUpdatePass,btnMenu;

    private TextView tvMainTitle;

    ValueEventListener valueListener = null;

    ArrayList<User> users;
    User u;

    String whatNow = "NOTHING";
    String currentName, currentMail, currentUniversity, currentPhone, currentPic = "";


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Firebase.setAndroidContext(getActivity());


        tv_phone = (EditText) v.findViewById(R.id.tv_phone);
        tv_university = (EditText) v.findViewById(R.id.tv_university);
        tv_username = (EditText) v.findViewById(R.id.tv_username);
        tv_email = (EditText) v.findViewById(R.id.tv_email);
        btnUpdateProfileName = (ImageButton) v.findViewById(R.id.btn_update_profile_name);
        btnUpdatePhone = (ImageButton) v.findViewById(R.id.btn_update_profile_phone);
        btnUpdateUniversity = (ImageButton) v.findViewById(R.id.btn_update_profile_university);
        btnUpdateMail = (ImageButton) v.findViewById(R.id.btn_update_profile_mail);
        btnUpdatePass = (Button) v.findViewById(R.id.btn_update_profile_pass);
        btnConfirmUpdate = (ImageButton) v.findViewById(R.id.btn_confirm_update);
        btnRefusUpdate = (ImageButton) v.findViewById(R.id.btn_refus_update);
        confirmRefusLayout = (LinearLayout) v.findViewById(R.id.layout_confirm_refus_update);
        user_profile_photo = (ImageView) v.findViewById(R.id.user_profile_photo);


        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                if (firebaseAuth.getCurrentUser() == null) {


                } else {

                    valueListener = dbref.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User currentUser = dataSnapshot.getValue(User.class);
                            tv_username.setText(currentUser.getUsername());
                            tv_email.setText(currentUser.getEmail());
                            tv_university.setText(currentUser.getUniversity());
                            tv_phone.setText(currentUser.getTelephone());
                            if (!currentUser.getUserPicture().equals("")) {
                                Picasso.with(getActivity()).load(currentUser.getUserPicture()).centerCrop()
                                        .resize(user_profile_photo.getMeasuredWidth(), user_profile_photo.getMeasuredHeight()).into(user_profile_photo);
                            }

                            currentName = currentUser.getUsername();
                            currentMail = currentUser.getEmail();
                            currentPhone = currentUser.getTelephone();
                            currentUniversity = currentUser.getUniversity();
                            currentPic = currentUser.getUserPicture();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


            }
        };


        btn_logout = (FloatingActionButton) v.findViewById(R.id.btn_logout);



        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));

            }
        });

        user_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_INTENT);
            }
        });

        btnUpdateProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatNow = "NAME";
                tv_username.setEnabled(true);
                confirmRefusLayout.setVisibility(View.VISIBLE);

                btnUpdateMail.setClickable(false);
                btnUpdateUniversity.setClickable(false);
                btnUpdatePhone.setClickable(false);
                user_profile_photo.setClickable(false);
            }
        });

        btnUpdatePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatNow = "PHONE";
                tv_phone.setEnabled(true);
                confirmRefusLayout.setVisibility(View.VISIBLE);

                btnUpdateMail.setClickable(false);
                btnUpdateUniversity.setClickable(false);
                btnUpdateProfileName.setClickable(false);
                user_profile_photo.setClickable(false);
            }
        });

        btnUpdateUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatNow = "UNIVERSITY";
                tv_university.setEnabled(true);
                confirmRefusLayout.setVisibility(View.VISIBLE);

                btnUpdateMail.setClickable(false);
                btnUpdateProfileName.setClickable(false);
                btnUpdatePhone.setClickable(false);
                user_profile_photo.setClickable(false);
            }
        });

        btnUpdateMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatNow = "MAIL";
                tv_email.setEnabled(true);
                confirmRefusLayout.setVisibility(View.VISIBLE);

                btnUpdateUniversity.setClickable(false);
                btnUpdateProfileName.setClickable(false);
                btnUpdatePhone.setClickable(false);
                user_profile_photo.setClickable(false);
            }
        });

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatNow = "PASS";
                //confirmRefusLayout.setVisibility(View.VISIBLE);
                loadDialogUpdatePassUpdate();
            }
        });

        btnConfirmUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whatNow.equals("NAME")) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).child("username").setValue(tv_username.getText().toString());
                    currentName = tv_username.getText().toString();
                    tv_username.setText(currentName);
                    tv_username.setEnabled(false);
                    confirmRefusLayout.setVisibility(View.GONE);
                } else if (whatNow.equals("PHONE")) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).child("telephone").setValue(tv_phone.getText().toString());
                    currentPhone = tv_phone.getText().toString();
                    tv_phone.setText(currentPhone);
                    tv_phone.setEnabled(false);
                    confirmRefusLayout.setVisibility(View.GONE);
                } else if (whatNow.equals("UNIVERSITY")) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).child("university").setValue(tv_university.getText().toString());
                    currentUniversity = tv_university.getText().toString();
                    tv_university.setText(currentUniversity);
                    tv_university.setEnabled(false);
                    confirmRefusLayout.setVisibility(View.GONE);
                } else if (whatNow.equals("MAIL")) {
                    loadDialogConfirmMailUpdate();
                } else if (whatNow.equals("PIC")) {
                    final ProgressDialog progressDialogUploading = new ProgressDialog(getActivity());
                    progressDialogUploading.setMessage("Uploading...");
                    progressDialogUploading.setCanceledOnTouchOutside(false);
                    progressDialogUploading.show();
                    final StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                    StorageReference filePath = mStorage.child("profilepics").child(currentName);
                    filePath.putFile(userPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference pathReference = mStorage.child("profilepics/" + currentName);
                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).child("userPicture").setValue(uri.toString());
                                    currentPic = uri.toString();
                                    confirmRefusLayout.setVisibility(View.GONE);
                                    progressDialogUploading.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });

        btnRefusUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whatNow.equals("NAME")) {
                    tv_username.setText(currentName);
                    tv_username.setEnabled(false);
                    confirmRefusLayout.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdateUniversity.setClickable(true);
                    btnUpdatePhone.setClickable(true);
                    user_profile_photo.setClickable(true);
                } else if (whatNow.equals("PHONE")) {
                    tv_phone.setText(currentPhone);
                    tv_phone.setEnabled(false);
                    confirmRefusLayout.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdateUniversity.setClickable(true);
                    btnUpdateProfileName.setClickable(true);
                    user_profile_photo.setClickable(true);
                } else if (whatNow.equals("UNIVERSITY")) {
                    tv_university.setText(currentUniversity);
                    tv_university.setEnabled(false);
                    confirmRefusLayout.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdateProfileName.setClickable(true);
                    btnUpdatePhone.setClickable(true);
                    user_profile_photo.setClickable(true);
                } else if (whatNow.equals("MAIL")) {
                    tv_email.setText(currentMail);
                    tv_email.setEnabled(false);
                    confirmRefusLayout.setVisibility(View.GONE);

                    btnUpdateUniversity.setClickable(true);
                    btnUpdateProfileName.setClickable(true);
                    btnUpdatePhone.setClickable(true);
                    user_profile_photo.setClickable(true);
                } else if (whatNow.equals("PIC")) {
//                    Picasso.with(getActivity()).load(currentPic).centerCrop()
//                            .resize(user_profile_photo.getMeasuredWidth(), user_profile_photo.getMeasuredHeight()).into(user_profile_photo);
                    confirmRefusLayout.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdateUniversity.setClickable(true);
                    btnUpdatePhone.setClickable(true);
                    btnUpdateProfileName.setClickable(true);
                }
            }
        });

        confirmRefusLayout.setVisibility(View.GONE);
        tv_username.setEnabled(false);
        tv_email.setEnabled(false);
        tv_university.setEnabled(false);
        tv_phone.setEnabled(false);




        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbref.removeEventListener(valueListener);
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvMainTitle = (TextView) getActivity().findViewById(R.id.tv_main_title);
        tvMainTitle.setText("Profile");


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {


            userPicUri = data.getData();

            Picasso.with(getActivity()).load(userPicUri.toString()).noPlaceholder().centerCrop().fit()
                    .into(user_profile_photo);

            whatNow = "PIC";
            confirmRefusLayout.setVisibility(View.VISIBLE);

            btnUpdateMail.setClickable(false);
            btnUpdateUniversity.setClickable(false);
            btnUpdatePhone.setClickable(false);
            btnUpdateProfileName.setClickable(false);

        }
    }

    void loadDialogConfirmMailUpdate() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_reauth);

        final EditText editMail = (EditText) dialog.findViewById(R.id.edit_reauth_mail);
        final EditText editPass = (EditText) dialog.findViewById(R.id.edit_reauth_pass);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel_reauth);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_confirm_reauth);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMail.getText().equals("") || editPass.getText().equals("")) {
                    Toast.makeText(getActivity(), "Please enter your login & password", Toast.LENGTH_SHORT).show();
                } else {
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    FirebaseUser userr = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(editMail.getText().toString(), editPass.getText().toString());
                    userr.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.updateEmail(tv_email.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                                mDatabase.child(firebaseAuth.getCurrentUser().getUid()).child("email").setValue(tv_email.getText().toString());
                                                currentMail = tv_email.getText().toString();
                                                tv_email.setText(currentMail);
                                                tv_email.setEnabled(false);
                                                confirmRefusLayout.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(), "E-mail updated successfully", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    });
                        }
                    });
                }
            }
        });

        dialog.show();
    }

    void loadDialogUpdatePassUpdate() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_update_pass);

        final EditText editPass = (EditText) dialog.findViewById(R.id.edit_update_pass);
        final EditText editPassConfirm = (EditText) dialog.findViewById(R.id.edit_update_pass_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel_update_pass);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok_update_pass);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editPass.getText().toString().equals("") || !editPass.getText().toString().equals(editPassConfirm.getText().toString())) {
                    Toast.makeText(getActivity(), "Please check password", Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                    loadDialogConfirmPassUpdate(editPass.getText().toString());
                }
            }
        });

        dialog.show();
    }

    void loadDialogConfirmPassUpdate(final String pass) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_reauth);

        final EditText editMail = (EditText) dialog.findViewById(R.id.edit_reauth_mail);
        final EditText editPass = (EditText) dialog.findViewById(R.id.edit_reauth_pass);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel_reauth);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_confirm_reauth);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMail.getText().equals("") || editPass.getText().equals("")) {
                    Toast.makeText(getActivity(), "Please enter your login & password", Toast.LENGTH_SHORT).show();
                } else {
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    FirebaseUser userr = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(editMail.getText().toString(), editPass.getText().toString());
                    userr.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.updatePassword(pass)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //succ
                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                                mDatabase.child(firebaseAuth.getCurrentUser().getUid()).child("password").setValue(pass);
                                                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }
                    });
                }
            }
        });

        dialog.show();
    }


}


