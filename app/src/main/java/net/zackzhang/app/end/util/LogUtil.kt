package net.zackzhang.app.end.util

import android.util.Log
import net.zackzhang.app.end.model.DataManager

object LogUtil {

    private val DEF_TAG = "____"

    private val VERBOSE = 0
    private val DEBUG = 1
    private val INFO = 2
    private val WARN = 3
    private val ERROR = 4
    private val NOTHING = 5

    //level的值是多少，就打印对应常量级别以上的日志
    private var level = VERBOSE

    fun v(tag: String, msg: String) {
        if (level <= VERBOSE) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (level <= DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun d(tag: String, msg: Int) {
        if (level <= DEBUG) {
            Log.d(tag, msg.toString())
        }
    }

    fun i(tag: String, msg: String) {
        if (level <= INFO) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (level <= WARN) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (level <= ERROR) {
            Log.e(tag, msg)
        }
    }

    fun d(msg: String) {
        d(DEF_TAG, msg)
    }

    fun d(msg: Int) {
        d(DEF_TAG, msg)
    }

    fun here() {
        d("****HERE****")
    }

    fun logAllSharedPreferences() {
        d(DataManager.preferenceHelper.allValues.toString())
    }
}
