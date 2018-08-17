package cn.com.smartadscreen.main.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.com.smartadscreen.main.ui.view.OnItemClickListener;
import cn.com.smartadscreen.main.ui.view.OnItemLongClickListener;

/**
 * Recycler Adapter 抽象类
 *
 */
public abstract class CommonRecyclerAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;
    @NonNull
    private final WeakReference<List<T>> weakReference;

    public CommonRecyclerAdapter(Context context, List<T> list) {
        weakReference = new WeakReference<>(list);
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if (weakReference.get() == null)
        {
            return 0;
        }
        return weakReference.get().size();
    }

    public void removeItem(int position)
    {
        if (weakReference.get() != null && weakReference.get().size()>position)
        {
            weakReference.get().remove(position);
        }
    }

    public void removeItem(T item)
    {
        if (weakReference.get() != null && item != null)
        {
            weakReference.get().remove(item);
        }
    }

    protected String getString(int resId){
        return mContext.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs){
        return mContext.getString(resId, formatArgs);
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolder(parent, viewType, itemClickListener, itemLongClickListener);
    }

    public T getItem(int position) {
        return weakReference.get().get(position);
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
//        if (mList == null) {
//            throw new ExecutionError(new Error("mList is null"));
//        }
        onBindViewHolder(holder, position, weakReference.get().get(position));
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * 设置Item 长按监听
     *
     * @param listener
     */
    public void setItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     * @param item
     */
    protected abstract void onBindViewHolder(V holder, int position, T item);

    /**
     * 创建holder
     *
     * @param parent
     * @param viewType
     * @param itemClickListener     item click event
     * @param itemLongClickListener item long click event
     * @return
     */
    protected abstract V onCreateViewHolder(ViewGroup parent, int viewType, OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickListener);
}
