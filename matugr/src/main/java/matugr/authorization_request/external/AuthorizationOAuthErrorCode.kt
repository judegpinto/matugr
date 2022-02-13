package matugr.authorization_request.external

import matugr.common.oauth.AuthorizationErrorCodeBody

enum class AuthorizationOAuthErrorCode {
    InvalidRequest,
    UnAuthorizedClient,
    AccessDenied,
    UnSupportedResponseType,
    InvalidScope,
    ServerError,
    TemporarilyUnavailable;
}

fun convertFromAuthorizationOAuthNetworkingError(
    authorizationOAuthErrorCodeBody: AuthorizationErrorCodeBody
): AuthorizationOAuthErrorCode {
    return when(authorizationOAuthErrorCodeBody) {
        AuthorizationErrorCodeBody.InvalidRequest -> AuthorizationOAuthErrorCode.InvalidRequest
        AuthorizationErrorCodeBody.UnauthorizedClient -> AuthorizationOAuthErrorCode.UnAuthorizedClient
        AuthorizationErrorCodeBody.AccessDenied -> AuthorizationOAuthErrorCode.AccessDenied
        AuthorizationErrorCodeBody.UnsupportedResponseType -> AuthorizationOAuthErrorCode.UnSupportedResponseType
        AuthorizationErrorCodeBody.InvalidScope -> AuthorizationOAuthErrorCode.InvalidScope
        AuthorizationErrorCodeBody.ServerError -> AuthorizationOAuthErrorCode.ServerError
        AuthorizationErrorCodeBody.TemporarilyUnavailable -> AuthorizationOAuthErrorCode.TemporarilyUnavailable
    }
}