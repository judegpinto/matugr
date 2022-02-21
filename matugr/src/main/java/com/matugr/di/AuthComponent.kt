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