package com.matugr.authorization_request.domain

internal class FakePendingAuthorizationStore: PendingAuthorizationStore {

    private var pendingAuthorization: PendingAuthorization? = null
    private val pendingAuthorizationHistory: ArrayList<PendingAuthorization> = arrayListOf()

    override fun storePendingAuthorization(pendingAuthorization: PendingAuthorization) {
        this.pendingAuthorization = pendingAuthorization
    }

    override fun cancelPendingAuthorization() {
        pendingAuthorization?.apply {
            authorizationContinuation.cancel()
            pendingAuthorizationHistory.add(this)
        }
        pendingAuthorization = null
    }

    fun pendingAuthorizationStored(): Boolean {
        return pendingAuthorization != null || pendingAuthorizationHistory.isNotEmpty()
    }

    fun pendingAuthorizationCancelled(): Boolean {
        return pendingAuthorizationHistory.isNotEmpty()
    }
}