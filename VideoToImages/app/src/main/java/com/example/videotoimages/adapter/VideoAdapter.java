package com.example.videotoimages.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videotoimages.activity.PlayVideoActivity;
import com.example.videotoimages.R;
import com.example.videotoimages.model.Video;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.viewHolder> {
    Context context;
    ArrayList<Video> videoArrayList;
    public OnItemClickListener onItemClickListener;
    DateFormat simpleTime = new SimpleDateFormat("mm:ss");

    public VideoAdapter(Context context, ArrayList<Video> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public VideoAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.viewHolder holder, final int position) {
//        Glide.with(context).load("file://" + videoArrayList.get(position).getBitmap())
//                .skipMemoryCache(false)
//                .into(holder.imgVideoPreView);
        holder.imgVideoPreView.setImageBitmap(videoArrayList.get(position).getBitmap());
        holder.videoName.setText(videoArrayList.get(position).getName());
        holder.videoDur.setText(simpleTime.format(videoArrayList.get(position).getDuration()) + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PlayVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("videoName", videoArrayList.get(position).getName());
                bundle.putString("videoPath", videoArrayList.get(position).getPath());
                bundle.putInt("videoDur", videoArrayList.get(position).getDuration());
                intent.putExtra("Path", bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView videoName, videoDur;
        ImageView imgVideoPreView;

        public viewHolder(View itemView) {
            super(itemView);
            imgVideoPreView = itemView.findViewById(R.id.imgVideoPreview);
            videoName = (TextView) itemView.findViewById(R.id.videoName);
            videoDur = (TextView) itemView.findViewById(R.id.videoDur);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}
