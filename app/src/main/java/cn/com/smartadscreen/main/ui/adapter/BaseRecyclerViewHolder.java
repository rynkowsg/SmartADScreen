package cn.com.smartadscreen.main.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import cn.com.smartadscreen.main.ui.view.OnItemClickListener;
import cn.com.smartadscreen.main.ui.view.OnItemLongClickListener;

/**
 * Created by whyte on 2016/7/23 0023.
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public BaseRecyclerViewHolder(@NonNull View v, OnItemClickListener listener, OnItemLongClickListener itemLongClickListener) {
        super(v);
        ButterKnife.bind(this, v);
        this.itemClickListener = listener;
        this.itemLongClickListener = itemLongClickListener;
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null)
            itemClickListener.onItemClick(v, getLayoutPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        return itemLongClickListener != null && itemLongClickListener.onItemLongClick(v, getLayoutPosition());
    }
}
