package com.uu.udemo.itemtouch;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.uu.udemo.R;

import java.util.ArrayList;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/11/30.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private ArrayList<String> mDatas;
    private Context mContext;

    public ItemAdapter(ArrayList<String> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("uu", "parent的宽度："+parent.getWidth());
        View content = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_touch, parent, false);
        return new ItemHolder(content);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return mDatas == null?0:mDatas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        TextView textView;
        int position = -1;

        public ItemHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_item);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "您点击的是"+position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setData(int position) {
            this.position = position;
            textView.setText(mDatas.get(position));
            if (position%2 == 0) {
                textView.setBackgroundColor(Color.RED);
            }else {
                textView.setBackgroundColor(Color.GREEN);
            }
        }
    }
}
