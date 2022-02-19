package com.matugr.sample.data.ui

sealed class UiToken {
    object AuthorizationError: UiToken()
    data class TokenSuccess(val expiry: Long): UiToken()
    data class TokenError(val errorAsString: String): UiToken()
    data class NoRefreshToken(val expiry: Long): UiToken()
    object LocalTokenInvalid: UiToken()
}
