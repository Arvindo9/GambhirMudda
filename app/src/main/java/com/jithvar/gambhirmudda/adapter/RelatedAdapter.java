package com.jithvar.gambhirmudda.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jithvar.gambhirmudda.R;
import com.jithvar.gambhirmudda.ReadNews;
import com.jithvar.gambhirmudda.constant.Config;
import com.jithvar.gambhirmudda.handler.RelatedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arvindo Mondal on 28/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class RelatedAdapter extends BaseAdapter {

    private final FragmentActivity activity;
//    private final Context context;
    private final ArrayList<RelatedData> homeDataList;
    private ProgressDialog mProgressDialog;


    public RelatedAdapter(FragmentActivity activity,
                          ArrayList<RelatedData> homeDataList) {
        this.activity = activity;
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
        final RelatedAdapter.DataHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.home_container,parent,false);
            holder = new DataHolder();
            holder.date_tv = (TextView)convertView.findViewById(R.id.news_date);
            holder.time_tv = (TextView)convertView.findViewById(R.id.news_time);
            holder.msgHeader_tv = (TextView)convertView.findViewById(R.id.news_subject);
            holder.profilePic = (ImageView) convertView.findViewById(R.id.profile_pic);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.linear_layout);
            convertView.setTag(holder);
        } else {
            holder = (DataHolder) convertView.getTag();
        }

        final RelatedData item = (RelatedData) this.getItem(position);
        assert item != null;

//        holder.userId_tv.setText(item.get_UserId());
        holder.date_tv.setText(item.getDate());
        holder.time_tv.setText(item.getTime());
        holder.msgHeader_tv.setText(item.getTitle());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final String fileName = item.getFeaturedImage();
        final String fileExtention = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length());

        Picasso.with(activity)
                .load(Config.HOME_IMAGE_FOLDER + fileName)
                .placeholder(R.drawable.face) //
                .error(R.drawable.face) //
                .fit() //
                .tag(holder) //
                .into(holder.profilePic);

//        Log.e("file", fileName + " " + fileExtention);

        holder.layout.setOnClickListener(new View.OnClickListener() {
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

    private class DataHolder{
        private ImageView bodyImage;
        private ImageView profilePic;
        private ImageView msgFile;
        private TextView name_tv;
        private TextView college_tv;
        private TextView userType_tv;
        private TextView department_tv;
        private TextView branchName_tv;
        private TextView date_tv;
        private TextView time_tv;
        private TextView msgBody_tv;
        private TextView msgHeader_tv;
        private LinearLayout layout;
    }

}
