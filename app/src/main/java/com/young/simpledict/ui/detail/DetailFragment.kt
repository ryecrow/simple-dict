package com.young.simpledict.ui.detail


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.young.simpledict.R
import com.young.simpledict.dict.model.DictDetail
import com.young.simpledict.dict.model.TranslateSentence
import java.util.*

/**
 * Author: taylorcyang
 * Date:   2014-10-25
 * Time:   16:01
 * Life with passion. Code with creativity!
 */
class DetailFragment : Fragment(), View.OnClickListener {

    private var mRootScrollView: ScrollView? = null

    private var mRootContainer: LinearLayout? = null
    private var mInflater: LayoutInflater? = null
    private var mDictDetail: DictDetail? = null
    private var mSpeakersListener: SpeakersListener? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onResume() {
        super.onResume()
        mSpeakersListener!!.register()
    }

    override fun onPause() {
        super.onPause()
        mSpeakersListener!!.stopPlaying()
        mSpeakersListener!!.unRegister()
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mInflater = inflater
        mRootScrollView = inflater.inflate(R.layout.detail_fragment, container, false) as ScrollView
        mRootContainer =
            mRootScrollView!!.findViewById<View>(R.id.dict_fragment_container) as LinearLayout
        mSpeakersListener = SpeakersListener()
        return mRootScrollView
    }

    fun setData(dictDetail: DictDetail?) {
        if (dictDetail == null) {
            throw NullPointerException("parameter can't be null!")
        }
        clearCards()
        mDictDetail = dictDetail
        if (addHeader()) {
            addDictExplain()
            addAudioSentence()
            addTranslateSentence()
            addWiki()
        }
    }

    private fun addDictExplain() {

    }

    private fun addHeader(): Boolean {
        val header = mInflater!!.inflate(R.layout.dict_header, mRootContainer, false) as CardView
        val root = header.findViewById<View>(R.id.dict_detail_root) as LinearLayout
        val word = header.findViewById<View>(R.id.dict_header_word) as TextView
        val engphonic = header.findViewById<View>(R.id.dict_header_engphonetic)
        val cnphonic = header.findViewById<View>(R.id.dict_header_cnphonetic)

        if (TextUtils.isEmpty(mDictDetail!!.word)) {
            word.text = activity!!.getString(R.string.word_not_found_tip)
            engphonic.visibility = View.GONE
            mRootContainer!!.addView(header)
            return false
        }

        word.text = mDictDetail!!.word
        if (mDictDetail!!.pinyin != null && mDictDetail!!.ukphonetic == null && mDictDetail!!.usphonetic == null) {
            //display only chinese
            Log.i(TAG, "display only chinese")
            engphonic.visibility = View.GONE
            cnphonic.visibility = View.VISIBLE
            (header.findViewById<View>(R.id.cnphonetic) as TextView).text = mDictDetail!!.pinyin
        } else {
            Log.i(TAG, "display english")
            val us = root.findViewById<View>(R.id.dict_header_usphonetic) as TextView
            val uk = root.findViewById<View>(R.id.dict_header_ukphonetic) as TextView
            val showUs = !TextUtils.isEmpty(mDictDetail!!.usphonetic)
            val showUk = !TextUtils.isEmpty(mDictDetail!!.ukphonetic)

            if (showUs) {
                us.text = mDictDetail!!.usphonetic
                mSpeakersListener!!.addListenerToView(
                    root.findViewById(R.id.dict_header_us_speech),
                    mDictDetail!!.usspeech
                )
            } else {
                root.findViewById<View>(R.id.us_speech_layout).visibility = View.GONE
            }

            if (showUk) {
                mSpeakersListener!!.addListenerToView(
                    root.findViewById(R.id.dict_header_uk_speech),
                    mDictDetail!!.ukspeech
                )
                uk.text = mDictDetail!!.ukphonetic
            } else {
                root.findViewById<View>(R.id.uk_speech_layout).visibility = View.GONE
            }

            if (showUk && showUs) {
                us.measure(0, 0)
                uk.measure(0, 0)
                val width = Math.max(
                    us.measuredWidth,
                    uk.measuredWidth
                ) + activity!!.resources.getDimension(R.dimen.phonetic_padding).toInt()
                us.width = width
                uk.width = width
            }
        }

        for (de in mDictDetail!!.explains) {
            for (tr in de.trs) {
                val w = mInflater!!.inflate(R.layout.word_single_textview, root, false) as TextView
                w.text = tr
                root.addView(w)
            }
            for (wf in de.wfs) {
                val ll = mInflater!!.inflate(R.layout.word_exp_lineatlayout, root, false)
                (ll.findViewById<View>(R.id.word_attr) as TextView).text = wf.name
                (ll.findViewById<View>(R.id.word_explain) as TextView).text = wf.value
                root.addView(ll)
            }
        }
        mRootContainer!!.addView(header)
        return true
    }

    private fun addTranslateSentence() {
        if (mDictDetail!!.translateSentences.isEmpty()) {
            return
        }
        val map = TreeMap<String, ArrayList<TranslateSentence>>()

        for (ts in mDictDetail!!.translateSentences) {
            if (TextUtils.isEmpty(ts.source)) ts.source =
                activity!!.getString(R.string.unknown_dict)
            if (!map.containsKey(ts.source)) {
                map[ts.source] = ArrayList()
            }
            map[ts.source]!!.add(ts)
        }

        val root = mInflater!!.inflate(
            R.layout.dict_translate_sentence,
            mRootContainer,
            false
        ) as ViewGroup
        val rootContainer =
            root.findViewById<View>(R.id.dict_translate_sentence_container) as ViewGroup

        for (key in map.keys) {
            val sourceContainer = mInflater!!.inflate(
                R.layout.dict_translate_sentence_source_container,
                root,
                false
            ) as ViewGroup
            (sourceContainer.findViewById<View>(R.id.source_name) as TextView).text = key
            for (ts in map[key]!!) {
                val sentence = mInflater!!.inflate(
                    R.layout.translate_sentence,
                    sourceContainer,
                    false
                ) as ViewGroup
                (sentence.findViewById<View>(R.id.source_sentence) as TextView).text = ts.sentence
                (sentence.findViewById<View>(R.id.translated_sentence) as TextView).text =
                    ts.translate
                sourceContainer.addView(sentence)
            }
            rootContainer.addView(sourceContainer)
        }
        mRootContainer!!.addView(root)
    }

    private fun addAudioSentence() {
        if (mDictDetail!!.audioSentences.isEmpty()) {
            return
        }
        val root =
            mInflater!!.inflate(R.layout.dict_audio_sentence, mRootContainer, false) as ViewGroup
        val rootContainer = root.findViewById<View>(R.id.dict_audio_sentence_container) as ViewGroup
        for (`as` in mDictDetail!!.audioSentences) {
            val ll = mInflater!!.inflate(R.layout.audio_sentence, rootContainer, false) as ViewGroup
            mSpeakersListener!!.addListenerToView(
                ll.findViewById(R.id.audio_sentence_speaker),
                `as`.sentenceAudioUrl
            )
            (ll.findViewById<View>(R.id.audio_sentence_text) as TextView).text =
                Html.fromHtml(`as`.sentence)
            rootContainer.addView(ll)
        }
        mRootContainer!!.addView(root)
    }

    private fun addWiki() {
        if (mDictDetail!!.wiki == null) {
            return
        }
        val wiki = mDictDetail!!.wiki
        val root = mInflater!!.inflate(R.layout.dict_wiki, mRootContainer, false) as ViewGroup
        val rootContainer = root.findViewById<View>(R.id.dict_wiki_container) as ViewGroup
        val sourceName = rootContainer.findViewById<View>(R.id.dict_wiki_source) as TextView
        sourceName.text = wiki.sourceName
        val url = wiki.sourceURL
        sourceName.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        for (summary in wiki.summary) {
            val sum = mInflater!!.inflate(R.layout.dict_wiki_summary, root, false) as TextView
            sum.text = Html.fromHtml(summary)
            rootContainer.addView(sum)
        }
        mRootContainer!!.addView(root)
    }

    private fun clearCards() {
        mRootContainer!!.removeAllViews()
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val TAG: String? = DetailFragment::class.simpleName
    }
}
