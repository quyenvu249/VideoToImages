package com.example.videotoimages.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.videotoimages.PhotoDetailActivity;
import com.example.videotoimages.R;
import com.example.videotoimages.model.CreatedPhotos;

import java.util.ArrayList;

public class CreatedPhotosAdapter extends BaseAdapter {
    Context context;
    ArrayList<CreatedPhotos> arrayList;
    LayoutInflater inflater;

    public CreatedPhotosAdapter(Context context, ArrayList<CreatedPhotos> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_created_photos, null);
            viewHolder = new ViewHolder();
            viewHolder.img = convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CreatedPhotos createdPhotos = arrayList.get(position);
        viewHolder.img.setImageBitmap(createdPhotos.getBitmap());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bitmap", String.valueOf(createdPhotos.getBitmap()));
                bundle.putString("path", arrayList.get(position).getPhotoPath());
                bundle.putString("name", createdPhotos.getPhotoName());
                bundle.putInt("size", createdPhotos.getPhotoSize());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("bundlePhoto", bundle);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView img;
    }
}
