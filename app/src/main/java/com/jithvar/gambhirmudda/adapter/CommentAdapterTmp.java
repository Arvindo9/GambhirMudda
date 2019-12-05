package com.jithvar.gambhirmudda.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jithvar.gambhirmudda.R;
import com.jithvar.gambhirmudda.ReadNews;
import com.jithvar.gambhirmudda.handler.Comments;

import java.util.ArrayList;

/**
 * Created by Arvindo Mondal on 3/8/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class CommentAdapterTmp extends RecyclerView.Adapter<CommentAdapterTmp.ViewHolder>  {

    private final ReadNews activity;
    private final ArrayList<Comments> commentsList;

    public CommentAdapterTmp(ReadNews readNews, ArrayList<Comments> commentsList) {
        this.activity = readNews;
        this.commentsList = commentsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.comment_adapter, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String  d = commentsList.get(position).getName();
        Log.e("983759=========", d);

        holder.date_tv.setText(commentsList.get(position).getDate());
        holder.time_tv.setText(commentsList.get(position).getTime());
        holder.name_tv.setText(commentsList.get(position).getName());
        holder.comment_tv.setText(commentsList.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name_tv;
        private TextView comment_tv;
        private TextView date_tv;
        private TextView time_tv;
        public View layout;

        ViewHolder(View itemView) {
            super(itemView);

            date_tv = (TextView)itemView.findViewById(R.id.date_s);
            time_tv = (TextView)itemView.findViewById(R.id.time_s);
            name_tv = (TextView)itemView.findViewById(R.id.name_s);
            comment_tv = (TextView) itemView.findViewById(R.id.comment_s);
        }
    }


}
