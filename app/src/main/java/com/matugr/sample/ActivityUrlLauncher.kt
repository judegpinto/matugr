package com.matugr.sample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.matugr.common.external.UrlLauncherPort
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * Sample app implementation of [UrlLauncherPort]. Leverages an activity to launch the URL via
 * implicit intent.
 */
@ActivityScoped
class ActivityUrlLauncher @Inject constructor(private val activity: Activity): UrlLauncherPort {
    override fun launchUrl(url: String) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
    }
}