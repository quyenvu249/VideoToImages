package com.example.videotoimages.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.videotoimages.R;
import com.example.videotoimages.activity.PhotoDetailActivity;
import com.example.videotoimages.model.CreatedPhotos;

import java.util.ArrayList;

public class CreatedPhotosAdapter extends BaseAdapter {
    Context context;
    ArrayList<CreatedPhotos> arrayList;
    LayoutInflater inflater;
    ArrayList<CreatedPhotos> arrToDelete;

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
        arrToDelete = new ArrayList<>();
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_created_photos, null);
            viewHolder = new ViewHolder();
            viewHolder.img = convertView.findViewById(R.id.img);
            viewHolder.cbState = convertView.findViewById(R.id.cbState);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CreatedPhotos createdPhotos = arrayList.get(position);
        viewHolder.img.setImageBitmap(createdPhotos.getBitmap());

        if (createdPhotos.isChecked() == true) {
            viewHolder.cbState.setChecked(true);
            viewHolder.cbState.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cbState.setChecked(false);
            viewHolder.cbState.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView img;
        CheckBox cbState;
    }

    public void removeCheck(int pos) {
        CreatedPhotos createdPhotos = arrayList.get(pos);
    }

}
