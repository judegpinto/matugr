package com.matugr.sample.data.domain

import com.google.gson.annotations.SerializedName

/**
 * Setup properties needed to configure matugr and call it's API. In a typical client application,
 * these values can be provided in a different way if needed, i.e. dependency injection to
 * the requiring class construction.
 *
 * These properties are required to be provided in the sample app, but they can be empty, e.g. scope.
 */
data class AuthDemoProperties (
    @SerializedName("auth_url") val authUrl: String,
    @SerializedName("token_url") val tokenUrl: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("redirect_uri") val redirectUri: String,
    @SerializedName("scope") val scope: String
)