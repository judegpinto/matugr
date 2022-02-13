package matugr.authorization_request.domain

import kotlinx.coroutines.CancellableContinuation
import matugr.authorization_request.external.AuthorizationResult

/**
 * Data holder for information needed to process authorization response received by app
 */
internal data class PendingAuthorization(
    val authorizationContinuation: CancellableContinuation<AuthorizationResult>,
    val codeVerifier: String,
    val state: String?
)
