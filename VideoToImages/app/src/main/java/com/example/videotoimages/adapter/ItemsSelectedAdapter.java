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
import com.example.videotoimages.model.VideoFolder;

import java.util.ArrayList;

public class ItemsSelectedAdapter extends RecyclerView.Adapter<ItemsSelectedAdapter.ViewHolder> {
    Context context;
    ArrayList<CreatedPhotos> arrayList;

    public ItemsSelectedAdapter(Context context, ArrayList<CreatedPhotos> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_created_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img.setImageBitmap(arrayList.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img);
        }
    }
}
