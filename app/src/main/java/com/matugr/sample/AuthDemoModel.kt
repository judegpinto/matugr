package com.matugr.sample

import android.util.Log
import com.matugr.authorization_request.external.AuthorizationRequest
import com.matugr.authorization_request.external.AuthorizationResponseType
import com.matugr.authorization_request.external.AuthorizationResult
import com.matugr.common.external.RequestPort
import com.matugr.common.external.UrlLauncherPort
import com.matugr.sample.data.domain.AuthDemoProperties
import com.matugr.sample.data.ui.UiToken
import com.matugr.sample.persistence.AuthDao
import com.matugr.sample.persistence.TokenInfo
import com.matugr.token_request.external.TokenGrantType
import com.matugr.token_request.external.TokenResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Core business logic for the sample app. Handles authorization and token requests, saving and
 * retrieving local tokens, as well as processing any corresponding errors.
 */
class AuthDemoModel @Inject constructor(
    private val authDemoProperties: AuthDemoProperties,
    private val requestPort: RequestPort,
    private val authDao: AuthDao
) {

    suspend fun launchAuthorizationForToken(urlLauncherPort: UrlLauncherPort): UiToken {
        return withContext(Dispatchers.IO) {
            val authRequest = AuthorizationRequest(
                authDemoProperties.authUrl,
                authDemoProperties.clientId,
                AuthorizationResponseType.Code,
                authDemoProperties.redirectUri,
                authDemoProperties.scope,
                customParameters = mapOf("additional_param" to "true")
            )
            val authorizationResponse = requestPort.performAuthorizationRequest(
                authRequest,
                urlLauncherPort)
            Log.d(javaClass.simpleName, "Authorization Response: $authorizationResponse")

            when(authorizationResponse) {
                is AuthorizationResult.Success -> {
                    Log.d(javaClass.simpleName, "Obtaining Token from Successful Auth Response")
                    val tokenResponse = fetchAccessTokenFromCode(
                        authorizationResponse.code, authorizationResponse.codeVerifier)
                    processTokenResponse(tokenResponse)
                }
                is AuthorizationResult.Error -> {
                    Log.i(javaClass.simpleName, "error: $authorizationResponse")
                    UiToken.AuthorizationError(authorizationResponse.toString())
                }
            }
        }
    }

    suspend fun fetchAccessTokenFromRefreshToken(): UiToken {
        val tokenInfo = getCurrentToken() ?: return UiToken.LocalTokenInvalid
        val refreshToken = tokenInfo.refreshToken ?: return UiToken.NoRefreshToken(tokenInfo.expirationTime)
        val tokenResponse = requestPort.performTokenRequest(
            authDemoProperties.tokenUrl,
            authDemoProperties.clientId,
            authDemoProperties.redirectUri,
            TokenGrantType.RefreshToken(refreshToken)
        ).also { Log.i(javaClass.simpleName, "Token Response: $it") }
        return processTokenResponse(tokenResponse)
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) { authDao.deleteTokenInfo() }
    }

    fun cancelAuth() {
        requestPort.extinguishAuthFlow()
    }

    suspend fun getCurrentToken(): TokenInfo? {
        return withContext(Dispatchers.IO) { authDao.getTokenInfo() }
    }

    private suspend fun fetchAccessTokenFromCode(code: String, codeVerifier: String): TokenResult {
        return requestPort.performTokenRequest(
            authDemoProperties.tokenUrl,
            authDemoProperties.clientId,
            authDemoProperties.redirectUri,
            TokenGrantType.AuthorizationCode(code, codeVerifier))
            .also { Log.i(javaClass.simpleName, "Token Response: $it") }
    }

    private suspend fun processTokenResponse(tokenResult: TokenResult): UiToken {
        return when (tokenResult) {
            is TokenResult.Success -> {
                val expirationDate = getExpirationTime(tokenResult.expiresIn)
                withContext(Dispatchers.IO) {
                    authDao.saveToken(TokenInfo(tokenResult.accessToken, expirationDate, tokenResult.refreshToken))
                }
                UiToken.TokenSuccess(expirationDate)
            }
            is TokenResult.Error ->  {
                when(tokenResult) {
                    is TokenResult.Error.OAuthError -> UiToken.TokenError(tokenResult.toString())
                    is TokenResult.Error.HttpError -> UiToken.TokenError(tokenResult.toString())
                    is TokenResult.Error.CannotTransformTokenJson -> UiToken.TokenError(tokenResult.toString())
                    is TokenResult.Error.NoResponseBody -> UiToken.TokenError(tokenResult.toString())
                }
            }
        }
    }

    private fun getExpirationTime(expiresIn: Long): Long {
        return System.currentTimeMillis() + expiresIn * 1000
    }
}