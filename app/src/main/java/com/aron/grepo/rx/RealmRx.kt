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


/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
abstract class OnSubscribeRealm<T> : Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {
        val realm = Realm.getDefaultInstance()

        val disposable = Listener(observer, realm)
        observer.onSubscribe(disposable)

        val entity: T
        realm.beginTransaction()
        try {
            entity = get(realm)
            realm.commitTransaction()
            observer.onNext(entity)
        } catch (e: RuntimeException) {
            realm.cancelTransaction()
            observer.onError(RealmException("Error during transaction.", e))
        }
    }

    internal class Listener<T>(
            private val observer: Observer<in T>,
            private val realm: Realm
    ) : Disposable {

        private val unsubscribed = AtomicBoolean()

        override fun isDisposed(): Boolean = unsubscribed.get()

        override fun dispose() {
            if (unsubscribed.compareAndSet(false, true)) {
                try {
                    realm.close()
                } catch (e: Exception) {
                    observer.onError(e)
                }
            }
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
