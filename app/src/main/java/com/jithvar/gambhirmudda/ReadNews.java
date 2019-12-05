package com.jithvar.gambhirmudda;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jithvar.gambhirmudda.adapter.CommentAdapter;
import com.jithvar.gambhirmudda.adapter.CommentAdapterTmp;
import com.jithvar.gambhirmudda.adapter.RelatedAdapter;
import com.jithvar.gambhirmudda.constant.Config;
import com.jithvar.gambhirmudda.dialog.CommentDialog;
import com.jithvar.gambhirmudda.handler.Comments;
import com.jithvar.gambhirmudda.handler.RelatedData;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import android.support.v4.app.Fragment;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.jithvar.gambhirmudda.constant.Config.LIKE_POST;
import static com.jithvar.gambhirmudda.constant.Config.LOAD_COMMENTS;
import static com.jithvar.gambhirmudda.constant.Config.LOAD_DATA;
import static com.jithvar.gambhirmudda.constant.Config.OPEN_NEWS_WEB;
import static com.jithvar.gambhirmudda.constant.Config.POST_COMMENTS;
import static com.jithvar.gambhirmudda.constant.Config.SITE_URL;
import static com.jithvar.gambhirmudda.constant.Config.VIEW_POST;

/**
 * Created by Arvindo Mondal on 27/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class ReadNews extends AppCompatActivity implements View.OnClickListener,
        CommentDialog.UserNameListener{


    private LinearLayout layout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Comments> commentsList;

    private TextView noComment;
//    private TextView msgBodyP1Tv;
    private TextView msgBodyP2Tv;
    private TextView msgSubjectTv;
    private TextView dateTv;
    private TextView timeTv;
    private TextView authorTv;
    private ImageView msgBodyImageIv;
    private TextView like_tv;
    private TextView view_tv;
    private TextView comment_tv;
    private ImageView likeIV;
    private WebView webViewMsg;
    private String primaryIdTo;
    private String categoryIdTo;

    private String PostId, PageNO, Category, Title,  NewsBody, FeaturedImage, Tags, Views;
    private String Likes, Author, Status, date, time, newsLink;

    private ProgressDialog progressDialog;// = new ProgressDialog(getActivity());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_news);

        primaryIdTo = getIntent().getStringExtra("PostId");
        categoryIdTo = getIntent().getStringExtra("Category");


        layout = (LinearLayout)findViewById(R.id.read_news);


        //for set setting fragment-------------------
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        Fragment relatesPost = new RelatesPost();//Get Fragment Instance
//        Bundle data = new Bundle();//Use bundle to pass data
////        data.putString("data", "This is Argument Fragment");//put string, int, etc in bundle
//
//        data.putString("PostId", primaryIdTo);
//        data.putString("Category", categoryIdTo);
//        // with a key value
//        relatesPost.setArguments(data);//Finally set argument bundle to fragment
//
//        fragmentManager.beginTransaction().replace(R.id.related_post,
//                relatesPost).commit();//now replace the argument fragment


        RelatedAdapter adapter;
        ArrayList<RelatedData> relatedDataList = new ArrayList<>();

        ListView listView1 = (ListView) findViewById(R.id.related_post);
        adapter = new RelatedAdapter( this, relatedDataList);
        listView1.setAdapter(adapter);

        try {
            new RelatedPostClass(adapter, relatedDataList, primaryIdTo, categoryIdTo, this).executeCommand();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        //------------------------------

        webViewMsg = (WebView) findViewById(R.id.web_msg);
        msgSubjectTv = (TextView) findViewById(R.id.news_subject);
//        msgBodyP1Tv = (TextView) findViewById(R.id.news_body_p1);
        msgBodyP2Tv = (TextView) findViewById(R.id.news_body_p2);
        authorTv = (TextView) findViewById(R.id.by_author);
        dateTv = (TextView) findViewById(R.id.news_date);
        timeTv = (TextView) findViewById(R.id.news_time);
        msgBodyImageIv = (ImageView) findViewById(R.id.body_image);
        like_tv = (TextView) findViewById(R.id.like);
        view_tv = (TextView) findViewById(R.id.views);
        comment_tv = (TextView) findViewById(R.id.comment);
        noComment = (TextView) findViewById(R.id.comment_txt);

        comment_tv.setOnClickListener(this);

        likeIV = (ImageView) findViewById(R.id.like_up);
        likeIV.setOnClickListener(this);
        findViewById(R.id.fb).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.whatsapp).setOnClickListener(this);

        //-----------------------------------
        commentsList = new ArrayList<>();

//        ListView listView = (ListView) findViewById(R.id.comments);
//        commentAdapter = new CommentAdapter(this, commentsList);
//        listView.setAdapter(commentAdapter);



        mRecyclerView = (RecyclerView) findViewById(R.id.comments);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
//        mAdapter = new CommentAdapterTmp(this, commentsList);
//        mRecyclerView.setAdapter(mAdapter);

        //------------------------------------------

        new ConnectingToServer().execute("");
    }

    @Override
    protected void onStart() {
        super.onStart();

        new LoadingComments().execute(LOAD_COMMENTS);
        new SendingLike("view").execute(VIEW_POST, primaryIdTo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.like_up:
                new SendingLike("like").execute(LIKE_POST, primaryIdTo);
                break;

            case R.id.fb:
                Intent shareIntent1 = new Intent(Intent.ACTION_SEND);
//                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent1.putExtra(Intent.EXTRA_SUBJECT, "News headline from Gambhir mudda " +
                        SITE_URL);
                shareIntent1.putExtra(Intent.EXTRA_TEXT, Title + " link " + newsLink);
                shareIntent1.setPackage("com.facebook");
                shareIntent1.setType("text/plain");
                shareIntent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(shareIntent1);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReadNews.this, "facebook not installed", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.whatsapp:

//                Uri imageUri = Uri.parse(Environment.getExternalStorageDirectory()+
//                        "/wide-awake/images/32.png");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "News headline from Gambhir mudda " +
                        SITE_URL);
                shareIntent.putExtra(Intent.EXTRA_TEXT, Title + " link " + newsLink);
                shareIntent.setPackage("com.whatsapp");
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReadNews.this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.share:
                Intent shareIntent2 = new Intent(Intent.ACTION_SEND);
//                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent2.putExtra(Intent.EXTRA_SUBJECT, "News headline from Gambhir mudda " +
                        SITE_URL);
                shareIntent2.putExtra(Intent.EXTRA_TEXT, Title + " link " + newsLink);
                shareIntent2.setType("text/plain");
                shareIntent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(shareIntent2);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReadNews.this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.comment:
                android.app.FragmentManager manager = getFragmentManager();
                CommentDialog editNameDialog = new CommentDialog();
                editNameDialog.show(manager, "fragment_edit_name");
                break;
        }
    }

    @Override
    public void onFinishUserDialog(String name, String phone, String comment) {
//        Toast.makeText(this, "Hello, " + name + " \n " + comment, Toast.LENGTH_SHORT).show();

        new CommentServer(name, phone, comment).execute(POST_COMMENTS   );
    }

    //----------------html file-----------

    @SuppressLint("NewApi")
    private void creatingHtmlFile() throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("arvindo_folder", Context.MODE_PRIVATE);

        File file = new File(directory, "arvindo.html");
        if(file.exists()){
            Log.e("output file", "true");
        }
        else {
            Log.e("output file", "false");
        }

        try (
                PrintWriter htmlFile = new PrintWriter(file)) {

            htmlFile.println(NewsBody);
            htmlFile.close();
        }

    }

    private void loadNewsInWebView(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("arvindo_folder", Context.MODE_PRIVATE);

        File file = new File(directory, "arvindo.html");
        if(file.exists()){
            webViewMsg.loadUrl("file://" + String.valueOf(file));

            Log.e("file path", "file://" + String.valueOf(file));
            Log.e("load file", directory + "/" + "arvindo.html");
        }
        else{
            Log.e("load file", "false");
        }
    }

    //-----------------loading data-------------
    private class ConnectingToServer extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(ReadNews.this, AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage(getResources().getString(R.string.loding));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            return requestServerForDataString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            try {
                if (result != null) {
//                    Toast.makeText(ReadNews.this, result, Toast.LENGTH_SHORT).show();

                    loadNewsInWebView();
                    msgSubjectTv.setText(Title);
//                    msgBodyP1Tv.setText(NewsBody);
                    authorTv.setText(Author);
                    date = date.substring(8) + "/" + date.substring(5, 7) + "/" +
                            date.substring(0, 4);
                    dateTv.setText(date);
                    timeTv.setText(time);
                    like_tv.setText(Likes);
//                    view_tv.setText(Views);
                    if(Views != null && !Views.equals("null") && !Views.equals("")){
                        view_tv.setText(Views);
                    }
                    else{
                        view_tv.setText("0");
                    }

                    if(Likes != null && !Likes.equals("null") && !Likes.equals("")){
                        like_tv.setText(Likes);
                    }
                    else{
                        like_tv.setText("0");
                    }
//                    comment_tv.setText();


                    Picasso.with(ReadNews.this)
                            .load(Config.HOME_IMAGE_FOLDER + FeaturedImage)
                            .placeholder(R.drawable.face) //
                            .error(R.drawable.face) //
                            .fit() //
                            .into(msgBodyImageIv);

                } else {
                    Toast.makeText(ReadNews.this, "Try Again", Toast.LENGTH_SHORT).show();
                }

                Log.e("DownloadTextTask<>", result);
            }
            catch (Exception ignored){}
        }
    }

    private String requestServerForDataString() {
        try {
            URL url = new URL(LOAD_DATA);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.connect();

            String reqHead = "Accept:application/json";
            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection","Keep-Alive"+reqHead);
            //Header header = new Header();

            @SuppressWarnings("deprecation")
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//            entity.addPart("register_type",new StringBody(id_s));

            entity.addPart("PostId",new StringBody(primaryIdTo));

            connection.addRequestProperty("content-length",entity.getContentLength()+"");
            connection.addRequestProperty(entity.getContentType().getName(),
                    entity.getContentType().getValue());

            OutputStream os = connection.getOutputStream();
            entity.writeTo(connection.getOutputStream());
            os.close();
            Log.d("HITTING","hitting url");
            connection.connect();

            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                return readStream(connection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fails";
    }

    private String readStream(InputStream inputStream) {

        String response = "";
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();

        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine())!=null){
                builder.append(line);
                Log.e("\n", builder.toString());
            }
            response = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("read server ", response);

        try {
            initializedData(response);
            response = getResources().getString(R.string.loding);
        } catch (Exception e) {
            e.printStackTrace();
            response = getResources().getString(R.string.error);
        }

        return response;
    }

    private void initializedData(String response) throws Exception {
        if(response != null && !response.equals("")) {
            try {
                JSONObject c = new JSONObject(response);    // create JSON obj from string
                PostId = c.getString("PostId");             //primary key, who posted
                Category = c.getString("Category");      // tab catagory
                Title = c.getString("Title");                 //msg sub
                NewsBody = c.getString("Description");        //msg sub
                FeaturedImage = c.getString("FeaturedImage"); //image path
                Tags = c.getString("Tags");         //
                Views = c.getString("Views");      //views
                Likes = c.getString("Likes");          //likes
                Author = c.getString("Author");
                Status = c.getString("Status");
                newsLink = OPEN_NEWS_WEB + c.getString("slug");
                String dateTime = c.getString("PublishedOn");

                date = dateTime.substring(0, 10);
                time = dateTime.substring(11, 16);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            creatingHtmlFile();

        }
    }

    //---------------------------------------like-------------------


    private class SendingLike extends AsyncTask<String, Integer, String> {

        private final String status;
        private String success = "";

        SendingLike(String status) {
            this.status = status;
        }

        @Override
        protected String doInBackground(String... url) {
            return requestServerForDataString(url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (success != null && success.equals("Success")) {
                    Toast.makeText(ReadNews.this, "you like it", Toast.LENGTH_SHORT).show();
                    if (status.equals("like")) {
                        like_tv.setText(Likes);
//                        likeIV.setBackground(ContextCompat.getDrawable(ReadNews.this, R.drawable.like_up));
                    }
                    else{
                        view_tv.setText(Views);
                    }
                } else {
                    if(status.equals("like")) {
                        Toast.makeText(ReadNews.this, getResources().getString(R.string.try_again),
                                Toast
                                .LENGTH_SHORT).show();
                    }
                }

                Log.e("DownloadTextTask-----", result);
            } catch (Exception ignored) {
            }
        }

        private String requestServerForDataString(String[] stringsArray) {
            try {
                URL url = new URL(stringsArray[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.connect();

                String reqHead = "Accept:application/json";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("connection", "Keep-Alive" + reqHead);
                //Header header = new Header();

                @SuppressWarnings("deprecation")
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                entity.addPart("PostId", new StringBody(stringsArray[1]));

                connection.addRequestProperty("content-length", entity.getContentLength() + "");
                connection.addRequestProperty(entity.getContentType().getName(),
                        entity.getContentType().getValue());

                OutputStream os = connection.getOutputStream();
                entity.writeTo(connection.getOutputStream());
                os.close();
                Log.d("HITTING", "hitting url");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return readStream1(connection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fails";
        }

        private String readStream1(InputStream inputStream) {

            String response = "";
            BufferedReader reader;
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    Log.e("\n", builder.toString());
                }
                response = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e("read server ", response);

            try {
                if (status.equals("like")) {
                    initializedData1(response);
                }
                else{
                    initializedData2(response);
                }
//                response = "loading...";
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
            }

            return response;
        }

        private void initializedData1(String response) throws Exception {
            if (response != null && !response.equals("")) {
                try {
                    JSONObject c = new JSONObject(response);    // create JSON obj from string
//                    PostId = c.getString("PostId");             //primary key, who posted
                    Likes = c.getString("Like");      // tab catagory
                    success = c.getString("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void initializedData2(String response) throws Exception {
            if (response != null && !response.equals("")) {
                try {
                    JSONObject c = new JSONObject(response);    // create JSON obj from string
//                    PostId = c.getString("PostId");             //primary key, who posted
                    Views = c.getString("Views");      // tab catagory
                    success = c.getString("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //---------------------------------------comment-------------------


    private class CommentServer extends AsyncTask<String, Integer, String> {

        private final String name;
        private final String phone;
        private final String comment;
        private String success = "";

        CommentServer(String name, String phone, String comment) {
            this.name = name;
            this.phone = phone;
            this.comment = comment;
        }

        @Override
        protected String doInBackground(String... url) {
            return requestServerForDataString(url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (success != null && success.equals("Success")) {
                    Toast.makeText(ReadNews.this, "comment posted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReadNews.this, getResources().getString(R.string.try_again),
                            Toast
                            .LENGTH_SHORT).show();
                }

                Log.e("DownloadTextTask=======", result);
            } catch (Exception ignored) {
            }
        }

        private String requestServerForDataString(String[] stringsArray) {
            try {
                URL url = new URL(stringsArray[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.connect();

                String reqHead = "Accept:application/json";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("connection", "Keep-Alive" + reqHead);
                //Header header = new Header();

                @SuppressWarnings("deprecation")
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                entity.addPart("PostId", new StringBody(primaryIdTo));
                entity.addPart("Name", new StringBody(name));
                entity.addPart("Phone", new StringBody(phone));
                entity.addPart("Comment", new StringBody(comment));

                connection.addRequestProperty("content-length", entity.getContentLength() + "");
                connection.addRequestProperty(entity.getContentType().getName(),
                        entity.getContentType().getValue());

                OutputStream os = connection.getOutputStream();
                entity.writeTo(connection.getOutputStream());
                os.close();
                Log.d("HITTING", "hitting url");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return readStream1(connection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fails";
        }

        private String readStream1(InputStream inputStream) {

            String response = "";
            BufferedReader reader;
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    Log.e("\n", builder.toString());
                }
                response = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e("read server ", response);

            try {
                initializedData1(response);
                response = "loading...";
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
            }

            return response;
        }

        private void initializedData1(String response) throws Exception {
            if (response != null && !response.equals("")) {
                try {
                    JSONObject c = new JSONObject(response);    // create JSON obj from string
                    success = c.getString("Status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //---------------------comment load---------------

    private class LoadingComments extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... url) {
            return requestServerForDataString(url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.equals("ok")) {
                if(commentsList.size() > 0){
                    ViewGroup.LayoutParams params = layout.getLayoutParams();
                    params.height = 400;
//                    params.width = 100;
                    layout.setLayoutParams(params);
                }
                else {
                    noComment.setVisibility(View.VISIBLE);
                }

                ReadNews.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        commentAdapter.notifyDataSetChanged();

                        mAdapter = new CommentAdapterTmp(ReadNews.this, commentsList);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
            }

            Log.e("comment", result);
        }

        private String requestServerForDataString(String[] stringsArray) {
            try {
                URL url = new URL(stringsArray[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.connect();

                String reqHead = "Accept:application/json";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("connection", "Keep-Alive" + reqHead);
                //Header header = new Header();

                @SuppressWarnings("deprecation")
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                entity.addPart("PostId", new StringBody(primaryIdTo));

                connection.addRequestProperty("content-length", entity.getContentLength() + "");
                connection.addRequestProperty(entity.getContentType().getName(),
                        entity.getContentType().getValue());

                OutputStream os = connection.getOutputStream();
                entity.writeTo(connection.getOutputStream());
                os.close();
                Log.d("HITTING", "hitting url");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return readStream(connection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fails";
        }

        private String readStream(InputStream inputStream) {

            String response = "";
            BufferedReader reader;
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    Log.e("\n", builder.toString());
                }
                response = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e("read server ", response);

            try {
                initializedData(response);
                response = "ok";
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
            }

            return response;
        }

        private String initializedData(String response){
            String ok = "";
            if (response != null && !response.equals("")) {
                try {
                    Log.e("releted post----", response);
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject c = jsonArray.getJSONObject(i);
                            String PostId = c.getString("PostId");             //primary key, who posted
                            String Name = c.getString("Name");      // tab catagory
                            String Phone = c.getString("Phone");                 //msg sub
                            String Comment = c.getString("Comment");         //
                            String dateTime = c.getString("CreateDate");

                            String date = "";
                            String time = "";
                            if(!dateTime.equals("null") && !dateTime.equals("")) {
                                date = dateTime.substring(0, 10);
                                time = dateTime.substring(11, 16);
                            }
                            ok = "ok";
                            commentsList.add(new Comments(PostId, Name, Phone, Comment, date,
                                    time));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return ok;
        }
    }
}