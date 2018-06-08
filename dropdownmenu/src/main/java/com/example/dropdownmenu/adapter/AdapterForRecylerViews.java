package com.example.dropdownmenu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dropdownmenu.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/6.
 */

public class AdapterForRecylerViews extends RecyclerView.Adapter<AdapterForRecylerViews.ViewHolder>{
    private List<Map<String, Object>> itemList;

    private OnItemClickListener click;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
        }
    }

    public AdapterForRecylerViews(List<Map<String, Object>> list) {
        itemList = new ArrayList<>();
        itemList = list;
    }

    public void setDataList(List<Map<String, Object>> list) {
        itemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_adapter_item, parent, false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Map<String, Object>item = itemList.get(position);
        holder.name.setText(item.get("name").toString());

        if(click != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.onItemClick(holder.getAdapterPosition());
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener click) {
        this.click = click;
    }
}