package com.young.simpledict.homepage.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.young.common.YLog;
import com.young.common.inject.Inject;
import com.young.common.inject.ViewInject;
import com.young.simpledict.R;
import com.young.simpledict.detailpage.ui.DetailFragment;
import com.young.simpledict.dict.DictAdapter;
import com.young.simpledict.dict.model.DictDetail;
import com.young.simpledict.service.event.SearchWordRequest;
import com.young.simpledict.service.event.SearchWordResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DictActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DictActivity";

    @Inject(R.id.search_box)
    EditText mSearchBox;

    @Inject(R.id.search_button)
    Button mSearchButton;

    @Inject(R.id.toolbar)
    Toolbar mToolbar;

    @Inject(R.id.waiting_progressbar)
    ProgressBar mWaitingProgressbar;

    private ProgressBarOperator mProgressBarOperator = new ProgressBarOperator();

    private DetailFragment mDictDetailFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dict_activity);
        ViewInject.doInject(this);
        setSupportActionBar(mToolbar);
        mSearchButton.setOnClickListener(this);
        mDictDetailFragment = new DetailFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.dict_detail_fragment, mDictDetailFragment)
                .commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                SearchWordRequest req = new SearchWordRequest();
                req.useDict = DictAdapter.DICT_YOUDAO_DETAIL;
                req.word = mSearchBox.getText().toString();
                EventBus.getDefault().post(req);
                mProgressBarOperator.show();
                break;
            default:

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull SearchWordResponse response) {
        mProgressBarOperator.dismiss();
        DictDetail info = response.dictDetail;
        long s = System.currentTimeMillis();
        mDictDetailFragment.setData(info);
        YLog.i(TAG, "setData consuming time:" + (System.currentTimeMillis() - s));
        YLog.i(TAG, info.word);
    }

    private class ProgressBarOperator {
        private static final long MIN_SHOW_TIME = 1300L;
        private long mLastShowTime;
        private Handler mMainHandler = new Handler(Looper.getMainLooper());
        private Runnable mProgressBarCanceler = new Runnable() {
            @Override
            public void run() {
                if (mWaitingProgressbar != null) {
                    mWaitingProgressbar.setVisibility(View.INVISIBLE);
                }
            }
        };

        public void show() {
            if (mWaitingProgressbar != null) {
                mMainHandler.removeCallbacks(mProgressBarCanceler);
                if (mWaitingProgressbar.getVisibility() != View.VISIBLE) {
                    mWaitingProgressbar.setVisibility(View.VISIBLE);
                }
                mLastShowTime = SystemClock.uptimeMillis();
            }
        }

        public void dismiss() {
            if (mWaitingProgressbar != null) {
                long delayTime = MIN_SHOW_TIME - (SystemClock.uptimeMillis() - mLastShowTime);
                if (delayTime > 0) {
                    mMainHandler.postDelayed(mProgressBarCanceler, delayTime);
                } else {
                    mProgressBarCanceler.run();
                }
            }
        }
    }
}
