package com.matugr.sample.di

import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import com.matugr.authorization_request.external.AuthRequestConfiguration
import com.matugr.common.external.AuthAdapterFactory
import com.matugr.common.external.RequestPort
import com.matugr.common.external.UriCharacter
import com.matugr.sample.BuildConfig
import com.matugr.sample.R
import com.matugr.sample.data.domain.AuthDemoProperties
import com.matugr.sample.persistence.AuthDao
import com.matugr.sample.persistence.AuthDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * The only Hilt-leveraged module in the sample app. Contains dependency-injected items. Other
 * items that are injected may not be here, e.g. ViewModels have inject-annotated constructors.
 */
@Module
@InstallIn(ViewModelComponent::class, ActivityComponent::class)
class AuthDemoModule {
    private val authDatabaseName = "auth_database"

    @Provides
    fun provideAuthProperties(): AuthDemoProperties {
        return AuthDemoProperties(
            BuildConfig.authUrl,
            BuildConfig.tokenUrl,
            BuildConfig.clientId,
            BuildConfig.redirectUri,
            BuildConfig.scope
        )
    }

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    fun provideAuthConfiguration(): AuthRequestConfiguration {
        return AuthRequestConfiguration(UriCharacter.ParametersIdentifier.Query)
    }

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    fun provideAuthPort(authRequestConfiguration: AuthRequestConfiguration, okHttpClient: OkHttpClient): RequestPort {
        return AuthAdapterFactory().createSingletonRequestPort(authRequestConfiguration, okHttpClient, R.layout.auth_loading)
    }

    @Provides
    fun provideAuthDatabase(@ApplicationContext context: Context): AuthDatabase {
        return Room.databaseBuilder(context, AuthDatabase::class.java, authDatabaseName).build()
    }

    @Provides
    fun provideAuthDao(authDatabase: AuthDatabase): AuthDao {
        return authDatabase.authDao()
    }
}