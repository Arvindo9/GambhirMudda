package com.jithvar.gambhirmudda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;
import com.jithvar.gambhirmudda.webservice.WebServiceHandler;
import com.jithvar.gambhirmudda.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import static com.jithvar.gambhirmudda.constant.Config.SITE_URL;

public class StartActivity extends AppCompatActivity {

//    private ProgressBar progressBar;
    private final ArrayList<String> strings = new ArrayList<>();
    private final ArrayList<String>IDlist = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private static final String TAG = StartActivity.class.getName();
    // Define a title for your current page, shown in autocompletion UI
    private static final String TITLE = "Sample Article";
    private String articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

//        progressDialog = new ProgressDialog(StartActivity.this, AlertDialog.THEME_HOLO_LIGHT);
//        progressDialog.setMessage("Loading...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();


        onNewIntent(getIntent());
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        try {
//            loadsTabs();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    /*
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                Intent i = new Intent(StartActivity.this, TabActivity.class);
                i.putExtra("tabStringList", strings);
                i.putExtra("tabIdList", IDlist);
                startActivity(i);
                break;

            case R.id.registration:
                break;
        }
    }
    */

    private void loadActivity(){
        Intent i = new Intent(StartActivity.this, TabActivity.class);
        i.putExtra("tabStringList", strings);
        i.putExtra("tabIdList", IDlist);
        startActivity(i);
        finish();
    }

    private void loadsTabs() throws ExecutionException, InterruptedException {
        WebServiceHandler serviceHandler = new WebServiceHandler(StartActivity.this);

        serviceHandler.webServiceListener = new WebServiceListener() {
            @Override
            public void onDataReceived(String response) {
                Log.e("Web Responce", response);
                if (response != null) {
                    try {
//                        IDlist.add("0");
//                        strings.add("HOME");

                        JSONArray jsnarray= new JSONArray(response);
                        for(int i=0;i<jsnarray.length();i++){
                            JSONObject jsnobj=jsnarray.getJSONObject(i);
                            Iterator<String> keys=jsnobj.keys();

                            while (keys.hasNext()) {
                                String name=keys.next();
                                IDlist.add(name);
                                strings.add(jsnobj.getString(name));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    progressDialog.dismiss();
                    loadActivity();
                }
            }
        };

        serviceHandler.tabsLoads();
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            articleId = data.getLastPathSegment();
//            TextView linkText = findViewById(R.id.link);
//            linkText.setText(data.toString());
        }
    }

    // [END handle_intent]

    // [START app_indexing_view]
    @Override
    public void onStart(){
        super.onStart();


        try {
            loadsTabs();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (articleId != null) {
            final Uri BASE_URL = Uri.parse(SITE_URL);
            final String APP_URI = BASE_URL.buildUpon().appendPath(articleId).build().toString();

            Indexable articleToIndex = new Indexable.Builder()
                    .setName(TITLE)
                    .setUrl(APP_URI)
                    .build();

            Task<Void> task = FirebaseAppIndex.getInstance().update(articleToIndex);

            // If the Task is already complete, a call to the listener will be immediately
            // scheduled
            task.addOnSuccessListener(StartActivity.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "App Indexing API: Successfully added " + TITLE + " to index");
                }
            });

            task.addOnFailureListener(StartActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "App Indexing API: Failed to add " + TITLE + " to index. " + exception.getMessage());
                }
            });

            // log the view action
            Task<Void> actionTask = FirebaseUserActions.getInstance().start(Actions.newView(TITLE,
                    APP_URI));

            actionTask.addOnSuccessListener(StartActivity.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "App Indexing API: Successfully started view action on " + TITLE);
                }
            });

            actionTask.addOnFailureListener(StartActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "App Indexing API: Failed to start view action on " + TITLE + ". "
                            + exception.getMessage());
                }
            });
        }
    }

    @Override
    public void onStop(){
        super.onStop();

        if (articleId != null) {
            final Uri BASE_URL = Uri.parse(SITE_URL);
            final String APP_URI = BASE_URL.buildUpon().appendPath(articleId).build().toString();

            Task<Void> actionTask = FirebaseUserActions.getInstance().end(Actions.newView(TITLE,
                    APP_URI));

            actionTask.addOnSuccessListener(StartActivity.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "App Indexing API: Successfully ended view action on " + TITLE);
                }
            });

            actionTask.addOnFailureListener(StartActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "App Indexing API: Failed to end view action on " + TITLE + ". "
                            + exception.getMessage());
                }
            });
        }
    }
}
