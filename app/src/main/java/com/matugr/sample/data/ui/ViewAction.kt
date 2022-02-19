package com.matugr.sample.data.ui

import com.matugr.common.external.UrlLauncherPort

/**
 * Actions that the UI can take, typically driven by UI interaction such as clicks.
 */
sealed class ViewAction {
    object Init: ViewAction()
    data class ClickLogin(val urlLauncherPort: UrlLauncherPort): ViewAction()
    object ClickCancel: ViewAction()
    object FetchNewToken: ViewAction()
    object Logout: ViewAction()
}
