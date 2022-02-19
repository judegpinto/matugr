package com.matugr.sample.data.ui

import com.matugr.common.external.UrlLauncherPort

sealed class ViewAction {
    object Init: ViewAction()
    data class ClickLogin(val urlLauncherPort: UrlLauncherPort): ViewAction()
    object ClickCancel: ViewAction()
    object FetchNewToken: ViewAction()
    object Logout: ViewAction()
}
