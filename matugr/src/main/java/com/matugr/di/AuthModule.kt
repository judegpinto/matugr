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

import com.matugr.authorization_request.domain.*
import com.matugr.authorization_request.domain.AuthorizationResponseManager
import com.matugr.authorization_request.domain.PendingAuthorizationStore
import com.matugr.common.external.AuthModel
import com.matugr.common.external.RequestPort
import com.matugr.token_request.networking.OkHttpTokenNetworking
import com.matugr.token_request.networking.TokenNetworking
import dagger.Binds
import dagger.Module

@Module(includes = [AuthModelModule::class, CommModule::class, ScaffoldingModule::class])
internal abstract class AuthModule {
    @Binds
    abstract fun provideRequestPort(authModel: AuthModel): RequestPort

    @Binds
    abstract fun provideAuthRedirectPort(
        authorizationResponseManager: AuthorizationResponseManager
    ): AuthRedirectPort

    @Binds
    abstract fun providePendingAuthorizationStore(
        authorizationResponseManager: AuthorizationResponseManager
    ): PendingAuthorizationStore

    @Binds
    abstract fun provideTokenNetworking(
        okHttpTokenNetworking: OkHttpTokenNetworking
    ): TokenNetworking

    @Binds
    abstract fun provideAuthorizationResultManufacturer(
        authorizationResultManufacturerImpl: AuthorizationResultManufacturerImpl
    ): AuthorizationResultManufacturer
}