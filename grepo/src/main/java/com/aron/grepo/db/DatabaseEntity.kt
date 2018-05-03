package com.aron.grepo.db

import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.RepositoryModel
import io.reactivex.Observable

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */

interface DatabaseEntity {

    fun save(entity: ApiRepository): Observable<RepositoryModel>
    fun saveAll(list: List<ApiRepository>): Observable<List<RepositoryModel>>
    fun read(entityId: String): Observable<RepositoryModel>
    fun readAll(): Observable<List<RepositoryModel>>
    fun readBatch(firstIndex: Int, lastIndex: Int): Observable<List<RepositoryModel>>
}
