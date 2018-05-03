package com.aron.grepo

import android.content.Context
import android.net.ConnectivityManager
import com.aron.grepo.network.InternetConnectivity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */
class InternetConnectivityImpl @Inject constructor(
        private val context: Context
) : InternetConnectivity {

    override fun connectionStatus(): Observable<InternetConnectivity.InternetConnectivityStatus> {
        val connectivityManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected

        return Observable.just(
                if (isConnected) InternetConnectivity.InternetConnectivityStatus.CONNECTED
                else InternetConnectivity.InternetConnectivityStatus.DISCONNECTED
        )
    }

}