package com.jithvar.gambhirmudda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;
import com.jithvar.gambhirmudda.adapter.Tabs_Adapter;
import com.jithvar.gambhirmudda.webservice.WebServiceHandler;
import com.jithvar.gambhirmudda.webservice.WebServiceListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.jithvar.gambhirmudda.constant.Config.SITE_URL;

/**
 * Created by Arvindo Mondal on 4/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */

public class TabActivity extends DrawableActivity implements TabLayout.OnTabSelectedListener {
    private TabLayout tabLayout;
    private ViewPager view;
    private Tabs_Adapter adpter;
    private int noOfTabs;
    private ArrayList<String> strings;// = new ArrayList<>();
    private ArrayList<String> IDlist;// = new ArrayList<>();
    private ArrayList<String> tabNameList;// = new ArrayList<>();
    private static final String TAG = TabActivity.class.getName();
    // Define a title for your current page, shown in autocompletion UI
    private static final String TITLE = "Sample Article";
    private String articleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        view = (ViewPager)findViewById(R.id.viewPager);

        tabLayout = (TabLayout)findViewById(R.id.tabs_layout);

        strings = getIntent().getStringArrayListExtra("tabStringList");
        IDlist = getIntent().getStringArrayListExtra("tabIdList");

//        Log.e("kdsbfksd " , String.valueOf(strings.size()));
//        Log.e("873465f" , String.valueOf(IDlist.size()));

        showTabs();

//        tabLayout.addTab(tabLayout.newTab().setText("HOME"));
//        tabLayout.addTab(tabLayout.newTab().setText("Latest updates"));
//        tabLayout.addTab(tabLayout.newTab().setText("Novels"));
//        tabLayout.addTab(tabLayout.newTab().setText("Study material"));
//        tabLayout.addTab(tabLayout.newTab().setText("Biography"));
//
//        tabLayout.setOnTabSelectedListener(this);
//        adpter = new Tabs_Adapter(getSupportFragmentManager(), tabLayout.getTabCount(), strings.size());
//        view.setAdapter(adpter);
//        view.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


//        onNewIntent(getIntent());
    }

    private void showTabs(){
        tabNameList = new ArrayList<>();

        int i = 0;
        while(i < strings.size()){
            if(!strings.get(1).equals("")) {
                tabLayout.addTab(tabLayout.newTab().setText(strings.get(i)));
                tabNameList.add(strings.get(i));
            }
            i++;
        }


//        tabLayout.addTab(tabLayout.newTab().setText("HOME"));
//        tabLayout.addTab(tabLayout.newTab().setText("Latest updates"));
//        tabLayout.addTab(tabLayout.newTab().setText("Novels"));
//        tabLayout.addTab(tabLayout.newTab().setText("Study material"));
//        tabLayout.addTab(tabLayout.newTab().setText("Biography"));

        tabLayout.setOnTabSelectedListener(this);
        adpter = new Tabs_Adapter(getSupportFragmentManager(), tabLayout.getTabCount(), tabNameList,
                tabNameList.size());
        view.setAdapter(adpter);
        view.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        view.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

//    protected void onNewIntent(Intent intent) {
//        String action = intent.getAction();
//        Uri data = intent.getData();
//        if (Intent.ACTION_VIEW.equals(action) && data != null) {
//            articleId = data.getLastPathSegment();
////            TextView linkText = findViewById(R.id.link);
////            linkText.setText(data.toString());
//        }
//    }

    // [END handle_intent]

    // [START app_indexing_view]
    @Override
    public void onStart(){
        super.onStart();

//        if (articleId != null) {
//            final Uri BASE_URL = Uri.parse(SITE_URL);
//            final String APP_URI = BASE_URL.buildUpon().appendPath(articleId).build().toString();
//
//            Indexable articleToIndex = new Indexable.Builder()
//                    .setName(TITLE)
//                    .setUrl(APP_URI)
//                    .build();
//
//            Task<Void> task = FirebaseAppIndex.getInstance().update(articleToIndex);
//
//            // If the Task is already complete, a call to the listener will be immediately
//            // scheduled
//            task.addOnSuccessListener(TabActivity.this, new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "App Indexing API: Successfully added " + TITLE + " to index");
//                }
//            });
//
//            task.addOnFailureListener(TabActivity.this, new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Log.e(TAG, "App Indexing API: Failed to add " + TITLE + " to index. " + exception.getMessage());
//                }
//            });
//
//            // log the view action
//            Task<Void> actionTask = FirebaseUserActions.getInstance().start(Actions.newView(TITLE,
//                    APP_URI));
//
//            actionTask.addOnSuccessListener(TabActivity.this, new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "App Indexing API: Successfully started view action on " + TITLE);
//                }
//            });
//
//            actionTask.addOnFailureListener(TabActivity.this, new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Log.e(TAG, "App Indexing API: Failed to start view action on " + TITLE + ". "
//                            + exception.getMessage());
//                }
//            });
//        }
    }

    @Override
    public void onStop(){
        super.onStop();

//        if (articleId != null) {
//            final Uri BASE_URL = Uri.parse(SITE_URL);
//            final String APP_URI = BASE_URL.buildUpon().appendPath(articleId).build().toString();
//
//            Task<Void> actionTask = FirebaseUserActions.getInstance().end(Actions.newView(TITLE,
//                    APP_URI));
//
//            actionTask.addOnSuccessListener(TabActivity.this, new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "App Indexing API: Successfully ended view action on " + TITLE);
//                }
//            });
//
//            actionTask.addOnFailureListener(TabActivity.this, new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Log.e(TAG, "App Indexing API: Failed to end view action on " + TITLE + ". "
//                            + exception.getMessage());
//                }
//            });
//        }
    }
}
