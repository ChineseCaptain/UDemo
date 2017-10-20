package com.uu.udemo.stackview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.uu.udemo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/10/17.
 */

public class StackAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<String> datas = new ArrayList<>();

    public StackAdapter(Context context, ArrayList<String> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SnapHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_snap, parent, false);
            holder = new SnapHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (SnapHolder) convertView.getTag();
        }
        holder.setContent(position);
        return convertView;
    }

    public class SnapHolder{
        @BindView(R.id.image)
        ImageView image;

        SnapHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setContent(int position) {
            Glide.with(mContext).load(datas.get(position)).centerCrop().into(image);
        }
    }
}
