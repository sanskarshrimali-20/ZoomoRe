package com.zoomore.reelapp.presentation

import android.app.Application
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Init Timber for logging
        Timber.plant(Timber.DebugTree())

        // Enable Firebase Crashlytics only on release build
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

    }
}