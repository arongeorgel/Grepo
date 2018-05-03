package com.aron.grepo.db

import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.RepositoryModel
import com.aron.grepo.rx.RealmRx
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import javax.inject.Inject

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
class RepositoryEntity @Inject constructor() : DatabaseEntity {

    override fun readBatch(firstIndex: Int, lastIndex: Int): Observable<List<RepositoryModel>> = RealmRx
            .getResults(Function<Realm, RealmResults<RealmRepository>> {
                it.where(RealmRepository::class.java).findAll()
            })
            .map {
                if (it.size > 0) {
                    realmModelListToUiModelList(it.slice(IntRange(firstIndex, lastIndex)))
                } else {
                    realmModelListToUiModelList(it)
                }
            }

    override fun read(entityId: String): Observable<RepositoryModel> = RealmRx
            .executeEntity(Function {
                it.where(RealmRepository::class.java)
                        .equalTo("uid", entityId)
                        .findFirst()
            })
            .map { realmModelToUiModel(it as RealmRepository) }

    override fun readAll(): Observable<List<RepositoryModel>> = RealmRx
            .getResults(Function<Realm, RealmResults<RealmRepository>> {
                it.where(RealmRepository::class.java).findAll()
            })
            .map { realmModelListToUiModelList(it) }

    override fun save(entity: ApiRepository): Observable<RepositoryModel> = RealmRx
            .executeEntity(Function {
                it.copyToRealmOrUpdate(apiModelToRealmModel(entity))
            })
            .map { realmModelToUiModel(it as RealmRepository) }

    override fun saveAll(list: List<ApiRepository>): Observable<List<RepositoryModel>> = RealmRx
            .executeList(Function<Realm, List<RealmRepository>> { realm ->
                realm.copyToRealmOrUpdate(apiModelListToRealmModelList(list))
            })
            .map {
                realmModelListToUiModelList(it)
            }

    private fun apiModelToRealmModel(apiModel: ApiRepository): RealmRepository = RealmRepository(
            uid = apiModel.id.toLong(),
            name = apiModel.name,
            description = apiModel.description,
            lastUpdate = apiModel.lastUpdate
    )

    private fun realmModelToUiModel(realmRepository: RealmRepository): RepositoryModel = RepositoryModel(
            name = realmRepository.name,
            description = realmRepository.description,
            isFavorite = realmRepository.isFavorite,
            lastUpdate = realmRepository.lastUpdate
    )

    private fun apiModelListToRealmModelList(list: List<ApiRepository>): RealmList<RealmRepository> {
        val realmList: RealmList<RealmRepository> = RealmList()
        list.forEach {
            val realmRepo = RealmRepository(
                    uid = it.id.toLong(),
                    name = it.name,
                    description = it.description,
                    lastUpdate = it.lastUpdate
            )

            realmList.add(realmRepo)
        }
        return realmList
    }

    private fun realmModelListToUiModelList(list: List<RealmRepository>): List<RepositoryModel> {
        val uiList: ArrayList<RepositoryModel> = arrayListOf()
        list.forEach {
            uiList.add(RepositoryModel(
                    name = it.name,
                    description = it.description,
                    isFavorite = it.isFavorite,
                    lastUpdate = it.lastUpdate
            ))
        }
        return uiList
    }
}

open class RealmRepository @JvmOverloads constructor(
        @PrimaryKey
        var uid: Long = 0,
        var name: String = "",
        var description: String? = "",
        var isFavorite: Boolean = false,
        var lastUpdate: String = ""
) : RealmObject() {

    override fun toString(): String {
        return "RealmRepository(uid=$uid, name='$name', description='$description', " +
                "isFavorite=$isFavorite, lastUpdate='$lastUpdate')"
    }
}
