package com.quest.countryinfo

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

class CountryInfoController : Application() {
    private var mRequestQueue: RequestQueue? = null
    private var mImageLoader: ImageLoader? = null

    val countryInfoRequestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }
            return mRequestQueue
        }

    val imageLoader: ImageLoader
        get() {
            countryInfoRequestQueue
            if (mImageLoader == null) {
                mImageLoader = ImageLoader(
                    this.mRequestQueue,
                    LruBitmapCache()
                )
            }
            return this.mImageLoader!!
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        countryInfoRequestQueue?.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        countryInfoRequestQueue?.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    companion object {
        private val TAG = CountryInfoController::class.java.simpleName
        @get:Synchronized var instance: CountryInfoController? = null
            private set
    }
}
