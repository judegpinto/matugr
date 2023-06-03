package com.matugr.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
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
            is UiViewState.Idle -> Idle(uiViewState)
            is UiViewState.Loading -> Loading(uiViewState.message)
            is UiViewState.Authorizing -> Authorizing(uiViewState)
            is UiViewState.DisplayToken -> DisplayToken(uiViewState)
            is UiViewState.DisplayError -> DisplayError(uiViewState)
            is UiViewState.DisplayTokenExpiredError -> DisplayTokenExpiredError(uiViewState)
        }
    }

    @Composable
    fun Idle(uiViewState: UiViewState.Idle) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleAndInfo(
                title = uiViewState.title,
                titleColor = uiViewState.titleColor,
                info = uiViewState.info
            )
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
    fun DisplayToken(uiToken: UiViewState.DisplayToken) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TitleInfoData(
                title = uiToken.title,
                titleColor = uiToken.titleColor,
                info = uiToken.moreInfo,
                data = uiToken.errorElementMap
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FetchNewTokenButton()
                LogoutButton()
            }
        }
    }

    @Composable
    fun Authorizing(uiViewState: UiViewState.Authorizing) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleAndInfo(
                title = uiViewState.title,
                titleColor = uiViewState.titleColor,
                info = uiViewState.info
            )
            CancelLoginButton()
        }
    }

    @Composable
    fun DisplayError(uiViewState: UiViewState.DisplayError) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleInfoData(
                title = uiViewState.title,
                titleColor = uiViewState.titleColor,
                info = uiViewState.errorInfo,
                data = uiViewState.errorElementMap
            )
            LoginButton()
        }
    }

    @Composable
    fun DisplayTokenExpiredError(uiViewState: UiViewState.DisplayTokenExpiredError) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleInfoData(
                title = uiViewState.title,
                titleColor = uiViewState.titleColor,
                info = uiViewState.errorInfo,
                data = uiViewState.errorElementMap
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FetchNewTokenButton()
                LogoutButton()
            }
        }
    }

    @Composable
    fun TitleInfoData(title: String, titleColor: Color, info: String, data: Map<String, String>) {
        TitleAndInfo(title = title, titleColor = titleColor, info = info)
        Metadata(errorMetadata = data)
    }

    @Composable
    fun TitleAndInfo(title: String, titleColor: Color, info: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = title,
                color = titleColor,
                fontSize = TextUnit(30.0f, TextUnitType.Sp),
                textAlign = TextAlign.Center)
            Text(text = info,
                fontSize = TextUnit(20.0f, TextUnitType.Sp),
                textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun Metadata(errorMetadata: Map<String, String>) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(errorMetadata.toList()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {
                    Text(text = it.first,
                        fontSize = TextUnit(15.0f, TextUnitType.Sp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                    )
                    Text(text = it.second,
                        fontSize = TextUnit(20.0f, TextUnitType.Sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    fun LoginButton() {
        Button(onClick = { authDemoViewModel.onAction(ViewAction.ClickLogin(activityUrlLauncher)) }) {
            Text(text = getString(R.string.button_login))
        }
    }

    @Composable
    fun LogoutButton() {
        Button(onClick = { authDemoViewModel.onAction(ViewAction.Logout) }) {
            Text(text = getString(R.string.button_logout))
        }
    }

    @Composable
    fun FetchNewTokenButton() {
        Button(onClick = { authDemoViewModel.onAction(ViewAction.FetchNewToken) }) {
            Text(text = getString(R.string.button_fetch_token))
        }
    }

    @Composable
    fun CancelLoginButton() {
        Button(onClick = { authDemoViewModel.onAction(ViewAction.ClickCancel) }) {
            Text(text = getString(R.string.button_cancel_login))
        }
    }
}