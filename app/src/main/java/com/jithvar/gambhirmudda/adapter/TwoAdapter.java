package com.jithvar.gambhirmudda.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jithvar.gambhirmudda.ImageDisplay;
import com.jithvar.gambhirmudda.R;
import com.jithvar.gambhirmudda.ReadNews;
import com.jithvar.gambhirmudda.VideoDisplay;
import com.jithvar.gambhirmudda.constant.Config;
import com.jithvar.gambhirmudda.handler.HomeData;
import com.jithvar.gambhirmudda.handler.TabTwoData;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.jithvar.gambhirmudda.constant.Config.HOME_ACTIVITY;
import static com.jithvar.gambhirmudda.constant.Config.LIKE_POST;
import static com.jithvar.gambhirmudda.constant.Config.OUTPUT_FILE_FOLDER;

/**
 * Created by Arvindo Mondal on 18/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class TwoAdapter extends BaseAdapter {

    private final FragmentActivity activity;
    private final Context context;
    private final ArrayList<TabTwoData> homeDataList;
    private ProgressDialog mProgressDialog;


    public TwoAdapter(FragmentActivity activity, Context context, ArrayList<TabTwoData> homeDataList) {
        this.activity = activity;
        this.context = context;
        this.homeDataList = homeDataList;
    }

    @Override
    public int getCount() {
        return homeDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return homeDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.common_container,parent,false);
            holder = new DataHolder();
            holder.date_tv = (TextView)convertView.findViewById(R.id.news_date);
            holder.time_tv = (TextView)convertView.findViewById(R.id.news_time);
            holder.msgHeader_tv = (TextView)convertView.findViewById(R.id.news_subject);
            holder.msg_body_tv = (TextView) convertView.findViewById(R.id.msg_body);
            holder.read_mode_tv = (TextView) convertView.findViewById(R.id.read_mode);
            holder.author_by_tv = (TextView) convertView.findViewById(R.id.by_author);
            holder.like_tv = (TextView) convertView.findViewById(R.id.like);
            holder.view_tv = (TextView) convertView.findViewById(R.id.views);
            holder.comment_tv = (TextView) convertView.findViewById(R.id.comment);
            holder.layout_1 = (LinearLayout) convertView.findViewById(R.id.layout_1);
            holder.layout_2 = (LinearLayout) convertView.findViewById(R.id.layout_2);

            holder.bodyImage = (ImageView) convertView.findViewById(R.id.body_image);
//            holder.faceBook = (ImageView) convertView.findViewById(R.id.fb);
//            holder.whatsApp = (ImageView) convertView.findViewById(R.id.whatsapp);
//            holder.share = (ImageView) convertView.findViewById(R.id.share);
            holder.like = (ImageView) convertView.findViewById(R.id.like_up);

            convertView.setTag(holder);
        } else {
            holder = (DataHolder) convertView.getTag();
        }

        final TabTwoData item = (TabTwoData) this.getItem(position);
        assert item != null;

//        holder.userId_tv.setText(item.get_UserId());
        holder.date_tv.setText(item.getDate());
        holder.time_tv.setText(item.getTime());
        holder.msgHeader_tv.setText(item.getTitle());

        holder.msg_body_tv.setText(item.getNewsBody());
        holder.author_by_tv.setText(item.getAuthor());

        holder.like.setBackground(ContextCompat.getDrawable(activity, R.drawable.like));

        if(item.getViews() != null) {
            holder.like_tv.setText(item.getLikes());
        }
        else{
            holder.like_tv.setText("0");
        }

        if(item.getViews() != null) {
            holder.view_tv.setText(item.getViews());
        }
        else{
            holder.view_tv.setText("0");
        }
//        holder.comment_tv.setText(item.());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final String fileName = item.getFeaturedImage();

        Picasso.with(context)
                .load(Config.HOME_IMAGE_FOLDER + fileName)
                .placeholder(R.drawable.face) //
                .error(R.drawable.face) //
                .fit() //
                .tag(holder) //
                .into(holder.bodyImage);

//        Log.e("file", fileName + " " + fileExtention);

        holder.read_mode_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

//        holder.faceBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendingLike(holder.like, holder.like_tv, "like").execute(item.getPostId());
            }
        });

        /*
        holder.whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = Config.HOME_IMAGE_FOLDER + fileName;
                URI uri = null;
                try {
                    uri = new URI(url);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Uri imageUri = Uri.parse("android.resource://com.jithvar.gambhirmudda/drawable/face.jpg");
//                Integer.toString(R.drawable.face));
                Log.e("jmsfgmsfg", String.valueOf(imageUri));
//                Uri imageUri = Uri.parse(pictureFile.getAbsolutePath());
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                //Target whatsapp:
                shareIntent.setPackage("com.whatsapp");
                //Add text and then Image URI
//                shareIntent.putExtra(Intent.EXTRA_TITLE, item.getAuthor());
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                shareIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle());
                shareIntent.setType("image/jpg");
//                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    activity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*/
        holder.layout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( activity, ReadNews.class);
                i.putExtra("PostId", item.getPostId());
                i.putExtra("Category", item.getCategory());
                activity.startActivity(i);
            }
        });


        holder.layout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( activity, ReadNews.class);
                i.putExtra("PostId", item.getPostId());
                i.putExtra("Category", item.getCategory());
                activity.startActivity(i);
            }
        });

        return convertView;
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog = new ProgressDialog(activity);
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getName());
//            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            BufferedInputStream input = null;
            BufferedOutputStream output = null;
            HttpURLConnection connection = null;
            String response = "";

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    response = connection.getResponseMessage();
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = new BufferedInputStream(connection.getInputStream());
                File dir = new File(OUTPUT_FILE_FOLDER);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                File outPut = new File(dir, params[1]);

                Log.e("path ffff", dir + "/" + params[1]);

//                File outPut = new File(Environment.getExternalStorageDirectory(), params[1]);
                if(params[2].equals("image")) {
                    output = new BufferedOutputStream(new FileOutputStream(OUTPUT_FILE_FOLDER + params[1]));
                }
                else{
                    output = new BufferedOutputStream(new FileOutputStream(outPut));
                }

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                response += "ok";
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }

                if (connection != null)
                    connection.disconnect();
            }

            return response;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            mProgressDialog.dismiss();
            // showing the server response in an alert dialog
            showAlert(result);
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(activity, "File downloaded", Toast.LENGTH_SHORT).show();
        }

    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class DataHolder{
        private ImageView bodyImage;
//        private ImageView faceBook;
//        private ImageView whatsApp;
//        private ImageView share;
        private ImageView like;

        private TextView author_by_tv;
        private TextView date_tv;
        private TextView time_tv;
        private TextView msgHeader_tv;
        private TextView msg_body_tv;
        private TextView read_mode_tv;
        private TextView like_tv;
        private TextView view_tv;
        private TextView comment_tv;
        private LinearLayout layout_1;
        private LinearLayout layout_2;
    }

    //---------------------------------------like-------------------


    private class SendingLike extends AsyncTask<String, Integer, String> {

        private final TextView tv;
        private final String status;
        private final ImageView like;
        private String PostId;
        private String Likes;
        private String success = "";

        SendingLike(ImageView like, TextView tv, String status) {
            this.like = like;
            this.tv = tv;
            this.status = status;
        }

        @Override
        protected String doInBackground(String... url) {
            return requestServerForDataString(url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("345345435n34543 34", success);
            try {
                if (success != null && success.equals("Success")) {
                    Toast.makeText(activity, "you like it", Toast.LENGTH_SHORT).show();

                    if (status.equals("like")) {
                        tv.setText(Likes);
                        like.setBackground(ContextCompat.getDrawable(activity, R.drawable.like_up));
                    }
                } else {
                    Toast.makeText(activity, "Try Again", Toast.LENGTH_SHORT).show();
                }

                Log.e("DownloadTextTask", result);
            } catch (Exception ignored) {
            }
        }

        private String requestServerForDataString(String[] stringsArray) {
            try {
                URL url = new URL(LIKE_POST);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.connect();

                String reqHead = "Accept:application/json";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("connection", "Keep-Alive" + reqHead);
                //Header header = new Header();

                @SuppressWarnings("deprecation")
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                entity.addPart("PostId", new StringBody(stringsArray[0]));

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
//                    PostId = c.getString("PostId");             //primary key, who posted
                    Likes = c.getString("Like");      // tab catagory
                    success = c.getString("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
