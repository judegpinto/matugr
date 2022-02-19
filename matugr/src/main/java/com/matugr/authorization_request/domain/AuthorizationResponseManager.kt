package com.matugr.authorization_request.domain

import com.matugr.authorization_request.external.AuthRequestConfiguration
import com.matugr.authorization_request.external.AuthorizationResult
import com.matugr.authorization_request.networking.AuthURI
import com.matugr.common.oauth.CODE
import com.matugr.common.oauth.STATE
import timber.log.Timber
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Delegate for authorization response processing. In turn this delegates to
 *
 * DI Scoped so the same instance stores the pending authorization and process the response
 */
@Singleton
internal class AuthorizationResponseManager @Inject constructor(
    private val authorizationResultManufacturer: AuthorizationResultManufacturer
): AuthRedirectPort, PendingAuthorizationStore {

    private var pendingAuthorization: PendingAuthorization? = null

    override fun handleRedirectUri(uri: URI) {
        pendingAuthorization?.apply {
            if (authorizationContinuation.isCancelled) return
            val authorizationResponse = authorizationResultManufacturer.processAuthorizationUri(uri, state, codeVerifier)
            authorizationContinuation.resume(authorizationResponse, null)
        }
        pendingAuthorization = null
    }

    override fun storePendingAuthorization(pendingAuthorization: PendingAuthorization) {
        this.pendingAuthorization = pendingAuthorization
    }

    override fun cancelPendingAuthorization() {
        pendingAuthorization?.authorizationContinuation?.cancel()
        pendingAuthorization = null
    }

}