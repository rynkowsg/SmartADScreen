package cn.com.smartadscreen.main.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.List;

import butterknife.BindView;
import cn.com.smartadscreen.main.ui.view.OnItemClickListener;
import cn.com.smartadscreen.main.ui.view.OnItemLongClickListener;
import cn.com.smartadscreen.model.bean.DownloadErrorCode;
import cn.com.smartadscreen.model.db.entity.DownloadTable;
import cn.com.startai.smartadh5.R;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/22
 * 作用：
 */

public class DownloadListAdapter extends CommonRecyclerAdapter<DownloadTable, DownloadListAdapter.ViewHolder> {

    public DownloadListAdapter(Context context, List<DownloadTable> list) {
        super(context, list);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, DownloadTable item) {
        double fileSize = item.getFileSizeDouble() / 1024 / 1024;
        int progress = item.getProgress();
        holder.tvFileName.setText(item.getFileName());

        holder.tvProgress.setVisibility(View.VISIBLE);
        holder.tvProgress.setText(progress + " %");

        if (progress == -1) {//文件下载失败
            holder.pbDownload.setVisibility(View.GONE);
            holder.tvFail.setVisibility(View.VISIBLE);
            holder.tvFail.setTextColor(ContextCompat.getColor(mContext, R.color.downloadError));
            holder.tvFail.setText(
                    getString(R.string.download_fail,
                            DownloadErrorCode.getDownloadMsgByCode(item.getErrorCode() == null ? 0 : item.getErrorCode())));
            holder.tvProgress.setVisibility(View.GONE);
        } else if(progress == 100) {
            holder.pbDownload.setVisibility(View.GONE);
            holder.tvFail.setVisibility(View.VISIBLE);
            holder.tvFail.setTextColor(0xffffffff);
            holder.tvFail.setText(
                    getString(R.string.download_suc));
        }else {
            holder.tvFail.setVisibility(View.GONE);
            holder.pbDownload.setVisibility(View.VISIBLE);
            holder.pbDownload.setProgress(progress);
        }
        holder.tvOrigin.setText(item.getFileOriginDescribeResId());
        holder.tvSize.setText(getString(R.string.file_size, fileSize));
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, OnItemClickListener itemClickListener,
                                            OnItemLongClickListener itemLongClickListener) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_download, parent, false), itemClickListener, itemLongClickListener);
    }

    class ViewHolder extends BaseRecyclerViewHolder {
        @BindView(R.id.tv_file_name)
        TextView tvFileName;
        @BindView(R.id.pb_download)
        ProgressBar pbDownload;
        @BindView(R.id.tv_size)
        TextView tvSize;
        @BindView(R.id.tv_fail)
        TextView tvFail;
        @BindView(R.id.tv_progress)
        TextView tvProgress;
        @BindView(R.id.tv_origin)
        TextView tvOrigin;

        ViewHolder(@NonNull View v, OnItemClickListener listener,
                          OnItemLongClickListener itemLongClickListener) {
            super(v, listener, itemLongClickListener);
        }
    }
}
