package com.jithvar.gambhirmudda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

import com.jithvar.gambhirmudda.adapter.RelatedAdapter;
import com.jithvar.gambhirmudda.handler.RelatedData;

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

import static com.jithvar.gambhirmudda.constant.Config.RELATED_POST;

/**
 * Created by Arvindo Mondal on 28/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class RelatesPost extends Fragment {

    private RelatedAdapter adapter;
    private ArrayList<RelatedData> relatedDataList;
    private String primaryIdTo;
    private String categoryIdTo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relatedDataList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.related_post, container, false);

        primaryIdTo = getArguments().getString("PostId");
        categoryIdTo = getArguments().getString("Category");

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new RelatedAdapter(getActivity(), relatedDataList);
        listView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isNetworkAvailable()){
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
//
        new ConnectingToServer().execute("");
    }

    private class ConnectingToServer extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... url) {
            return requestServerForDataString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
                }

                Log.e("DownloadTextTask", result);
            }
            catch (Exception ignored){}
        }
    }

    private String requestServerForDataString() {
        try {
            URL url = new URL(RELATED_POST);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.connect();

            String reqHead = "Accept:application/json";
            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection","Keep-Alive"+reqHead);
            //Header header = new Header();

            @SuppressWarnings("deprecation")
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//            entity.addPart("register_type",new StringBody(id_s));

            entity.addPart("PostId", new StringBody(primaryIdTo));
            entity.addPart("CategoryId",new StringBody(categoryIdTo));

            connection.addRequestProperty("content-length",entity.getContentLength()+"");
            connection.addRequestProperty(entity.getContentType().getName(),
                    entity.getContentType().getValue());

            OutputStream os = connection.getOutputStream();
            entity.writeTo(connection.getOutputStream());
            os.close();
            Log.d("HITTING","hitting url");
            connection.connect();
            Log.e("Related post", String.valueOf(connection.getResponseCode()));
            Log.e("Related", String.valueOf(HttpURLConnection.HTTP_OK));

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
        Log.e("respond form server ", response);

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
                Log.e("releted post----", response);
                JSONArray jsonArray = new JSONArray(response);

                for(int i=0;i<jsonArray.length();i++){
                    try {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String PostId = c.getString("PostId");             //primary key, who posted
//                        String PageNO = c.getString("CountId");      // page no
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
                        String time = dateTime.substring(11, 16);
//                        pageNO = Integer.parseInt(PageNO);

                        relatedDataList.add(new RelatedData(PostId, Category, Title, Tags, Views,
                                Likes, Author,
                                Status, date, time, FeaturedImage));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.e(",fdfg===============", String.valueOf(relatedDataList.size()));

//                loadNextList = true;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        relatedDataList.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        relatedDataList.clear();
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
