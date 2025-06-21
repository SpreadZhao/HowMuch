package com.spread.howmuch_android

import android.app.Application
import android.content.Context
import com.spread.common.HowMuch

class HowMuchApplication : Application() {

    init {
        HowMuch.application = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

}