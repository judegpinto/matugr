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