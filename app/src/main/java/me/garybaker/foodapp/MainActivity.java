package me.garybaker.foodapp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.design.widget.Snackbar.make;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static me.garybaker.foodapp.R.color;
import static me.garybaker.foodapp.R.id;
import static me.garybaker.foodapp.R.string;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static String EXTRA_POST_KEY = "main";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference ref = database.getReference();
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mPostReference;
    private DatabaseReference displayName;


    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;

    public View headerView;

    private TextView navUserDisplayName;

    // [START declare_database_ref]
    public DatabaseReference mDatabase;
    // [END declare_database_ref]


    private String mAuthorView;

    public Toast toast;
    private boolean mStopThread;


    public ViewGroup layout;

    private Snackbar snackbar;

//    public static final CustomSnackbar;

//    public static final CustomSnackbar customSnackbar;

    /**
     *
     * @param savedInstanceState
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(color.colorPrimaryDark));


        setContentView(R.layout.activity_main);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        displayName = ref.child("users").child(mAuth.getCurrentUser().getUid());

        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Database
        mPostReference = ref.child("posts").child("-Ld8QJN5-xmJniNi3_IG");

        mAuth.signInWithEmailAndPassword("garybakerdev@gmail.com", "Gary0704051").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    makeText(MainActivity.this, "Authentication Success.", LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    makeText(MainActivity.this, "Authentication failed.", LENGTH_SHORT).show();
                }
            }
        });


        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RecentPostsFragment(),
                    new MyPostsFragment(),
                    new MyTopPostsFragment(),
            };
            private final String[] mFragmentNames = new String[]{
                    "Yesterday",
                    "Today",
                    "Tomorrow"
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(id.container);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setCurrentItem(1);


        TabLayout tabLayout = findViewById(id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


//        final Toast toast = new Toast(getApplicationContext());
        final FloatingActionButton fab = findViewById(id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // create instance
                Toast toast = new Toast(getApplicationContext());

// inflate custom view
                View viewInflated = getLayoutInflater().inflate(R.layout.activity_new_post, null);

// set custom view
                toast.setView(viewInflated);

// set duration
                toast.setDuration(Toast.LENGTH_LONG);

// set position
//                int margin = getResources().getDimensionPixelSize(R.dimen.toast_vertical_margin);
//                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, margin);

// show toast
                toast.show();


            }
        });

        FloatingActionButton closeFab;
        closeFab = findViewById(id.fabNewPost);

        closeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast.cancel();

                fab.show();
            }
        });

        DrawerLayout drawer = findViewById(id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, string.navigation_drawer_open, string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);

        TextView navEmail = headerView.findViewById(id.userDisplayEmail);
        navEmail.setText(mAuth.getCurrentUser().getEmail());

        navUserDisplayName = headerView.findViewById(id.userDisplayName);
    }


    /**
     *
     * public void onBackPressed()
     *
     * this functions' sole purpose is to handle closing the DrawerLayout View at drawer_layout
     * it receives a click event to then which it assess's weather the View is open or closed
     * and then closes the drawer when the condition's are met and fully satisfied
     *
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showSnackbar(ViewGroup view, String message, int duration)
    {
        make(view, message, duration).show();
    }



    /**
     *
     * @param menu
     * @return
     *
     *  onCreateOptionsMenu()
     *  Inflate the menu; this adds items to the action bar if it is present.
     *
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }



    public void openDialog() {
        final Dialog dialog = new Dialog(this.getApplicationContext()); // Context, this, etc.
        dialog.setContentView(R.layout.activity_new_post);
//            dialog.setTitle(R.string.dialog_title);
        dialog.show();
    }

    /**
     *
     * @param item
     * @return
     *
     * onOptionsItemSelected()
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     *
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.logout) {
            mAuth.signOut();

            Intent logout = new Intent(this, SignInActivity.class);
            startActivity(logout);
        }

        if (id == R.id.createPost) {
            Intent createPost = new Intent(this, NewPost.class);
            startActivity(createPost);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * onStart()
     * Get Post object and use the values to update the UI
     * Getting Post failed, log a message
     */

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        mPostReference.addValueEventListener(postListener);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue(User.class).getUsername();

                navUserDisplayName.setText(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        displayName.addValueEventListener(userListener);

    }


    /**
     *
     * @param item
     * @return
     *
     * well this i really don't logically understand what is going on here at this point just yet
     * in the development phase seems to be something regarding the navigation menu that maybe
     * corresponds to the R.layout.nav_header_main.xml if i am correct..?
     */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}