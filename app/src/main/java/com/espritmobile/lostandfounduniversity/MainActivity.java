package com.espritmobile.lostandfounduniversity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.espritmobile.lostandfounduniversity.Fragments.AboutFragment;
import com.espritmobile.lostandfounduniversity.Fragments.FilterFragment;
import com.espritmobile.lostandfounduniversity.Fragments.HomeFragment;
import com.espritmobile.lostandfounduniversity.Fragments.MyPostsFragment;
import com.espritmobile.lostandfounduniversity.Fragments.ProfileFragment;
import com.espritmobile.lostandfounduniversity.Fragments.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemMyPosts;
    private ResideMenuItem itemSearch;
    private ResideMenuItem itemAbout;

    ValueEventListener valueListener = null;


    boolean doubleBackToExitPressedOnce = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            getWindow().setStatusBarColor(getResources().getColor(R.color.colorEspritPlus));
        }


        setContentView(R.layout.activity_main);
        setUpMenu();

        if (savedInstanceState == null) {
            changeFragment(new HomeFragment());
        }


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                }

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void setUpMenu() {
        //attach menu to current activity
        resideMenu = new ResideMenu(this);

        resideMenu.setBackground(R.drawable.whitebk);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);


        //setting width of the menu using scale factor between 0.1 f to 1.0 f
        resideMenu.setScaleValue(0.5f);

        itemHome = new ResideMenuItem(this, R.drawable.home, "Home");
        itemMyPosts = new ResideMenuItem(this, R.drawable.myposts, "My posts");
        itemSearch = new ResideMenuItem(this, R.drawable.search, "Search");
        itemProfile = new ResideMenuItem(this, R.drawable.profile_user, "Profile");
        itemAbout = new ResideMenuItem(this, R.drawable.info, "About us");


        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemMyPosts.setOnClickListener(this);
        itemSearch.setOnClickListener(this);
        itemAbout.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemMyPosts, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSearch, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);


        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {

        }

        @Override
        public void closeMenu() {

        }
    };

    @Override
    public void onClick(View v) {

        if (v == itemHome) {
            changeFragment(new HomeFragment());
        } else if (v == itemProfile) {
            changeFragment(new ProfileFragment());
        } else if (v == itemMyPosts) {
            changeFragment(new MyPostsFragment());
        } else if (v == itemSearch) {
            changeFragment(new SearchFragment());
        } else if (v == itemAbout) {
            changeFragment(new AboutFragment());
        }

        resideMenu.closeMenu();
    }


    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .commit();


    }

    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (currentFragment instanceof HomeFragment) {
            if (doubleBackToExitPressedOnce) {

                moveTaskToBack(true);
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else if (currentFragment instanceof MyPostsFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment()).commit();
        } else if (currentFragment instanceof SearchFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment()).commit();
        } else if (currentFragment instanceof FilterFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new SearchFragment()).commit();
        } else if (currentFragment instanceof ProfileFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment()).commit();
        }else if (currentFragment instanceof AboutFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment()).commit();
        }
    }
}