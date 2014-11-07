package com.young.simpledict.detailpage.ui;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.young.common.YLog;
import com.young.simpledict.R;
import com.young.simpledict.dict.model.AudioSentence;
import com.young.simpledict.dict.model.DictDetail;
import com.young.simpledict.dict.model.DictExplain;
import com.young.simpledict.dict.model.TranslateSentence;
import com.young.simpledict.dict.model.Wiki;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author: taylorcyang
 * Date:   2014-10-25
 * Time:   16:01
 * Life with passion. Code with creativity!
 */
public class DetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "DetailFragment";

    private ScrollView mRootScrollView;
    private LinearLayout mRootContainer;
    private LayoutInflater mInflater;
    private DictDetail mDictDetail;
    private SpeakersListener mSpeakersListener;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mSpeakersListener.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSpeakersListener.stopPlaying();
        mSpeakersListener.unRegister();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        mRootScrollView = (ScrollView) inflater.inflate(R.layout.detail_fragment, container, false);
        mRootContainer = (LinearLayout) mRootScrollView.findViewById(R.id.dict_fragment_container);
        mSpeakersListener = new SpeakersListener();
        return mRootScrollView;
    }

    public void setData(DictDetail dictDetail) {
        if (dictDetail == null) {
            throw new NullPointerException("parameter can't be null!");
        }
        clearCards();
        mDictDetail = dictDetail;
        if (addHeader()) {
            addDictExplain();
            addAudioSentence();
            addTranslateSentence();
            addWiki();
        }
    }

    private void addDictExplain() {

    }

    private boolean addHeader() {
        CardView header = (CardView) mInflater.inflate(R.layout.dict_header, mRootContainer, false);
        LinearLayout root = (LinearLayout) header.findViewById(R.id.dict_detail_root);
        TextView word = (TextView) header.findViewById(R.id.dict_header_word);
        View engphonic = header.findViewById(R.id.dict_header_engphonetic);
        View cnphonic = header.findViewById(R.id.dict_header_cnphonetic);

        if (TextUtils.isEmpty(mDictDetail.word)) {
            word.setText(getActivity().getString(R.string.word_not_found_tip));
            engphonic.setVisibility(View.GONE);
            mRootContainer.addView(header);
            return false;
        }

        word.setText(mDictDetail.word);
        if (mDictDetail.pinyin != null &&
                (mDictDetail.ukphonetic == null && mDictDetail.usphonetic == null)) {
            //display only chinese
            YLog.i(TAG, "display only chinese");
            engphonic.setVisibility(View.GONE);
            cnphonic.setVisibility(View.VISIBLE);
            ((TextView) header.findViewById(R.id.cnphonetic)).setText(mDictDetail.pinyin);
        } else {
            YLog.i(TAG, "display english");
            TextView us = (TextView) root.findViewById(R.id.dict_header_usphonetic);
            TextView uk = (TextView) root.findViewById(R.id.dict_header_ukphonetic);
            boolean showUs = !TextUtils.isEmpty(mDictDetail.usphonetic);
            boolean showUk = !TextUtils.isEmpty(mDictDetail.ukphonetic);

            if (showUs) {
                us.setText(mDictDetail.usphonetic);
                mSpeakersListener.addListenerToView(root.findViewById(R.id.dict_header_us_speech), mDictDetail.usspeech);
            } else {
                root.findViewById(R.id.us_speech_layout).setVisibility(View.GONE);
            }

            if (showUk) {
                mSpeakersListener.addListenerToView(root.findViewById(R.id.dict_header_uk_speech), mDictDetail.ukspeech);
                uk.setText(mDictDetail.ukphonetic);
            } else {
                root.findViewById(R.id.uk_speech_layout).setVisibility(View.GONE);
            }

            if (showUk && showUs) {
                us.measure(0, 0);
                uk.measure(0, 0);
                int width = Math.max(us.getMeasuredWidth(), uk.getMeasuredWidth()) +
                        (int) getActivity().getResources().getDimension(R.dimen.phonetic_padding);
                us.setWidth(width);
                uk.setWidth(width);
            }
        }

        for (DictExplain de : mDictDetail.explains) {
            for (String tr : de.trs) {
                TextView w = (TextView) mInflater.inflate(R.layout.word_single_textview, root, false);
                w.setText(tr);
                root.addView(w);
            }
            for (DictExplain.WF wf : de.wfs) {
                View ll = mInflater.inflate(R.layout.word_exp_lineatlayout, root, false);
                ((TextView) ll.findViewById(R.id.word_attr)).setText(wf.name);
                ((TextView) ll.findViewById(R.id.word_explain)).setText(wf.value);
                root.addView(ll);
            }
        }
        mRootContainer.addView(header);
        return true;
    }

    private void addTranslateSentence() {
        if (mDictDetail.translateSentences.isEmpty()) {
            return;
        }
        Map<String, ArrayList<TranslateSentence>> map = new TreeMap<String, ArrayList<TranslateSentence>>();

        for (TranslateSentence ts : mDictDetail.translateSentences) {
            if (TextUtils.isEmpty(ts.source)) ts.source = getActivity().getString(R.string.unknown_dict);
            if (!map.containsKey(ts.source)) {
                map.put(ts.source, new ArrayList<TranslateSentence>());
            }
            map.get(ts.source).add(ts);
        }

        ViewGroup root = (ViewGroup) mInflater.inflate(R.layout.dict_translate_sentence, mRootContainer, false);
        ViewGroup rootContainer = (ViewGroup) root.findViewById(R.id.dict_translate_sentence_container);

        for (String key : map.keySet()) {
            ViewGroup sourceContainer = (ViewGroup) mInflater.inflate(R.layout.dict_translate_sentence_source_container, root, false);
            ((TextView) sourceContainer.findViewById(R.id.source_name)).setText(key);
            for (TranslateSentence ts : map.get(key)) {
                ViewGroup sentence = (ViewGroup) mInflater.inflate(R.layout.translate_sentence, sourceContainer, false);
                ((TextView) sentence.findViewById(R.id.source_sentence)).setText(ts.sentence);
                ((TextView) sentence.findViewById(R.id.translated_sentence)).setText(ts.translate);
                sourceContainer.addView(sentence);
            }
            rootContainer.addView(sourceContainer);
        }
        mRootContainer.addView(root);
    }

    private void addAudioSentence() {
        if (mDictDetail.audioSentences.isEmpty()) {
            return;
        }
        ViewGroup root = (ViewGroup) mInflater.inflate(R.layout.dict_audio_sentence, mRootContainer, false);
        ViewGroup rootContainer = (ViewGroup) root.findViewById(R.id.dict_audio_sentence_container);
        for (AudioSentence as : mDictDetail.audioSentences) {
            ViewGroup ll = (ViewGroup) mInflater.inflate(R.layout.audio_sentence, rootContainer, false);
            mSpeakersListener.addListenerToView(ll.findViewById(R.id.audio_sentence_speaker), as.sentenceAudioUrl);
            ((TextView) ll.findViewById(R.id.audio_sentence_text)).setText(Html.fromHtml(as.sentence));
            rootContainer.addView(ll);
        }
        mRootContainer.addView(root);
    }

    private void addWiki() {
        if (mDictDetail.wiki == null) {
            return;
        }
        Wiki wiki = mDictDetail.wiki;
        ViewGroup root = (ViewGroup) mInflater.inflate(R.layout.dict_wiki, mRootContainer, false);
        ViewGroup rootContainer = (ViewGroup) root.findViewById(R.id.dict_wiki_container);
        TextView sourceName = (TextView) rootContainer.findViewById(R.id.dict_wiki_source);
        sourceName.setText(wiki.sourceName);
        final String url = wiki.sourceURL;
        sourceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        for (String summary : wiki.summary) {
            TextView sum = (TextView) mInflater.inflate(R.layout.dict_wiki_summary, root, false);
            sum.setText(Html.fromHtml(summary));
            rootContainer.addView(sum);
        }
        mRootContainer.addView(root);
    }

    private void clearCards() {
        mRootContainer.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:

        }
    }
}
