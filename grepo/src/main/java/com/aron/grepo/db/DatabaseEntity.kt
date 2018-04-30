package com.aron.grepo.db

import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.RepositoryModel
import io.reactivex.Observable

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
interface DatabaseEntity<in T, R> {

    fun save(entity: T): Observable<R>
    fun saveAll(list: List<T>): Observable<List<R>>
    fun read(entityId: String): Observable<R>
    fun readAll(): Observable<List<R>>
    fun readBatch(firstIndex: Int, lastIndex: Int): Observable<List<R>>
}
