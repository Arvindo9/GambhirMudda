package com.jithvar.gambhirmudda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.jithvar.gambhirmudda.adapter.TwoAdapter;
import com.jithvar.gambhirmudda.handler.TabTwoData;

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
import static com.jithvar.gambhirmudda.constant.Config.CATRGORY_LOAD_DATA;

/**
 * Created by Arvindo Mondal on 5/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class TabTwo extends Fragment implements AbsListView.OnScrollListener {

    private ProgressDialog progressDialog;// = new ProgressDialog(getActivity());
    private TwoAdapter homeAdapter;
    private ArrayList<TabTwoData> homeDataList;
    private int pageNO;
    private boolean loadNextList;
//    private HomeAdapter homeAdapter;
//    private ArrayList<HomeData> homeDataList;

    private boolean start_type;
    private int lastPrimaryId;
    //    private boolean isViewDestroued = false;
    private String userID = "";
    private int tabPosition;
    private boolean isDataAvalible;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start_type = true;
        lastPrimaryId = -1;
        homeDataList = new ArrayList<>();

//        db = new DataBaseAwake_1(getActivity());
//        try {
//            if(db.checkStatus()){
//                userID = db.getTrueUserId();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.tab_two, container, false);
//        myOnClickListener = new MyOnClickListener(this);

        pageNO = 1;

        tabPosition = getArguments().getInt("tabPosition");
        Log.e("tab pss--------", String.valueOf(tabPosition));

        isDataAvalible = true;

        ListView listView = (ListView) view.findViewById(R.id.tab_two_list);
        homeAdapter = new TwoAdapter(getActivity(), getContext(), homeDataList);
        listView.setAdapter(homeAdapter);
        listView.setOnScrollListener(this);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isNetworkAvailable()){
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
        else {
            new ConnectingToServer().execute("");
        }
//        try {
//            initializedDataTmp("cvc");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        switch(view.getId())
        {
            case R.id.tab_two_list:
                if(isDataAvalible) {
                    final int lastItem = firstVisibleItem + visibleItemCount;

//                    Log.e("--" + firstVisibleItem + " --- " + visibleItemCount, lastItem + " -- " +
//                            totalItemCount + " " + String.valueOf(loadNextList));

                    if (lastItem == totalItemCount && loadNextList) {
                        pageNO += 1;
//                        Log.e("page no-----------", String.valueOf(pageNO));
//                        new TabTwo.ConnectingToServer().execute("");
                        if (!isNetworkAvailable()){
                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new ConnectingToServer().execute("");
                        }
                    }
                }
        }
    }

    private class ConnectingToServer extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage("Loading request...");
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
//            try {
//                if (result != null) {
//                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
//                }
//
//                Log.e("DownloadTextTask", result);
//            }
//            catch (Exception ignored){}
        }
    }

    private String requestServerForDataString() {
        try {
            URL url = new URL(CATRGORY_LOAD_DATA);
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
            entity.addPart("CategoryId",new StringBody(String.valueOf(tabPosition)));

            connection.addRequestProperty("content-length",entity.getContentLength()+"");
            connection.addRequestProperty(entity.getContentType().getName(),
                    entity.getContentType().getValue());

            OutputStream os = connection.getOutputStream();
            entity.writeTo(connection.getOutputStream());
            os.close();
            Log.d("HITTING","hitting url");
            connection.connect();

//            Log.e("HITTING", String.valueOf(connection.getResponseCode()));
//            Log.e("HITTING", String.valueOf(HttpURLConnection.HTTP_OK));
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
            response = "loading...";
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

                if(jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject c = jsonArray.getJSONObject(i);
                            String PostId = c.getString("PostId");             //primary key, who posted
                            String PageNO = c.getString("CountId");      // page no
                            String Category = c.getString("Category");      // tab catagory
                            String Title = c.getString("Title");                 //msg sub
                            String NewsBody = c.getString("Description");                 //msg sub
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

                            homeDataList.add(new TabTwoData(PostId, Category, Title, NewsBody, Tags, Views,
                                    Likes, Author,
                                    Status, date, time, FeaturedImage));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    isDataAvalible = false;
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
            String PostId = "user_id_" + i;
            String Category = "college_name";
            String name = "name";
            String Title = "";// = c.getString("user_type");
            String NewsBody = "";
            String Tags = "jn";
            String Views = "nmnm";
            String Likes = "tis is msg header";
            String Author = "hey \n this is msg bogy";
            String msg_file = "";
            String FeaturedImage = "";
            String Status = "";
            String dateTime = "";

            String date = "22";//dateTime.substring(0, 10);
            String time = "52";//dateTime.substring(11);

//            userType = "";//temp

            homeDataList.add(new TabTwoData(PostId, Category, Title, NewsBody, Tags, Views,
                    Likes, Author,
                    Status, date, time, FeaturedImage));
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeDataList.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeDataList.clear();
        start_type = true;
//        isViewDestroued = true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
