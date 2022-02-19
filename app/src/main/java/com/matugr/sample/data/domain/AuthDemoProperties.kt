package com.matugr.sample.data.domain

import com.google.gson.annotations.SerializedName

data class AuthDemoProperties (
    @SerializedName("auth_url") val authUrl: String,
    @SerializedName("token_url") val tokenUrl: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("redirect_uri") val redirectUri: String,
    @SerializedName("scope") val scope: String
)