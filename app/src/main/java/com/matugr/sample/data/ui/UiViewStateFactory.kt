package com.matugr.sample.data.ui

import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import com.matugr.sample.R
import com.matugr.token_request.external.TokenOAuthErrorCode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class UiViewStateFactory @Inject constructor(
    private val resources: Resources
) {
    fun idle():UiViewState.Idle {
        return UiViewState.Idle(
            title = resources.getString(R.string.title_no_local_tokens),
            titleColor = Color.Blue,
            info = resources.getString(R.string.idle_state)
        )
    }

    fun authorizing(): UiViewState.Authorizing {
        return UiViewState.Authorizing(
            title = resources.getString(R.string.title_authorizing),
            titleColor = Color.Blue,
            info = resources.getString(R.string.authorizing)
        )
    }

    fun authorizationError(authorizationErrorDetail: String) = UiViewState.DisplayError(
        title = resources.getString(R.string.error_authorization),
        titleColor = Color.Red,
        errorInfo = authorizationErrorDetail,
        errorElementMap = emptyMap()
    )

    fun noLocalToken() = UiViewState.DisplayError(
        title = resources.getString(R.string.title_no_valid_token),
        titleColor = Color.Red,
        errorInfo = resources.getString(R.string.error_invalid_local_token),
        errorElementMap = emptyMap()
    )

    fun localTokenExpired(expirationTime: Long) = UiViewState.DisplayTokenExpiredError(
        title = resources.getString(R.string.title_expired_token),
        titleColor = Color.Red,
        errorInfo = resources.getString(R.string.info_expired_token),
        errorElementMap = mapOf(
            resources.getString(R.string.key_token_expiration) to formatDate(expirationTime),
        )
    )

    fun httpTokenError(errorCode: Int, errorBody: String) = UiViewState.DisplayError(
        title = resources.getString(R.string.title_http_token_error),
        titleColor = Color.Red,
        errorInfo = resources.getString(R.string.info_http_token_error),
        errorElementMap = mapOf(
            resources.getString(R.string.key_http_token_error_code) to errorCode.toString(),
            resources.getString(R.string.key_http_token_error_body) to errorBody
        )
    )

    fun illegalStateTokenError(error: String) = UiViewState.DisplayError(
        title = resources.getString(R.string.title_illegal_state_error),
        titleColor = Color.Red,
        errorInfo = error,
        errorElementMap = mapOf()
    )

    fun oAuthTokenError(
        oAuthErrorCode: TokenOAuthErrorCode,
        oAuthErrorInfo: String?,
        oAuthErrorUri: String?
    ) = UiViewState.DisplayError(
        title = resources.getString(R.string.title_oauth_token_error),
        titleColor = Color.Red,
        errorInfo = resources.getString(R.string.info_oauth_token_error),
        errorElementMap = mapOf(
            resources.getString(R.string.key_oauth_error_code) to oAuthErrorCode.toString(),
            resources.getString(R.string.key_oauth_error_description) to oAuthErrorInfo.orEmpty(),
            resources.getString(R.string.key_oauth_error_uri) to oAuthErrorUri.orEmpty()
        )
    )

    fun displayToken(expirationTime: Long): UiViewState.DisplayToken {
        return UiViewState.DisplayToken(
            title = resources.getString(R.string.title_tokens_valid),
            titleColor = Color.Green,
            moreInfo = resources.getString(R.string.info_tokens_valid),
            errorElementMap = mapOf(
                resources.getString(R.string.key_token_expiration) to formatDate(expirationTime),
            )
        )
    }

    fun noRefreshTokenError(expiry: Long): UiViewState.DisplayError {
        return UiViewState.DisplayError(
            title = resources.getString(R.string.title_no_refresh_token),
            titleColor = Color.Red,
            errorInfo = resources.getString(R.string.error_no_refresh_token),
            errorElementMap = mapOf(
                resources.getString(R.string.key_refresh_token_error_access_token_expiration) to formatDate(expiry)
            )
        )
    }

    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat(resources.getString(R.string.token_date_format), Locale.US)
        val date = Date(time)
        return sdf.format(date)
    }
}