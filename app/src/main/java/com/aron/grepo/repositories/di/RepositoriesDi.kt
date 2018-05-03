package com.aron.grepo.repositories.di

import com.aron.grepo.InternetConnectivityImpl
import com.aron.grepo.db.DatabaseEntity
import com.aron.grepo.db.RepositoryEntity
import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.RepositoryModel
import com.aron.grepo.network.InternetConnectivity
import com.aron.grepo.repositories.RepositoriesActivity
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class RepoScope

@RepoScope
@Subcomponent(modules = [RepositoriesBindsModule::class, RepositoriesProvidesModule::class])
interface RepositoriesComponent {

    fun inject(activity: RepositoriesActivity)

    @Subcomponent.Builder
    interface Builder {

        fun build(): RepositoriesComponent

        @BindsInstance
        fun activity(repositoriesActivity: RepositoriesActivity): Builder
    }
}

@Module
interface RepositoriesBindsModule {

    @RepoScope
    @Binds
    fun bindsRepositoriesEntity(it: RepositoryEntity): DatabaseEntity

    @RepoScope
    @Binds
    fun bindsInternetConnectivityImpl(it: InternetConnectivityImpl): InternetConnectivity

}

@Module
class RepositoriesProvidesModule {

}
