package org.peenyaindustries.piaconnect.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.activity.About.InviteeMembers;
import org.peenyaindustries.piaconnect.activity.About.ManagingCouncilMembers;
import org.peenyaindustries.piaconnect.activity.About.OfficeBearers;
import org.peenyaindustries.piaconnect.activity.About.PanelList;
import org.peenyaindustries.piaconnect.activity.About.PastPresidents;
import org.peenyaindustries.piaconnect.activity.About.PeenyaIndustriesAssociation;
import org.peenyaindustries.piaconnect.adapters.SnapAdapter;
import org.peenyaindustries.piaconnect.app.Config;
import org.peenyaindustries.piaconnect.callback.VolleyResponseCallback;
import org.peenyaindustries.piaconnect.helper.ConnectionDetector;
import org.peenyaindustries.piaconnect.helper.DatabaseHelper;
import org.peenyaindustries.piaconnect.helper.NotificationUtils;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.json.FetchData;
import org.peenyaindustries.piaconnect.json.ParseData;
import org.peenyaindustries.piaconnect.models.Category;
import org.peenyaindustries.piaconnect.models.Post;
import org.peenyaindustries.piaconnect.models.Snap;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private RecyclerView baseView;
    private PrefManager pref;
    private ProgressDialog pDialog;
    private FetchData f;
    private ParseData p;
    private DatabaseHelper db;
    private List<Post> posts;
    private List<Category> cats;
    private long currentCount = 0;
    private String id = null;

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    id = intent.getStringExtra("message");
                }
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("PIA Connect");

        pref = new PrefManager(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        f = new FetchData(this);
        p = new ParseData(this);
        db = new DatabaseHelper(this);
        posts = new ArrayList<>();
        cats = new ArrayList<>();

        //Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        baseView = (RecyclerView) findViewById(R.id.baseView);
        baseView.setLayoutManager(new LinearLayoutManager(this));
        baseView.setHasFixedSize(true);
        baseView.setNestedScrollingEnabled(false);

        if (isConnected()) {
            if (db.getPostCount() == 0) {
                downloadData();
            } else {
                checkDatabase();
            }
        } else {
            Toast.makeText(this, "Check your Network Connection", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isConnected() {
        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        Boolean isInternetActive = connection.isConnectedToInternet();
        if (isInternetActive) {
            return true;
        } else {
            return false;
        }
    }

    private void setupAdapter() {

        SnapAdapter snapAdapter = new SnapAdapter(this);

        snapAdapter.addSnap(new Snap(Gravity.CENTER, "", db.getAllPosts()));
        List<Category> cats = db.getAllCategories();
        for (int i = 0; i < cats.size(); i++) {
            Category cat = cats.get(i);
            snapAdapter.addSnap(new Snap(Gravity.START, cat.getTitle(), db.getAllPostsByCategory(cat.getTitle())));
        }

        db.closeDB();

        baseView.setAdapter(snapAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if (isConnected()) {
                if (db.getPostCount() == 0) {
                    downloadData();
                } else {
                    checkDatabase();
                }
            } else {
                Toast.makeText(this, "Check your Network Connection", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            startActivity(new Intent(this, NewsActivity.class));
        } else if (id == R.id.nav_industry) {

        } else if (id == R.id.nav_events) {

        } else if (id == R.id.nav_jobs) {

        } else if (id == R.id.nav_pia) {
            startActivity(new Intent(this, PeenyaIndustriesAssociation.class));
        } else if (id == R.id.nav_office_bearers) {
            startActivity(new Intent(this, OfficeBearers.class));
        } else if (id == R.id.nav_managing_council) {
            startActivity(new Intent(this, ManagingCouncilMembers.class));
        } else if (id == R.id.nav_panel_list) {
            startActivity(new Intent(this, PanelList.class));
        } else if (id == R.id.nav_invitee_members) {
            startActivity(new Intent(this, InviteeMembers.class));
        } else if (id == R.id.nav_past_presidents) {
            startActivity(new Intent(this, PastPresidents.class));
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=org.peenyaindustries.piaconnect");
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent, "Share via");
            startActivity(sendIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void downloadData() {

        showpDialog();

        f.fetchData(new VolleyResponseCallback() {
            @Override
            public void onSuccess(String response) {
                cats = p.parseCategories(response);

                for (int i = 0; i < cats.size(); i++) {

                    storeCategories(cats.get(i));
                }
            }
        }, Config.URL_CATEGORIES);

        f.fetchData(new VolleyResponseCallback() {
            @Override
            public void onSuccess(String response) {

                posts = p.parsePosts(response);

                for (int i = 0; i < posts.size(); i++) {

                    storePosts(posts.get(i));
                }

                hidepDialog();
                setupAdapter();
            }
        }, Config.URL_POST_ALL);

    }

    public void checkDatabase() {
        //Start Progress Dialog
        showpDialog();

        //get current posts count in SQLite DB
        final long count = db.getPostCount();

        //get current posts count in web server
        f.fetchData(new VolleyResponseCallback() {
            @Override
            public void onSuccess(String response) {
                List<Post> posts = p.parseTotalPosts(response);

                currentCount = posts.get(0).getTotalPost();

                if (count < currentCount) {
                    //if posts are added in web server fetch them and insert into SQLite
                    long difference = currentCount - count;
                    fetchRecentPosts(difference);

                } else if (count > currentCount) {
                    //if posts are removed from web server reflect the changes in SQLite DB
                    db.deleteAllContent();
                    hidepDialog();
                    downloadData();
                } else {
                    //if no changes fetch the data from SQLite and display
                    hidepDialog();
                    setupAdapter();
                }
            }
        }, Config.URL_POST);
    }

    public void storeCategories(Category category) {

        long count = db.getCategoryCount();

        if (count > 0) {
            db.deleteCategory(category, false);
        }
        db.insertCategory(category);

    }

    public void storePosts(Post post) {
        db.insertPost(post, post.getCategories());
    }

    private void fetchRecentPosts(long currentCount) {

        f.fetchData(new VolleyResponseCallback() {
            @Override
            public void onSuccess(String response) {
                cats = p.parseCategories(response);

                for (int i = 0; i < cats.size(); i++) {

                    storeCategories(cats.get(i));
                }
            }
        }, Config.URL_CATEGORIES);
        f.fetchData(new VolleyResponseCallback() {
            @Override
            public void onSuccess(String response) {
                List<Post> posts = p.parsePosts(response);

                for (int i = 0; i < posts.size(); i++) {
                    storePosts(posts.get(i));
                }
                hidepDialog();
                setupAdapter();
            }
        }, Config.URL_POST_RECENT + currentCount);

    }

    /**
     * Progress Dialog
     */
    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.setTitle("Please wait while we fetch the data..");
            pDialog.setMessage("This might take a while depending upon the internet strength...");
            pDialog.show();
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Back Button Logic
     * Should tap twice to exit
     */
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
