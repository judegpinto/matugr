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

import com.matugr.authorization_request.external.AuthorizationRequestConfiguration
import com.matugr.common.external.RequestPort
import com.matugr.ui.AuthActivity
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = [AuthModule::class, UiModule::class])
internal interface AuthComponent {

    fun inject(authActivity: AuthActivity)

    @Component.Builder
    interface Builder {
        fun build(): AuthComponent

        @BindsInstance
        fun authLayout(layout: Int?): Builder

        @BindsInstance
        fun authConfiguration(authorizationRequestConfiguration: AuthorizationRequestConfiguration): Builder

        @BindsInstance
        fun okHttpClient(okHttpClient: OkHttpClient?): Builder
    }

    fun provideAuthPort(): RequestPort
}