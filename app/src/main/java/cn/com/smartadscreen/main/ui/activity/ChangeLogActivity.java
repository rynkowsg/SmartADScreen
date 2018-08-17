package cn.com.smartadscreen.main.ui.activity;

import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

import java.io.InputStream;

import butterknife.BindView;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.main.ui.base.BaseActivityV1;
import cn.com.startai.smartadh5.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public final class ChangeLogActivity extends BaseActivityV1 {

    @BindView(R.id.id_markdown_text_view)
    TextView mMarkDownText;
    private Disposable mDisposable;

    @Override
    protected boolean shouldInterceptBack() {
        return false;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_change_log;
    }

    @Override
    public void initData() {

        mDisposable = Observable.just("change_log.md")
                .observeOn(Schedulers.io())
                .map(s -> getFromAssets("change_log.md"))
                .filter(changeLog -> changeLog != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeLog ->
                        RichText.fromMarkdown(changeLog).into(mMarkDownText)
                );

    }

    @Override
    public void initView() {

    }

    @Override
    public void addViewListener() {

    }

    private String getFromAssets(String fileName) {
        try {
            InputStream is = getResources().getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();
            if (read != -1) {
                return new String(buffer);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        //检测泄露
        Application.getContext().mRefWatcher.watch(this);
    }



}
