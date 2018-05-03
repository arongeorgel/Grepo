package com.aron.grepo

import android.app.Application
import com.aron.grepo.di.ApplicationComponent
import com.aron.grepo.di.DaggerApplicationComponent
import io.realm.Realm
import timber.log.Timber

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
class GrepoApplication : Application() {

    lateinit var appComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Timber.plant(Timber.DebugTree())

        appComponent = DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
        appComponent.inject(this)
    }
}