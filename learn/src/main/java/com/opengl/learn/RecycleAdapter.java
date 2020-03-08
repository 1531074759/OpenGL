package com.opengl.learn;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mDatas;

    public RecycleAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        String[] split = mDatas.get(i).split("\\.");
        holder.title.setText(split[split.length - 1]);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RenderActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mDatas.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
        }
    }
}
