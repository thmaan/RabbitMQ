package com.example.rabbitmq;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<String> mData;

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView tvMsg;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
        }
    }
    public ChatAdapter(ArrayList<String> data){
        mData = data;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        ChatViewHolder cvh = new ChatViewHolder(view);

        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        String currentMsg = mData.get(position);

        holder.tvMsg.setText(currentMsg);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}
