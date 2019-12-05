package com.jithvar.gambhirmudda.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jithvar.gambhirmudda.R;
import com.jithvar.gambhirmudda.ReadNews;
import com.jithvar.gambhirmudda.constant.Config;
import com.jithvar.gambhirmudda.handler.Comments;
import com.jithvar.gambhirmudda.handler.RelatedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arvindo Mondal on 2/8/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class CommentAdapter extends BaseAdapter {
    private final ReadNews activity;
    private final ArrayList<Comments> commentsList;

    public CommentAdapter(ReadNews readNews, ArrayList<Comments> commentsList) {
        this.activity = readNews;
        this.commentsList = commentsList;
    }


    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentsList.get(position);
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
            convertView = inflater.inflate(R.layout.comment_adapter,parent,false);
            holder = new DataHolder();
            holder.date_tv = (TextView)convertView.findViewById(R.id.date_s);
            holder.time_tv = (TextView)convertView.findViewById(R.id.time_s);
            holder.name_tv = (TextView)convertView.findViewById(R.id.name_s);
            holder.comment_tv = (TextView) convertView.findViewById(R.id.comment_s);
            convertView.setTag(holder);
        } else {
            holder = (DataHolder) convertView.getTag();
        }

        final Comments item = (Comments) this.getItem(position);
        assert item != null;

//        holder.userId_tv.setText(item.get_UserId());
        holder.date_tv.setText(item.getDate());
        holder.time_tv.setText(item.getTime());
        holder.name_tv.setText(item.getName());
        holder.comment_tv.setText(item.getComment());

        Log.e("mahlkjjsdjflsdjfl", "k,djngkdfng");

        return convertView;
    }

    private class DataHolder{
        private TextView name_tv;
        private TextView comment_tv;
        private TextView date_tv;
        private TextView time_tv;
    }
}
