package com.example.videotoimages.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.videotoimages.R;

import java.util.ArrayList;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {
    Context context;
    ArrayList<String> emojisList;

    public EmojiAdapter(Context context, ArrayList<String> emojisList) {
        this.context = context;
        this.emojisList = emojisList;
    }

    public EmojiAdapter.OnItemClickListener onItemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_emoji, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtEmoji.setText(emojisList.get(position));
    }

    @Override
    public int getItemCount() {
        return emojisList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmoji;

        ViewHolder(View itemView) {
            super(itemView);
            txtEmoji = itemView.findViewById(R.id.txtEmoji);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }

    }

    public void setOnItemClickListener(EmojiAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}
