package com.example.dropdownmenu.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dropdownmenu.R;
import com.example.dropdownmenu.bean.FirstMenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/6.
 */

public class AdapterForRecylerView extends RecyclerView.Adapter<AdapterForRecylerView.ViewHolder>{
    private List<FirstMenuModel> itemList;

    private OnItemClickListener click;
    /*
     * 记录所点击item相对于屏幕的横坐标
     */
    private float rawX;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
        }
    }

    public AdapterForRecylerView(List<FirstMenuModel> list) {
        itemList = new ArrayList<>();
        itemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_adapter_item, parent, false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.name.setText(itemList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onItemClick(holder.getAdapterPosition());
            }
        });
        // 获取所点击Item相对于屏幕的横坐标
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    rawX = motionEvent.getRawX();
                }
                return false;   //这里要返回false,返回true会使点击事件无效
            }
        });
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

    // 获取点击点相对于屏幕的横坐标，返回给Activity
    public float getRawX() {
        return rawX;
    }
}