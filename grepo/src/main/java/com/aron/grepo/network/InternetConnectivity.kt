package com.aron.grepo.network

import io.reactivex.Observable

/**
 * @author Georgel Aron
 * @since 01/05/2018
 * @version 1.0.0
 */
interface InternetConnectivity {

    enum class InternetConnectivityStatus { CONNECTED, DISCONNECTED }

    fun connectionStatus(): Observable<InternetConnectivityStatus>
}
