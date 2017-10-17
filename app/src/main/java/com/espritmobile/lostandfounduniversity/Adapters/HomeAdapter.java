package com.espritmobile.lostandfounduniversity.Adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.espritmobile.lostandfounduniversity.AddPostActivity;
import com.espritmobile.lostandfounduniversity.ChatActivity;
import com.espritmobile.lostandfounduniversity.DetailsActivity;
import com.espritmobile.lostandfounduniversity.EditPostActivity;
import com.espritmobile.lostandfounduniversity.Fragments.MyPostsFragment;
import com.espritmobile.lostandfounduniversity.Models.Post;
import com.espritmobile.lostandfounduniversity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Noor on 06/12/2016.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomViewHolder> {

    private ArrayList<Post> posts;
    private Context context;
    private String username, userpic, telephone;



    public static final String DETAIL_EXTRA = "detailExtra";

    public HomeAdapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_home, null);


        final CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int position) {
        final Post post = posts.get(position);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(post.getUserId());

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue().toString();
                userpic = dataSnapshot.child("userPicture").getValue().toString();


                //Setting text view title

                customViewHolder.imgAnnoncePicture.setDrawingCacheEnabled(true);
                customViewHolder.imgAnnoncePicture.buildDrawingCache();

                customViewHolder.tvAnnonceUserName.setText(username);
                customViewHolder.tvAnnonceType.setText(post.getType());
                customViewHolder.tvAnnonceTime.setText(post.getTimePost());
                customViewHolder.tvAnnonceDescription.setText(post.getDescription());
                if (!post.getPostPicture().equals("")) {
                    Picasso.with(context).load(post.getPostPicture()).noPlaceholder().centerCrop().fit().into(
                            customViewHolder.imgAnnoncePicture
                    );
                }
                if(!userpic.equals("")) {
                    Picasso.with(context).load(userpic).noPlaceholder().centerCrop().fit().into(
                            customViewHolder.imgUser
                    );
                }

                customViewHolder.tvAnnonceContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.contact)
                                .setItems(R.array.contact, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item

                                        telephone = dataSnapshot.child("telephone").getValue().toString();

                                        switch (which) {
                                            case 0:
                                                Intent i1 = new Intent(context, ChatActivity.class);
                                                i1.putExtra("iduser", post.getUserId());
                                                context.startActivity(i1);
                                                break;
                                            case 1:
                                                if(telephone.equals("undefined")){
                                                    Toast.makeText(context, "User phone number not available", Toast.LENGTH_LONG).show();
                                                }else {
                                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                    callIntent.setData(Uri.parse("tel:" + telephone));
                                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                        return;

                                                    }
                                                    context.startActivity(callIntent);
                                                }

                                                break;
                                            case 2 :

                                                if(telephone.equals("undefined")){
                                                    Toast.makeText(context, "User phone number not available", Toast.LENGTH_LONG).show();
                                                }else {

                                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                                    smsIntent.setData(Uri.parse("mms-sms:"));
                                                    smsIntent.setType("vnd.android-dir/mms-sms");
                                                    smsIntent.putExtra("address", telephone);

                                                    context.startActivity(smsIntent);
                                                }
                                                break;

                                        }
                                    }
                                });

                        builder.show();

                    }
                });
                customViewHolder.tvAnnonceDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(DETAIL_EXTRA, post);
                        context.startActivity(intent);
                    }
                });

                customViewHolder.tvAnnonceReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("If you find that this post contains inappropriate contents you can report it ").setTitle("Warning")
                                    .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                    "mailto","lostandfounduniversity@gmail.com", null));
                                            context.startActivity(Intent.createChooser(intent, "Choose an Email client :"));




                                        }
                                    })
                                    .setNegativeButton("Hide", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            customViewHolder.rlRow.setVisibility(View.GONE);

                                        }
                                    });
                            builder.show();

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

    @Override
    public int getItemCount() {
        return (null != posts ? posts.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imgUser;
        protected ImageView imgAnnoncePicture;
        protected TextView tvAnnonceUserName;
        protected TextView tvAnnonceType;
        protected TextView tvAnnonceTime;
        protected TextView tvAnnonceDescription;
        protected TextView tvAnnonceContact;
        protected TextView tvAnnonceDetail;
        protected TextView tvAnnonceReport;
        protected RelativeLayout rlRow;
        public CustomViewHolder(View view) {
            super(view);
            this.imgUser = (ImageView) view.findViewById(R.id.img_annonce_user);
            this.imgAnnoncePicture = (ImageView) view.findViewById(R.id.img_annonce_picture);
            this.tvAnnonceUserName = (TextView) view.findViewById(R.id.tv_annonce_user_name);
            this.tvAnnonceType = (TextView) view.findViewById(R.id.tv_annonce_type);
            this.tvAnnonceTime = (TextView) view.findViewById(R.id.tv_annonce_time);
            this.tvAnnonceDescription = (TextView) view.findViewById(R.id.tv_annonce_description);
            this.tvAnnonceContact = (TextView) view.findViewById(R.id.tv_annonce_contact);
            this.tvAnnonceDetail = (TextView) view.findViewById(R.id.tv_annonce_detail);
            this.tvAnnonceReport = (TextView) view.findViewById(R.id.tv_annonce_report);
            this.rlRow = (RelativeLayout) view.findViewById(R.id.rl_row);
        }
    }
}
