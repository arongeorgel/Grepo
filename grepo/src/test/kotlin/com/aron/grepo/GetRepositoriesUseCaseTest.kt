package com.aron.grepo

import com.aron.grepo.mocks.MockDatabaseEntity
import com.aron.grepo.mocks.MockInternetConnectivity
import com.aron.grepo.models.ApiRepository
import com.aron.grepo.repositories.GetRepositoriesUseCase
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Rule
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.mock.MockRetrofit

/**
 * @author Georgel Aron
 * @since 03/05/2018
 * @version 1.0.0
 */
class GetRepositoriesUseCaseTest {

    companion object {
        const val HTTP_ERROR_CODE: Int = 500
    }

    private val apiResponse = listOf(
            ApiRepository("1", "abc"),
            ApiRepository("2", "def")
    )
    private val responseSuccess = Observable.just(
            Response.success(apiResponse)
    )
    private val responseError = Observable.just(
            Response.error<List<ApiRepository>>(
                    HTTP_ERROR_CODE, ResponseBody.create(
                    MediaType.parse("APPLICATION_JSON"),
                    "{\"error\" = \"Server error\"}")
            )
    )

    @Rule
    @JvmField
    val mockitoRule = MockitoJUnit.rule()


    fun beforeEach() {
        val useCase = GetRepositoriesUseCase(MockDatabaseEntity(),
                MockInternetConnectivity(),
                mock(Retrofit::class.java))
    }

    fun whenAskToGetFreshDataFromNetworkShouldSucceed() {
        
    }
}