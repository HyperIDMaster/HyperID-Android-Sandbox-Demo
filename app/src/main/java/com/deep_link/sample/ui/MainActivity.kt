package com.deep_link.sample.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deep_link.sample.ui.theme.Deep_link_smpleTheme

private const val TAG = "DeepLinkTest"

//**************************************************************************************************
//	eMainRoutes
//--------------------------------------------------------------------------------------------------
sealed class eMainRoutes(val route: String)
{
	object eMainScreen			: eMainRoutes("eMainScreen")
	object eWebViewScreen		: eMainRoutes("eWebViewScreen")
}

class MainActivity : ComponentActivity()
{
	private val refreshTokenState							= mutableStateOf<String?>(null)
	private val refreshTokenUpdate		: (String?) -> Unit = { refreshTokenState.value = it }

	override fun onCreate(savedInstanceState : Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContent()
		{
			Deep_link_smpleTheme()
			{
					Surface(modifier = Modifier.fillMaxSize(),
							color = MaterialTheme.colorScheme.surface)
					{
						val navController = rememberNavController()

						val onNavigateToWebView		: () -> Unit = { navController.navigate(eMainRoutes.eWebViewScreen.route) }
						val onNavigateToMainView	: () -> Unit = { navController.popBackStack() }

						val refreshToken			by refreshTokenState

						NavHost(navController = navController,
								startDestination = eMainRoutes.eMainScreen.route)
						{
							composable(eMainRoutes.eMainScreen.route)
							{
								ScreenMain(onNavigateToWebView,
										   refreshToken,
										   refreshTokenUpdate)
							}
							composable(eMainRoutes.eWebViewScreen.route)
							{
								ScreenWebView(onNavigateToMainView,
											  refreshTokenUpdate)
							}

						}
					}
			}
		}
		HandleIntent(intent)
	}
	override fun onNewIntent(_intent : Intent?)
	{
		super.onNewIntent(_intent)

		HandleIntent(_intent)
	}
	private fun HandleIntent(_intent : Intent?)
	{
		_intent?.data?.also()
		{ _redirectUri ->
			_redirectUri.getQueryParameter("code")?.also()
			{ _code ->
				Log.d(TAG, "[HandleIntent] code/$_code")

				AccountTokensReceive(_code,
									 REDIRECT_URL_BROWSER,
									 refreshTokenUpdate)
			}
		}
	}
}
