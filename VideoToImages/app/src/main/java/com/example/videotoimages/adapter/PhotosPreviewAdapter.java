package com.example.videotoimages.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videotoimages.R;
import com.example.videotoimages.model.CreatedPhotos;

import java.util.ArrayList;

public class PhotosPreviewAdapter extends RecyclerView.Adapter<PhotosPreviewAdapter.viewHolder> {
    Context context;
    ArrayList<CreatedPhotos> arrayList;

    public PhotosPreviewAdapter(Context context, ArrayList<CreatedPhotos> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_created_photos, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        //Glide.with(context).load(arrayList.get(position).photoPath).into(holder.img);
        holder.img.setImageBitmap(arrayList.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }
}
