package com.aron.grepo.di

import android.app.Application
import android.content.Context
import com.aron.grepo.GrepoApplication
import com.aron.grepo.repositories.di.RepoScope
import com.aron.grepo.repositories.di.RepositoriesComponent
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Scope
import javax.inject.Singleton

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */
@Singleton
@Component(modules = [AppModule::class, ApiModule::class])
interface ApplicationComponent {

    fun inject(app: GrepoApplication)

    fun repositoriesComponentBuilder(): RepositoriesComponent.Builder

    @Component.Builder
    interface Builder {

        fun build(): ApplicationComponent

        @BindsInstance
        fun application(app: Application): Builder
    }
}

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext
}

@Module
class ApiModule {

    @Provides
    @Singleton
    @Named("scheduler-io")
    fun providesIoScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}
