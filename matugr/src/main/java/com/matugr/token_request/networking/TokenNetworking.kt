package com.matugr.token_request.networking

/**
 * Networking API that separates OAuth-specific logic from networking implementation
 */
internal interface TokenNetworking {
    suspend fun tokenRequestWithCode(
        tokenUrl: String,
        authorizationCode: String,
        clientId: String,
        codeVerifier: String,
        redirectUri: String,
        customParameters: Map<String, String>): TokenNetworkingResponse

    suspend fun tokenRequestWithRefreshToken(
        tokenUrl: String,
        refreshToken: String,
        clientId: String,
        customParameters: Map<String, String>): TokenNetworkingResponse
}
