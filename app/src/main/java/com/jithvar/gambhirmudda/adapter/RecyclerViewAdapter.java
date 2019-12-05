package com.jithvar.gambhirmudda.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jithvar.gambhirmudda.R;

import java.util.ArrayList;

/**
 * Created by Arvindo Mondal on 3/27/2017.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyView> {

    private ArrayList<String> list;
    private FragmentActivity activity;

    @Override
    public int getItemCount() {
        return list.size();
    }

    RecyclerViewAdapter(ArrayList<String> horizontalList,FragmentActivity activity) {
        this.list = horizontalList;
        this.activity = activity;
    }

    class MyView extends RecyclerView.ViewHolder {

        private TextView bookTitleTv;
        private TextView authorTv;
        private TextView costTv;
        private TextView rateTv;
        private TextView menuListTv;
        private ImageView bookPicIv;

        MyView(View view) {
            super(view);

//            bookTitleTv = (TextView) view.findViewById(R.id.book_title);
//            authorTv = (TextView) view.findViewById(R.id.author);
//            costTv = (TextView) view.findViewById(R.id.cost);
//            rateTv = (TextView) view.findViewById(R.id.rate);
//            menuListTv = (TextView) view.findViewById(R.id.menu_list);
//            bookPicIv = (ImageView) view.findViewById(R.id.book_pic);

        }
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_h_container,
//                parent, false);

        return null;//new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        holder.bookTitleTv.setText(list.get(position));
        holder.authorTv.setText(list.get(position));
        holder.costTv.setText(list.get(position));
        holder.rateTv.setText(list.get(position));
        holder.menuListTv.setText(list.get(position));
//        holder.bookPicIv.setText(list.get(position));

        holder.bookPicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "sd", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
