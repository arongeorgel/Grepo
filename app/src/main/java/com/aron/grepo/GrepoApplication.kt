package com.aron.grepo

import android.app.Application
import io.realm.Realm
import timber.log.Timber

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
class GrepoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Timber.plant(Timber.DebugTree())
    }
}