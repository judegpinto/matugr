package com.matugr.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.matugr.sample.data.ui.UiViewState
import com.matugr.sample.data.ui.ViewAction
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Singular, compose-based activity for the sample app. Driven by [UiViewState].
 */
@ExperimentalUnitApi
@AndroidEntryPoint
class AuthDemoActivity : AppCompatActivity() {
    private val authDemoViewModel by viewModels<AuthDemoViewModel>()

    @Inject
    lateinit var activityUrlLauncher: ActivityUrlLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthDemo()
        }
        authDemoViewModel.onAction(ViewAction.Init)
    }

    @Composable
    fun AuthDemo() {
        val state = authDemoViewModel.viewState.collectAsState()
        when(val uiViewState = state.value) {
            is UiViewState.Idle -> Idle()
            is UiViewState.Loading -> Loading(uiViewState.message)
            is UiViewState.Authorizing -> Authorizing()
            is UiViewState.DisplayToken -> DisplayToken(uiViewState.status, uiViewState.expiresIn)
            is UiViewState.DisplayError -> DisplayError(uiViewState.error)
        }
    }

    @Composable
    fun Idle() {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = getString(R.string.idle_state),
                fontSize = TextUnit(20.0f, TextUnitType.Sp),
                textAlign = TextAlign.Center)
            LoginButton()
        }
    }

    @Composable
    fun Loading(message: String) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = getString(R.string.loading_general_text), fontSize = TextUnit(20.0f, TextUnitType.Sp))
            Text(text = message, fontSize = TextUnit(20.0f, TextUnitType.Sp))
        }
    }

    @Composable
    fun DisplayToken(status: String, expiry: String) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = getString(R.string.label_token_status), color = Color.Gray, fontSize = TextUnit(15.0f, TextUnitType.Sp))
                    Text(text = status, fontSize = TextUnit(20.0f, TextUnitType.Sp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = getString(R.string.label_token_expiry), color = Color.Gray, fontSize = TextUnit(15.0f, TextUnitType.Sp))
                    Text(text = expiry, fontSize = TextUnit(20.0f, TextUnitType.Sp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { authDemoViewModel.onAction(ViewAction.FetchNewToken) }) {
                    Text(text = getString(R.string.button_fetch_token))
                }
                Button(onClick = { authDemoViewModel.onAction(ViewAction.Logout) }) {
                    Text(text = getString(R.string.button_logout))
                }
            }
        }
    }

    @Composable
    fun Authorizing() {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = getString(R.string.authorizing), fontSize = TextUnit(20.0f, TextUnitType.Sp))
            Button(onClick = { authDemoViewModel.onAction(ViewAction.ClickCancel) }) {
                Text(text = getString(R.string.button_cancel_login))
            }
        }
    }

    @Composable
    fun DisplayError(error: String) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = error, fontSize = TextUnit(20.0f, TextUnitType.Sp))
            LoginButton()
        }
    }

    @Composable
    fun LoginButton() {
        Button(onClick = { authDemoViewModel.onAction(ViewAction.ClickLogin(activityUrlLauncher)) }) {
            Text(text = getString(R.string.button_login))
        }
    }
}