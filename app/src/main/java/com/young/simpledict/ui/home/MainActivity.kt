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
import androidx.appcompat.widget.Toolbar
import com.young.common.inject.Inject
import com.young.common.inject.ViewInject
import com.young.simpledict.R
import com.young.simpledict.dict.DictAdapter
import com.young.simpledict.service.event.BaseEvent
import com.young.simpledict.service.event.SearchWordRequest
import com.young.simpledict.service.event.SearchWordResponse
import com.young.simpledict.ui.detail.DetailFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), View.OnClickListener {

    @Inject(R.id.search_box)
    internal var mSearchBox: EditText? = null

    @Inject(R.id.search_button)
    internal var mSearchButton: Button? = null

    @Inject(R.id.toolbar)
    internal var mToolbar: Toolbar? = null

    @Inject(R.id.waiting_progressbar)
    internal var mWaitingProgressbar: ProgressBar? = null

    private val mProgressBarOperator = ProgressBarOperator()

    private var mDictDetailFragment: DetailFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dict_activity)
        ViewInject.doInject(this)
        setSupportActionBar(mToolbar)
        mSearchButton!!.setOnClickListener(this)
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
                    mSearchBox!!.text.toString(),
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
        Log.i(TAG, info?.word)
    }

    private inner class ProgressBarOperator {
        private var mLastShowTime: Long = 0
        private val mMainHandler = Handler(Looper.getMainLooper())
        private val mProgressBarCanceler = Runnable {
            if (mWaitingProgressbar != null) {
                mWaitingProgressbar!!.visibility = View.INVISIBLE
            }
        }

        fun show() {
            if (mWaitingProgressbar != null) {
                mMainHandler.removeCallbacks(mProgressBarCanceler)
                if (mWaitingProgressbar!!.visibility != View.VISIBLE) {
                    mWaitingProgressbar!!.visibility = View.VISIBLE
                }
                mLastShowTime = SystemClock.uptimeMillis()
            }
        }

        fun dismiss() {
            if (mWaitingProgressbar != null) {
                val delayTime = MIN_SHOW_TIME - (SystemClock.uptimeMillis() - mLastShowTime)
                if (delayTime > 0) {
                    mMainHandler.postDelayed(mProgressBarCanceler, delayTime)
                } else {
                    mProgressBarCanceler.run()
                }
            }
        }
    }

    companion object {
        private val TAG: String? = MainActivity::class.simpleName
        private const val MIN_SHOW_TIME = 1300L
    }
}
