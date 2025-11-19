package com.devansh.crikstats

import android.app.Application
import com.devansh.crikstats.di.AppComponent
import com.devansh.crikstats.di.DaggerAppComponent
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CrikStatsApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        SplitCompat.install(this)
    }
}