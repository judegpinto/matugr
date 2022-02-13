package com.matugr.authorization_request.domain

/**
 * API for authorization request logic to interact with authorization response logic.
 */
internal interface PendingAuthorizationStore {
    fun storePendingAuthorization(pendingAuthorization: PendingAuthorization)
    fun cancelPendingAuthorization()
}
