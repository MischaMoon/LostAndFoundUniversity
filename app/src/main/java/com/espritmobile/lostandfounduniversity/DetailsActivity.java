package com.espritmobile.lostandfounduniversity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.espritmobile.lostandfounduniversity.Adapters.HomeAdapter;
import com.espritmobile.lostandfounduniversity.Adapters.MyPostsAdapter;
import com.espritmobile.lostandfounduniversity.Models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvObject,tvType,tvUniversity,tvPlace,tvTimeOfPost,tvTimeOfEvent,tvUser,tvTel,tvDescription;
    private ImageView ivPostDetailPicture;
    private LinearLayout llDetailBg;
    private Post post;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvObject = (TextView) findViewById(R.id.tv_post_detail_object);
        tvType = (TextView) findViewById(R.id.tv_post_detail_type);
        tvUniversity = (TextView) findViewById(R.id.tv_post_detail_university);
        tvPlace = (TextView) findViewById(R.id.tv_post_detail_place);
        tvTimeOfPost = (TextView) findViewById(R.id.tv_post_detail_timeofpost);
        tvTimeOfEvent = (TextView) findViewById(R.id.tv_post_detail_timeofevent);
        tvUser = (TextView) findViewById(R.id.tv_post_detail_user);
        tvTel = (TextView) findViewById(R.id.tv_post_detail_tel);
        tvDescription = (TextView) findViewById(R.id.tv_post_detail_desc);
        ivPostDetailPicture = (ImageView) findViewById(R.id.iv_post_detail_picture);

        Bundle bundle = getIntent().getExtras();
        post = bundle.getParcelable(HomeAdapter.DETAIL_EXTRA);

        tvObject.setText(post.getObject());
        tvType.setText(post.getType());
        tvUniversity.setText(post.getPlacePost());
        tvPlace.setText(post.getPlace());
        tvTimeOfPost.setText(post.getTimePost());
        tvTimeOfEvent.setText(post.getDate()+" at "+post.getTime());
        tvDescription.setText(post.getDescription());


        ivPostDetailPicture.setDrawingCacheEnabled(true);
        ivPostDetailPicture.buildDrawingCache();
        if(!post.getPostPicture().equals("")) {
            Picasso.with(this).load(post.getPostPicture()).noPlaceholder().centerCrop().fit().into(
                    ivPostDetailPicture
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbRef.child("users").child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvUser.setText(dataSnapshot.child("username").getValue().toString());
                tvTel.setText(dataSnapshot.child("telephone").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
