package matugr.authorization_request.domain

import kotlinx.coroutines.CancellableContinuation
import matugr.authorization_request.AuthorizationResult

internal data class PendingAuthorization(
    val authorizationContinuation: CancellableContinuation<AuthorizationResult>,
    val codeVerifier: String,
    val state: String?
)
