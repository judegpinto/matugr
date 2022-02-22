/*
 * Copyright 2022 The Matugr Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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