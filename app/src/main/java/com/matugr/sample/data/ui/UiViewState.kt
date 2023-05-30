package com.matugr.sample.data.ui

import androidx.compose.ui.graphics.Color

/**
 * All the various states that can be displayed to the UI itself, including any data.
 */
sealed class UiViewState {
    data class Idle(
        val title: String,
        val titleColor: Color,
        val info: String
    ): UiViewState()
    data class Authorizing(
        val title: String,
        val titleColor: Color,
        val info: String
    ): UiViewState()
    data class DisplayError(
        val title: String,
        val titleColor: Color,
        val errorInfo: String,
        val errorElementMap: Map<String, String>,
    ): UiViewState()
    data class DisplayTokenExpiredError(
        val title: String,
        val titleColor: Color,
        val errorInfo: String,
        val errorElementMap: Map<String, String>,
    ): UiViewState()
    data class Loading(val message: String): UiViewState()
    data class DisplayToken(
        val title: String,
        val titleColor: Color,
        val moreInfo: String,
        val errorElementMap: Map<String, String>,
    ) : UiViewState()
}
