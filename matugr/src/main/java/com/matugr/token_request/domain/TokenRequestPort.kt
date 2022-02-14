package com.matugr.token_request.domain

import com.matugr.token_request.external.TokenGrantType
import com.matugr.token_request.external.TokenResult

/**
 * Domain class that separates token requests from other OAuth-related requests
 */
internal interface TokenRequestPort {
    suspend fun performTokenRequest(tokenUrl: String,
                                    clientId: String,
                                    redirectUri: String,
                                    tokenGrantType: TokenGrantType,
                                    customParameters: Map<String, String> = emptyMap()
    ): TokenResult
}