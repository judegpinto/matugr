package com.matugr.sample

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matugr.common.external.UrlLauncherPort
import com.matugr.sample.data.ui.UiToken
import com.matugr.sample.data.ui.UiViewState
import com.matugr.sample.data.ui.UiViewStateFactory
import com.matugr.sample.data.ui.ViewAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Core view logic for the sample app. Delegates UI events to the business layer and processes the
 * data received from the business layer for the UI.
 */
@HiltViewModel
class AuthDemoViewModel @Inject constructor(
    private val authDemoModel: AuthDemoModel,
    private val resources: Resources,
    private val uiViewStateFactory: UiViewStateFactory
    ): ViewModel() {
    private val viewStateFlow: MutableStateFlow<UiViewState> = MutableStateFlow(uiViewStateFactory.idle())
    val viewState: StateFlow<UiViewState> = viewStateFlow

    fun onAction(viewAction: ViewAction) {
        when(viewAction) {
            is ViewAction.Init -> displayCurrentState()
            is ViewAction.ClickLogin -> launchUrl(viewAction.urlLauncherPort)
            is ViewAction.FetchNewToken -> fetchNewAccessToken()
            is ViewAction.Logout -> logout()
            is ViewAction.ClickCancel -> handleClickCancel()
        }
    }

    private fun displayCurrentState() {
        if (viewState.value is UiViewState.Authorizing) return
        viewModelScope.launch {
            viewStateFlow.value = UiViewState.Loading(resources.getString(R.string.loading_local_token))
            val currentToken = authDemoModel.getCurrentToken()
            viewStateFlow.value = when {
                (currentToken == null) -> uiViewStateFactory.idle()
                (currentToken.expirationTime > System.currentTimeMillis()) -> uiViewStateFactory.localTokenExpired(currentToken.expirationTime)
                else -> uiViewStateFactory.displayToken(currentToken.expirationTime)
            }
        }
    }

    private fun launchUrl(urlLauncherPort: UrlLauncherPort) {
        viewModelScope.launch {
            viewStateFlow.value = uiViewStateFactory.authorizing()
            val uiToken = authDemoModel.launchAuthorizationForToken(urlLauncherPort)
            processUiToken(uiToken)
        }
    }

    private fun fetchNewAccessToken() {
        viewModelScope.launch {
            viewStateFlow.value = UiViewState.Loading(resources.getString(R.string.loading_fetch_new_token))
            val uiToken = authDemoModel.fetchAccessTokenFromRefreshToken()
            processUiToken(uiToken)
        }
    }

    private fun logout() {
        viewModelScope.launch(NonCancellable) {
            launch(NonCancellable) {
                authDemoModel.logout()
            }
            viewStateFlow.value = uiViewStateFactory.idle()
        }
    }

    private fun handleClickCancel() {
        authDemoModel.cancelAuth()
        viewStateFlow.value = uiViewStateFactory.idle()
    }

    private fun processUiToken(uiToken: UiToken) {
        viewStateFlow.value = when(uiToken) {
            is UiToken.AuthorizationError -> uiViewStateFactory.authorizationError(uiToken.detail)
            is UiToken.NoRefreshToken -> uiViewStateFactory.noRefreshTokenError(uiToken.expiry)
            is UiToken.TokenError -> transformTokenError(uiToken)
            is UiToken.TokenSuccess -> uiViewStateFactory.displayToken(uiToken.expiry)
            is UiToken.NoLocalToken -> uiViewStateFactory.noLocalToken()
        }
    }

    private fun transformTokenError(uiToken: UiToken.TokenError): UiViewState.DisplayError {
        return when(uiToken) {
            is UiToken.TokenError.HttpError -> uiViewStateFactory.httpTokenError(uiToken.code, uiToken.jsonBody)
            is UiToken.TokenError.IllegalState -> uiViewStateFactory.illegalStateTokenError(uiToken.errorAsString)
            is UiToken.TokenError.OAuthError -> uiViewStateFactory.oAuthTokenError(
                oAuthErrorCode = uiToken.oAuthErrorCode,
                oAuthErrorInfo = uiToken.errorDescription,
                oAuthErrorUri = uiToken.errorUri
            )
        }
    }
}