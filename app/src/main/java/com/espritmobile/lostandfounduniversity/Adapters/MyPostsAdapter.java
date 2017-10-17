package com.espritmobile.lostandfounduniversity.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by Gadour on 30/12/2016.
 */

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.CustomViewHolder> {
    private ArrayList<Post> posts;
    private Context context;
    private String username, userpic;
    public static final String POST_EXTRA = "postExtra";
    public static final String POST_POSITION = "postPostion";

    public MyPostsAdapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
        this.context = context;
    }

    @Override
    public MyPostsAdapter.CustomViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_my_posts, null);


        final MyPostsAdapter.CustomViewHolder viewHolder = new MyPostsAdapter.CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyPostsAdapter.CustomViewHolder customViewHolder, final int position) {
        final Post post = posts.get(position);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(post.getUserId());

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue().toString();
                userpic = dataSnapshot.child("userPicture").getValue().toString();

                //Setting text view title

                customViewHolder.imgAnnoncePicture.setDrawingCacheEnabled(true);
                customViewHolder.imgAnnoncePicture.buildDrawingCache();

                customViewHolder.tvAnnonceUserName.setText(username);
                customViewHolder.tvAnnonceType.setText(post.getType());
                customViewHolder.tvAnnonceTime.setText(post.getTimePost());
                customViewHolder.tvAnnonceDescription.setText(post.getDescription());
                if(! post.getPostPicture().equals("")) {
                    Picasso.with(context).load(post.getPostPicture()).noPlaceholder().centerCrop().fit().into(
                            customViewHolder.imgAnnoncePicture
                    );
                }
                if(! userpic.equals("")) {
                    Picasso.with(context).load(userpic).noPlaceholder().centerCrop().fit().into(
                            customViewHolder.imgUser
                    );
                }

                customViewHolder.tvAnnonceEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EditPostActivity.class);
                        intent.putExtra(POST_EXTRA, post);
                        intent.putExtra(POST_POSITION,position);
                        context.startActivity(intent);

                    }
                });
                customViewHolder.tvAnnonceDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        if (post.getType().equals("Lost")) {
                            builder.setMessage("Delete this post?").setTitle("Warning")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                                            if(!post.getPostPicture().equals("")) {
                                                StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(post.getPostPicture());
                                                httpsReference.delete();
                                                dbRef.child("posts").child(MyPostsFragment.KEYS.get(position)).removeValue();
                                            }else{
                                                dbRef.child("posts").child(MyPostsFragment.KEYS.get(position)).removeValue();
                                            }




                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                            builder.show();

                        } else if (post.getType().equals("Found")) {
                            builder.setTitle("Important");
                            builder.setMessage("In order to ensure reliability of our application and improve your experience, you cannot delete a post of type \"Found\".");
                            builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            builder.show();
                        }
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
        protected TextView tvAnnonceEdit;
        protected TextView tvAnnonceDelete;

        public CustomViewHolder(View view) {
            super(view);
            this.imgUser = (ImageView) view.findViewById(R.id.img_annonce_user);
            this.imgAnnoncePicture = (ImageView) view.findViewById(R.id.img_annonce_picture);
            this.tvAnnonceUserName = (TextView) view.findViewById(R.id.tv_annonce_user_name);
            this.tvAnnonceType = (TextView) view.findViewById(R.id.tv_annonce_type);
            this.tvAnnonceTime = (TextView) view.findViewById(R.id.tv_annonce_time);
            this.tvAnnonceDescription = (TextView) view.findViewById(R.id.tv_annonce_description);
            this.tvAnnonceEdit = (TextView) view.findViewById(R.id.tv_annonce_edit);
            this.tvAnnonceDelete = (TextView) view.findViewById(R.id.tv_annonce_delete);
        }
    }
}
