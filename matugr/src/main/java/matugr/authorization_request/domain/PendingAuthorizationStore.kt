package matugr.authorization_request.domain

internal interface PendingAuthorizationStore {
    fun storePendingAuthorization(pendingAuthorization: PendingAuthorization)
    fun cancelPendingAuthorization()
}
