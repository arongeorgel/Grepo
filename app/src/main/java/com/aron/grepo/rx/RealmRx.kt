package com.aron.grepo.rx

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.realm.Realm
import io.realm.RealmObject
import io.realm.exceptions.RealmException
import java.util.concurrent.atomic.AtomicBoolean
import io.realm.RealmList
import io.realm.RealmResults
import io.realm.log.RealmLog
import timber.log.Timber


/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
abstract class OnSubscribeRealm<T> : Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {
        val realm = Realm.getDefaultInstance()

        val disposable = Listener()
        observer.onSubscribe(disposable)

        val entity: T
        realm.beginTransaction()
        try {
            entity = get(realm)
            observer.onNext(entity)
            realm.commitTransaction()
        } catch (e: Throwable) {
            if (realm.isInTransaction) {
                realm.cancelTransaction()
            } else {
                Timber.w("Could not cancel transaction, not currently in a transaction.")
            }
            Timber.w(e, "Failed to execute transaction")
            observer.onError(RealmException("Error during transaction.", e))
        } finally {
            realm.close()
        }
    }

    internal class Listener : Disposable {

        private val unsubscribed = AtomicBoolean(false)

        override fun isDisposed(): Boolean = unsubscribed.get()

        override fun dispose() {
            unsubscribed.compareAndSet(false, true)
        }

    }

    abstract fun get(realm: Realm): T
}

object RealmRx {

    fun <T : RealmObject> executeEntity(function: Function<Realm, T>): Observable<T> =
            object : OnSubscribeRealm<T>() {
                override fun get(realm: Realm): T = function.apply(realm)
            }

    fun <T : RealmObject> executeList(function: Function<Realm, List<T>>): Observable<List<T>> =
            object : OnSubscribeRealm<List<T>>() {
                override fun get(realm: Realm): List<T> = function.apply(realm)
            }

    fun <T : RealmObject> getResults(function: Function<Realm, RealmResults<T>>): Observable<RealmResults<T>> =
            object : OnSubscribeRealm<RealmResults<T>>() {
                override fun get(realm: Realm): RealmResults<T> = function.apply(realm)
            }
}
