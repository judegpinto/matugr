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