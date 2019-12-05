package com.jithvar.gambhirmudda;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.jithvar.gambhirmudda.adapter.HomeAdapter;
import com.jithvar.gambhirmudda.handler.HomeData;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.jithvar.gambhirmudda.constant.Config.HOME_ACTIVITY;

/**
 * Created by Arvindo Mondal on 5/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class HomeTab extends Fragment implements AbsListView.OnScrollListener{

    private ProgressDialog progressDialog;// = new ProgressDialog(getActivity());
    private HomeAdapter homeAdapter;
    private ArrayList<HomeData> homeDataList;
    private WebView webView;
    private ListView listView;
    private int pageNO;
    private boolean loadNextList;
//    private HomeAdapter homeAdapter;
//    private ArrayList<HomeData> homeDataList;

    private boolean start_type;
    private int lastPrimaryId;
    //    private boolean isViewDestroued = false;
    private String userID = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start_type = true;
        lastPrimaryId = -1;
        homeDataList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.home_tab, container, false);
//        myOnClickListener = new MyOnClickListener(this);
        pageNO = 1;

        listView = (ListView) view.findViewById(R.id.home_list);
        homeAdapter = new HomeAdapter(getActivity(), getContext(), homeDataList);
        listView.setAdapter(homeAdapter);
        listView.setOnScrollListener(this);
        loadNextList = false;

//        new ConnectingToServer().execute("");
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isNetworkAvailable()){
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast
                    .LENGTH_SHORT).show();
        }
        else{
            new ConnectingToServer().execute("");
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        switch(view.getId())
        {
            case R.id.home_list:
                final int lastItem = firstVisibleItem + visibleItemCount;

//                Log.e("--" + firstVisibleItem + " --- " + visibleItemCount, lastItem + " -- " +
//                            totalItemCount + " " + String.valueOf(loadNextList));

                if(lastItem == totalItemCount && loadNextList)
                {
                    pageNO += 1;
//                    Log.e("page no-----------", String.valueOf(pageNO));
//                    new ConnectingToServer().execute("");
                    if (!isNetworkAvailable()){
                        Toast.makeText(getActivity(), getResources().getString(R.string
                                        .no_internet),
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        new ConnectingToServer().execute("");
                    }
                }
        }
    }

    private class ConnectingToServer extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage(getResources().getString(R.string.loding));
            progressDialog.setCancelable(false);
            progressDialog.show();
            loadNextList = false;
        }

        @Override
        protected String doInBackground(String... url) {
            return requestServerForDataString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            loadNextList = true;
            try {
                if (result != null) {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.try_again),
                            Toast
                            .LENGTH_SHORT).show();
                }

                Log.e("DownloadTextTask", result);
            }
            catch (Exception ignored){}
        }
    }

    private String requestServerForDataString() {
        try {
            URL url = new URL(HOME_ACTIVITY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.connect();

            String reqHead = "Accept:application/json";
            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection","Keep-Alive"+reqHead);
            //Header header = new Header();

            @SuppressWarnings("deprecation")
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//            entity.addPart("register_type",new StringBody(id_s));

            entity.addPart("CountId", new StringBody(String.valueOf(pageNO)));
            entity.addPart("CategoryId",new StringBody("0"));

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
        Log.e("responset form server ", response);

        try {
            initializedData(response);
            response = getResources().getString(R.string.loding);
        } catch (Exception e) {
            e.printStackTrace();
            response = "error";
        }

        return response;
    }

    private void initializedData(String response) throws Exception {
        if(response != null && !response.equals("")){
            try {
                Log.e("sdf", response);
                JSONArray jsonArray = new JSONArray(response);

                for(int i=0;i<jsonArray.length();i++){
                    try {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String PostId = c.getString("PostId");             //primary key, who posted
                        String PageNO = c.getString("CountId");      // page no
                        String Category = c.getString("Category");      // tab catagory
                        String Title = c.getString("Title");                 //msg sub
                        String FeaturedImage = c.getString("FeaturedImage"); //image path
                        String Tags = c.getString("Tags");         //
                        String Views = c.getString("Views");      //views
                        String Likes = c.getString("Likes");          //likes
                        String Author = c.getString("Author");
                        String Status = c.getString("Status");
                        String dateTime = c.getString("PublishedOn");

                        String date = dateTime.substring(0, 10);
                        date = date.substring(8) + "/" + date.substring(5, 7) + "/" +
                                date.substring(0, 4);
                        String time = dateTime.substring(11, 16);
                        pageNO = Integer.parseInt(PageNO);

                        homeDataList.add(new HomeData(PostId, Category, Title, Tags, Views, Likes, Author,
                                Status, date, time, FeaturedImage));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

//                loadNextList = true;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        homeAdapter.notifyDataSetChanged();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializedDataTmp(String response) throws Exception {
//        if(response != null && !response.equals("")){
        Log.e("sdf", response);
//                JSONArray jsonArray = new JSONArray(response);

        String primary_id = "-1";
        int count =20;
        for(int i=0;i<10;i++){
//                    JSONObject c = jsonArray.getJSONObject(i);
            primary_id = String.valueOf(i);
            String user_id = "user_id_" + i;
//                String clgName = "college_name";
//                String name = "name";
            String userType;// = c.getString("user_type");
            String pic_path_name = "";
//                String department_name = "jn";
//                String branch_name = "nmnm";
            String msg_header = "tis is msg header";
//                String msg_body = "hey \n this is msg bogy";
            String msg_file = "";
            String video_thumb = "";
            String dateTime = "";

            String date = "22";//dateTime.substring(0, 10);
            String time = "52";//dateTime.substring(11);

            userType = "";//temp

//                homeDataList.add(new HomeData(user_id, msg_header, pic_path_name,
//                        msg_file, date, time, video_thumb));
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                homeAdapter.notifyDataSetChanged();
            }
        });

        this.lastPrimaryId = Integer.parseInt(primary_id);
        if (lastPrimaryId > 0){
            start_type = false;
        }
//        }
    }    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeDataList.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeDataList.clear();
//        start_type = true;
//        isViewDestroued = true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
