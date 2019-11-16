package com.young.simpledict.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.young.simpledict.R
import com.young.simpledict.dict.DictAdapter
import com.young.simpledict.service.event.BaseEvent
import com.young.simpledict.service.event.SearchWordRequest
import com.young.simpledict.service.event.SearchWordResponse
import com.young.simpledict.ui.detail.DetailFragment
import kotlinx.android.synthetic.main.dict_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mSearchBox: EditText = search_box

    private var mSearchButton: Button = search_button

    internal var mWaitingProgressbar: ProgressBar = waiting_progressbar

    private val mProgressBarOperator = ProgressBarOperator()

    private var mDictDetailFragment: DetailFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dict_activity)
        setSupportActionBar(toolbar)
        mSearchButton.setOnClickListener(this)
        mDictDetailFragment = DetailFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.dict_detail_fragment, mDictDetailFragment!!)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_button -> {
                val req = SearchWordRequest(
                    BaseEvent.EMPTY_EVENT_CODE,
                    mSearchBox.text.toString(),
                    DictAdapter.DICT_YOUDAO_DETAIL
                )
                EventBus.getDefault().post(req)
                mProgressBarOperator.show()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(response: SearchWordResponse) {
        mProgressBarOperator.dismiss()
        val info = response.dictDetail
        val s = System.currentTimeMillis()
        mDictDetailFragment!!.setData(info)
        Log.i(TAG, "setData consuming time:" + (System.currentTimeMillis() - s))
        Log.i(TAG, info?.word ?: "")
    }

    private inner class ProgressBarOperator {
        private var mLastShowTime: Long = 0
        private val mMainHandler = Handler(Looper.getMainLooper())
        private val mProgressBarCanceler = Runnable {
            mWaitingProgressbar.visibility = View.INVISIBLE
        }

        fun show() {
            mMainHandler.removeCallbacks(mProgressBarCanceler)
            if (mWaitingProgressbar.visibility != View.VISIBLE) {
                mWaitingProgressbar.visibility = View.VISIBLE
            }
            mLastShowTime = SystemClock.uptimeMillis()
        }

        fun dismiss() {
            val delayTime = MIN_SHOW_TIME - (SystemClock.uptimeMillis() - mLastShowTime)
            if (delayTime > 0) {
                mMainHandler.postDelayed(mProgressBarCanceler, delayTime)
            } else {
                mProgressBarCanceler.run()
            }
        }
    }

    companion object {
        private val TAG: String? = MainActivity::class.simpleName
        private const val MIN_SHOW_TIME = 1300L
    }
}
