package com.aron.grepo.mocks

import com.aron.grepo.db.DatabaseEntity
import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.RepositoryModel
import io.reactivex.Observable

/**
 * @author Georgel Aron
 * @since 03/05/2018
 * @version 1.0.0
 */
class MockDatabaseEntity : DatabaseEntity {

    override fun save(entity: ApiRepository): Observable<RepositoryModel> =
            Observable.just(RepositoryModel())

    override fun saveAll(list: List<ApiRepository>): Observable<List<RepositoryModel>> {
        val mockList = listOf(RepositoryModel(), RepositoryModel(), RepositoryModel())
        return Observable.just(mockList)
    }

    override fun read(entityId: String): Observable<RepositoryModel> {
        return Observable.just(RepositoryModel())
    }

    override fun readAll(): Observable<List<RepositoryModel>> {
        val mockList = listOf(RepositoryModel(), RepositoryModel(), RepositoryModel())
        return Observable.just(mockList)
    }

    override fun readBatch(firstIndex: Int, lastIndex: Int): Observable<List<RepositoryModel>> {
        val mockList = listOf(RepositoryModel(), RepositoryModel(), RepositoryModel())
        return Observable.just(mockList)
    }
}