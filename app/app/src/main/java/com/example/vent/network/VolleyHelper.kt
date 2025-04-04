package com.example.vent.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyHelper private constructor(context: Context?) {

    companion object {
        @Volatile
        private var INSTANCE: VolleyHelper? = null

        fun getInstance(context: Context?) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleyHelper(context).also { INSTANCE = it }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context?.applicationContext)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }
}
