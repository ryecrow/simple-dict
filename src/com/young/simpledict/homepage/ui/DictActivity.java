package com.young.simpledict.homepage.ui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.young.common.YLog;
import com.young.droidinject.Inject;
import com.young.droidinject.InjectView;
import com.young.simpledict.R;
import com.young.simpledict.detailpage.ui.DetailFragment;
import com.young.simpledict.dict.DictAdapter;
import com.young.simpledict.dict.model.DictDetail;
import com.young.simpledict.service.event.SearchWordRequest;
import com.young.simpledict.service.event.SearchWordResponse;
import de.greenrobot.event.EventBus;

public class DictActivity extends FragmentActivity
        implements View.OnClickListener, SensorEventListener {
    private static final String TAG = "DictActivity";

    @InjectView(R.id.search_box)
    EditText mSearchBox;

    @InjectView(R.id.search_button)
    Button mSearchButton;

    private DetailFragment mDictDetailFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dict_activity);
        Inject.inject(this);
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
                break;
            default:

        }
    }

    public void onEventMainThread(@NonNull SearchWordResponse response) {
        DictDetail info = response.dictDetail;
        mDictDetailFragment.setData(info);
        YLog.i(TAG, info.word);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
