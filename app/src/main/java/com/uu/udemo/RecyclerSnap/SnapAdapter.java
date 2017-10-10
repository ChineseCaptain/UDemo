package com.uu.udemo.RecyclerSnap;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.uu.udemo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/9/26.
 */

public class SnapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<String> datas = new ArrayList<>();

    public SnapAdapter(Activity activity, ArrayList<String> datas) {
        this.activity = activity;
        this.datas = datas;
    }

    @Override
    public SnapHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_snap, parent, false);
        SnapHolder holder = new SnapHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SnapHolder) {
            ((SnapHolder) holder).setContent(position);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class SnapHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;

        SnapHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setContent(int position) {
            Glide.with(activity).load(datas.get(position)).centerCrop().into(image);
        }
    }
}
