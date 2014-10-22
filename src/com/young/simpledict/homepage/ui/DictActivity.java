package com.young.simpledict.homepage.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.young.common.YLog;
import com.young.droidinject.Inject;
import com.young.droidinject.InjectView;
import com.young.simpledict.R;
import com.young.simpledict.dict.DictAdapter;
import com.young.simpledict.dict.model.AudioSentence;
import com.young.simpledict.dict.model.DictExplain;
import com.young.simpledict.dict.model.TranslateSentence;
import com.young.simpledict.service.event.SearchWordRequest;
import com.young.simpledict.service.event.SearchWordResponse;
import com.young.simpledict.dict.model.DictDetail;
import de.greenrobot.event.EventBus;

public class DictActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DictActivity";

    @InjectView(R.id.display_area)
    TextView mDisplayTextView;

    @InjectView(R.id.search_box)
    EditText mSearchBox;

    @InjectView(R.id.search_button)
    Button mSearchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dict_activity);
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
                req.useDict = DictAdapter.DICT_YOUDAO_DETAIL;
                req.word = mSearchBox.getText().toString();
                EventBus.getDefault().post(req);
                break;
            default:

        }
    }

    public void onEventMainThread(SearchWordResponse response) {
        DictDetail info = response.dictDetail;
        if (info != null) {
            StringBuffer sb = new StringBuffer();
            if (info.usphontic != null) {
                sb.append("US: /").append(info.usphontic).append("/<br/>");
            }
            if (info.ukphontic != null) {
                sb.append("UK: /").append(info.ukphontic).append("/<br/>");
            }
            for (DictExplain de : info.explains) {
                for (String s : de.trs) {
                    sb.append(s).append("<br/>");
                }
                for (DictExplain.WF w : de.wfs) {
                    sb.append(w.name).append(':').append(w.value).append("<br/>");
                }
            }
            for (AudioSentence as : info.audioSentences) {
                sb.append(as.audioLength).append(" ")
                        .append(as.sentence).append("<br/>\t")
                        .append(as.sentenceAudioUrl).append("<br/>");
            }
            for(TranslateSentence ts: info.translateSentences) {
                sb.append(ts.source).append(' ').append(ts.sentence).append("<br/>\t")
                        .append(ts.translate).append("<br/>");
            }

            mDisplayTextView.setText(Html.fromHtml(sb.toString()));
        }
        YLog.i(TAG, info.word);
    }
}
