package com.example.editanywhere.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    protected List<T> list = new ArrayList<>();

    public BaseAdapter() {
    }

    @NonNull
    @Override
    public V onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void onDataSetChanged(List<T> list) {
        if (list != null) {
            int preSize = this.list.size();
            this.list.clear();
            notifyItemRangeRemoved(0, preSize);
            this.list.addAll(list);
            notifyItemRangeChanged(0, list.size());
        }
    }

    public void onDataSetDeleteOne(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
            //刷新下标，不然下标就重复
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    public void onDataSetInsertOne(int position, T item) {
        if (position >= 0 && position <= list.size()) {
            list.add(position, item);
            notifyItemInserted(position);
            //刷新下标，不然下标就不连续
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    public void onDataSetEditOne(int position, T item) {
        if (list != null && position >= 0 && position < list.size()) {
            list.set(position, item);
            notifyItemChanged(position);
        }
    }
}
