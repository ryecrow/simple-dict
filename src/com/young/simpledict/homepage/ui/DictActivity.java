package com.young.simpledict.homepage.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.young.common.YLog;
import com.young.droidinject.Inject;
import com.young.droidinject.InjectView;
import com.young.simpledict.R;
import com.young.simpledict.service.event.SearchWordRequest;
import com.young.simpledict.service.event.SearchWordResponse;
import com.young.simpledict.service.model.DictDetail;
import de.greenrobot.event.EventBus;

public class DictActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DictActivity";

    @InjectView(R.id.search_box)
    EditText mSearchBox;

    @InjectView(R.id.search_button)
    Button mSearchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Inject.inject(this);
        mSearchButton.setOnClickListener(this);
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
                req.word = mSearchBox.getText().toString();
                EventBus.getDefault().post(req);
                break;
            default:

        }
    }

    public void onEventMainThread(SearchWordResponse response) {
        DictDetail info = response.dictDetail;
        YLog.i(TAG, info.word);
    }
}
