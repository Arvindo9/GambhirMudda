package com.jithvar.gambhirmudda;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
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
import java.util.concurrent.ExecutionException;

import static com.jithvar.gambhirmudda.constant.Config.RELATED_POST;

/**
 * Created by Arvindo Mondal on 3/8/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
class RelatedPostClass extends AsyncTask<String, Integer, String> {

    private RelatedAdapter adapter;
    private ArrayList<RelatedData> relatedDataList;
    private String primaryIdTo;
    private String categoryIdTo;
    private Activity activity;

    RelatedPostClass(RelatedAdapter adapter, ArrayList<RelatedData> relatedDataList,
                     String primaryIdTo, String categoryIdTo, Activity activity){
        this.adapter = adapter;
        this.relatedDataList = relatedDataList;
        this.primaryIdTo = primaryIdTo;
        this.categoryIdTo = categoryIdTo;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... url) {
        return requestServerForDataString(url);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
        Log.e("DownloadTextTask", result);
    }

    private String requestServerForDataString(String[] urlList) {
        try {
            URL url = new URL(urlList[0]);
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
        if (response != null && !response.equals("")) {
            try {
                Log.e("releted post----", response);
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    void executeCommand()throws ExecutionException, InterruptedException{
        execute(RELATED_POST);
    }
}
