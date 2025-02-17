package com.citizen.fmc.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.citizen.fmc.R;

import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.Holder> {
    Context context;
    ArrayList<Integer> bannerList;
    public BannerAdapter(Context context, ArrayList<Integer> bannerList){
        this.context = context;
        this.bannerList = bannerList;
    }
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.adapter_banner_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.Holder holder, int position) {
        Glide.with(context).load(bannerList.get(position)).thumbnail(Glide.with(context).load(R.drawable.banner_image)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_banner);
        }
    }
}
