package com.matugr.sample.data.ui

/**
 * All the various states that can be displayed to the UI itself, including any data.
 */
sealed class UiViewState {
    object Idle: UiViewState()
    object Authorizing: UiViewState()
    data class DisplayError(val error: String): UiViewState()
    data class Loading(val message: String): UiViewState()
    data class DisplayToken(val status: String, val expiresIn: String): UiViewState()
}
