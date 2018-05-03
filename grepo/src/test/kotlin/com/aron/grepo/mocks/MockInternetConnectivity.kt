package com.aron.grepo.mocks

import com.aron.grepo.network.InternetConnectivity
import io.reactivex.Observable

/**
 * @author Georgel Aron
 * @since 03/05/2018
 * @version 1.0.0
 */
class MockInternetConnectivity: InternetConnectivity {

    override fun connectionStatus(): Observable<InternetConnectivity.InternetConnectivityStatus> =
            Observable.just(InternetConnectivity.InternetConnectivityStatus.CONNECTED)

}