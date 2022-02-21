package com.matugr.common.external

import com.matugr.authorization_request.external.AuthorizationRequestConfiguration
import com.matugr.di.AuthComponent
import com.matugr.di.DaggerAuthComponent
import okhttp3.OkHttpClient

class AuthAdapterFactory {

    /**
     * Generates a [RequestPort]
     *
     * This [RequestPort] will be a singleton, it can be re-created, but the old [RequestPort]
     * will be unusable.
     */
    fun createSingletonRequestPort(authorizationRequestConfiguration: AuthorizationRequestConfiguration,
                                   okHttpClient: OkHttpClient? = null,
                                   loadingAuthLayoutId: Int? = null
    ): RequestPort {
        return DaggerAuthComponent.builder()
            .authConfiguration(authorizationRequestConfiguration)
            .okHttpClient(okHttpClient)
            .authLayout(loadingAuthLayoutId)
            .build()
            .also {
                authModelComponent = it
            }.provideAuthPort()
    }

    companion object {
        private var authModelComponent: AuthComponent? = null

        internal fun getComponent(): AuthComponent? {
            return authModelComponent
        }
    }
}