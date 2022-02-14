package com.matugr.di

import com.matugr.common.tools.DispatcherProvider
import com.matugr.token_request.networking.OAuthTokenErrorBody
import com.matugr.token_request.networking.OkHttpTokenNetworking
import com.matugr.token_request.networking.TokenResponseBody
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named

const val DEFAULT = "default"

@Module
class CommModule {

    @Provides
    internal fun provideTokenResponseJsonAdapter(moshi: Moshi): JsonAdapter<TokenResponseBody> {
        return moshi.adapter(TokenResponseBody::class.java)
    }

    @Provides
    internal fun provideTokenError(moshi: Moshi): JsonAdapter<OAuthTokenErrorBody> {
        return moshi.adapter(OAuthTokenErrorBody::class.java)
    }

    @Named(DEFAULT)
    @Provides
    fun provideDefaultOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    internal fun provideTokenNetworkRequestImpl(
        @Named(DEFAULT) defaultOkHttpClient: OkHttpClient,
        clientProvidedOkHttpClient: OkHttpClient?,
        dispatcherProvider: DispatcherProvider
    ): OkHttpTokenNetworking {
        val okHttpClient = clientProvidedOkHttpClient ?: defaultOkHttpClient
        return OkHttpTokenNetworking(okHttpClient, dispatcherProvider)
    }
}