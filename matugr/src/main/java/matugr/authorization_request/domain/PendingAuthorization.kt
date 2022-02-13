package matugr.authorization_request.domain

import kotlinx.coroutines.CancellableContinuation

internal data class PendingAuthorization(
    val authorizationContinuation: CancellableContinuation<AuthorizationResult>,
    val codeVerifier: String,
    val state: String?
)
