package com.matugr.sample

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matugr.common.external.UrlLauncherPort
import com.matugr.sample.data.ui.UiToken
import com.matugr.sample.data.ui.UiViewState
import com.matugr.sample.data.ui.ViewAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AuthDemoViewModel @Inject constructor(
    private val authDemoModel: AuthDemoModel,
    private val resources: Resources
    ): ViewModel() {
    private val viewStateFlow: MutableStateFlow<UiViewState> = MutableStateFlow(UiViewState.Idle)
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
        if (viewState.value == UiViewState.Authorizing) return
        viewModelScope.launch {
            viewStateFlow.value = UiViewState.Loading(resources.getString(R.string.loading_local_token))
            val currentToken = authDemoModel.getCurrentToken()
            if (currentToken != null) {
                viewStateFlow.value = UiViewState.DisplayToken(resources.getString(R.string.token_status_valid), formatDate(currentToken.expirationTime))
                return@launch
            }
            viewStateFlow.value = UiViewState.Idle
        }
    }

    private fun launchUrl(urlLauncherPort: UrlLauncherPort) {
        viewModelScope.launch {
            viewStateFlow.value = UiViewState.Authorizing
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
            viewStateFlow.value = UiViewState.Idle
        }
    }

    private fun handleClickCancel() {
        authDemoModel.cancelAuth()
        viewStateFlow.value = UiViewState.Idle
    }

    private fun processUiToken(uiToken: UiToken) {
        viewStateFlow.value = when(uiToken) {
            is UiToken.AuthorizationError -> UiViewState.DisplayError("${resources.getString(R.string.error_authorization)}: ${uiToken.detail}")
            is UiToken.NoRefreshToken -> UiViewState.DisplayError(resources.getString(R.string.error_no_refresh_token, formatDate(uiToken.expiry)))
            is UiToken.TokenError -> UiViewState.DisplayError(resources.getString(R.string.error_invalid_fetched_token, uiToken.errorAsString))
            is UiToken.TokenSuccess -> UiViewState.DisplayToken(resources.getString(R.string.token_status_valid), formatDate(uiToken.expiry))
            is UiToken.LocalTokenInvalid -> UiViewState.DisplayError(resources.getString(R.string.error_invalid_local_token))
        }
    }

    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat(resources.getString(R.string.token_date_format), Locale.US)
        val date = Date(time)
        return sdf.format(date)
    }
}