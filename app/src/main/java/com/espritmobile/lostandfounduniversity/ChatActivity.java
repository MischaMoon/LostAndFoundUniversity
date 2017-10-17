package com.espritmobile.lostandfounduniversity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.espritmobile.lostandfounduniversity.Adapters.ChatAdapter;
import com.espritmobile.lostandfounduniversity.Models.Message;
import com.espritmobile.lostandfounduniversity.Models.User;
import com.espritmobile.lostandfounduniversity.Utils.psuhNotificationAllUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private EditText etMessage;
    private ImageButton sendButton;
    private RecyclerView recycler;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ChildEventListener childEventListener;

    private SharedPreferences prefs;
    private String username;
    private String userId;
    String iduser;
    private ChatAdapter adapter;
    private DatabaseReference mDatabaseref;


    private static final String TAG = "TCHAT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mDatabaseref = FirebaseDatabase.getInstance().getReference();

        //Initialisation de la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Intent intent = getIntent();
        iduser = intent.getStringExtra("iduser");

        setSupportActionBar(toolbar);

        //Initialisation des vues
        initViews();
        initFirebase();

        prefs = getSharedPreferences("tchat", MODE_PRIVATE);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    attachChildListener();
                    username = prefs.getString("PSEUDO", null);
                    userId = user.getUid();
                    adapter.setUser(user);

                }else{
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void attachChildListener() {
        if(childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.w(TAG, "onChildAdded: ");
                    Message message = dataSnapshot.getValue(Message.class);
                    message.setUid(dataSnapshot.getKey());
                    adapter.addMessage(message);
                    recycler.scrollToPosition(adapter.getItemCount() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Message message = dataSnapshot.getValue(Message.class);
                    message.setUid(dataSnapshot.getKey());
                    adapter.deleteMessage(message);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mRef.child(Constants.MESSAGES_DB).child(MainActivity.userId).child(iduser).addChildEventListener(childEventListener);
        }
    }

    private void detachChildListener(){
        if(childEventListener != null){
            mRef.child(Constants.MESSAGES_DB).child(MainActivity.userId).child(iduser).removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
    private void initViews(){
        etMessage = (EditText) findViewById(R.id.etMessage);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recycler.setLayoutManager(manager);
        ArrayList<Message> messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        recycler.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_DONE){
                    sendMessage();
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }

                return false;
            }
        });
    }

    private void sendMessage() {
        final String content = etMessage.getText().toString();
        if(!TextUtils.isEmpty(content)){
            Message message = new Message(username, userId, content, null);
            //(MainActivity.userId currentusers
            // iduser to send
            mRef.child(Constants.MESSAGES_DB).child(MainActivity.userId).child(iduser).push().setValue(message);
            mRef.child(Constants.MESSAGES_DB).child(iduser).child(MainActivity.userId).push().setValue(message);

            /*************************CurrentUser****************************/
            final User currentUser = new User();
            mDatabaseref.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    currentUser.setEmail(dataSnapshot.child("email").getValue().toString()) ;
                    currentUser.setTelephone(dataSnapshot.child("telephone").getValue().toString());
                    currentUser.setPassword(dataSnapshot.child("password").getValue().toString());
                    currentUser.setUniversity(dataSnapshot.child("university").getValue().toString());
                    currentUser.setUserPicture(dataSnapshot.child("userPicture").getValue().toString());
                    currentUser.setUsername(dataSnapshot.child("username").getValue().toString());
                    currentUser.setUserId(userId);
                    currentUser.setToken(dataSnapshot.child("token").getValue().toString());
                    /*************************toSend****************************/
                    final User userToSend = new User();
                    mDatabaseref.child("users").child(iduser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            userToSend.setEmail(dataSnapshot.child("email").getValue().toString()) ;
                            userToSend.setTelephone(dataSnapshot.child("telephone").getValue().toString());
                            userToSend.setPassword(dataSnapshot.child("password").getValue().toString());
                            userToSend.setUniversity(dataSnapshot.child("university").getValue().toString());
                            userToSend.setUserPicture(dataSnapshot.child("userPicture").getValue().toString());
                            userToSend.setUsername(dataSnapshot.child("username").getValue().toString());
                            userToSend.setUserId(iduser);
                            userToSend.setToken(dataSnapshot.child("token").getValue().toString());

                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    psuhNotificationAllUsers.sendAndroidNotification(userToSend.getToken(),"From  " +currentUser.getUsername() ,Constants.MESSAGES_DB);
                                    return null;
                                }
                            }.execute();



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    /************************************************************/


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            /************************************************************/


            etMessage.setText("");
        }
    }

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null){
            mAuth.removeAuthStateListener(authStateListener);
        }
        detachChildListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(authStateListener);
    }
}
