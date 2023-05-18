package com.deep_link.sample.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.deep_link.sample.*
import com.deep_link.sample.R

private const val TAG = "DeepLinkTest"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMain(_onNavigateToWebView		: () -> Unit,
			   _refreshToken			: String?,
			   _refreshTokenUpdate		: (String?) -> Unit)
{
	Log.d(TAG, "[ScreenMain] _refreshToken/$_refreshToken")

	Column(modifier = Modifier.fillMaxSize())
	{
		val context = LocalContext.current

		TopAppBar(modifier = Modifier.fillMaxWidth(1.0f),
				  title = { ToolbarTitle() },
				  colors = TopAppBarDefaults.topAppBarColors(containerColor		= MaterialTheme.colorScheme.primary,
															 titleContentColor	= MaterialTheme.colorScheme.primary))

		Column(modifier = Modifier.padding(horizontal = 16.dp,
										   vertical = 16.dp))
		{
			Button(modifier = Modifier.fillMaxWidth(),
				   enabled = _refreshToken.isNullOrBlank(),
				   onClick = _onNavigateToWebView)
			{
				Text(text = stringResource(id = R.string.account_login_with_web_view))
			}

			Button(modifier = Modifier.fillMaxWidth(),
				   enabled = _refreshToken.isNullOrBlank(),
				   onClick = { OpenExternalBrowser(_context		= context.applicationContext,
												   _site		= SITE_SANDBOX,
												   _redirectURL	= REDIRECT_URL_BROWSER) })
			{
				Text(text = stringResource(id = R.string.account_login_with_external_browser))
			}

			Button(modifier = Modifier.fillMaxWidth(),
				   enabled = _refreshToken?.isNotBlank() == true,
				   onClick = { AccountTokensRevoke(_refreshToken, _refreshTokenUpdate) })
			{
				Text(text = stringResource(id = R.string.account_logout))
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWebView(_onNavigateToMainView		: () -> Unit,
				  _refreshTokenUpdate		: (String?) -> Unit)
{
	Column(modifier = Modifier.fillMaxSize())
	{
		TopAppBar(modifier = Modifier.fillMaxWidth(1.0f),
				  title = { ToolbarTitle() },
				  navigationIcon =	{
					  IconButton(onClick = _onNavigateToMainView)
					  {
						  Icon(imageVector = Icons.Filled.ArrowBack,
							   contentDescription	= null,
							   tint = MaterialTheme.colorScheme.onPrimary)
					  }
				  },
				  colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
															 titleContentColor = MaterialTheme.colorScheme.primary))
		AndroidView(modifier = Modifier.fillMaxSize(),
					factory =
					{
						WebView(it).apply()
						{
							layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																  ViewGroup.LayoutParams.MATCH_PARENT)

							CookieManager.getInstance().removeAllCookies(null)
							clearCache(true)

							with(settings)
							{
								@SuppressLint("SetJavaScriptEnabled")
								javaScriptEnabled						= true
								blockNetworkLoads						= false
								settings.domStorageEnabled				= true
								cacheMode								= WebSettings.LOAD_CACHE_ELSE_NETWORK
								setLayerType(View.LAYER_TYPE_HARDWARE, null)
							}

							webViewClient = object : WebViewClient()
							{
								//==================================================================================================
								//	onPageFinished
								//--------------------------------------------------------------------------------------------------
								override fun onPageFinished(_view : WebView?, _url : String?)
								{
									super.onPageFinished(_view, _url)

									Log.d(TAG, "[onPageFinished] $_url")
								}
								//==================================================================================================
								//	shouldOverrideUrlLoading
								//--------------------------------------------------------------------------------------------------
								override fun shouldOverrideUrlLoading(_view : WebView?, _request : WebResourceRequest?) : Boolean
								{
									Log.d(TAG, "[shouldOverrideUrlLoading] new format url/${_request?.url}")

									_request?.url?.also()
									{ _uri ->
										if(_uri.host?.contains("localhost") == true)
										{
											Log.d(TAG, "[shouldOverrideUrlLoading] new format account logged")

											_uri.getQueryParameter("code")?.also()
											{ _codeParameter ->
												_onNavigateToMainView.invoke()

												AccountTokensReceive(_codeParameter,
																	 REDIRECT_URL_WEB_VIEW,
																	 _refreshTokenUpdate)

												return true
											}
										}
									}
									return false
								}
								//==================================================================================================
								//	shouldOverrideUrlLoading
								//--------------------------------------------------------------------------------------------------
								@Deprecated("Deprecated in Java")
								@Suppress("OverridingDeprecatedMember")
								override fun shouldOverrideUrlLoading(_view	: WebView?,
																	  _url	: String?)	: Boolean
								{
									Log.d(TAG, "[shouldOverrideUrlLoading] old format")

									if(_url != null)
									{
										val uri : Uri
										try
										{
											uri = Uri.parse(_url)
										}
										catch(_exception : Exception)
										{
											return false
										}

										if(uri.host?.contains("localhost") == true)
										{
											Log.d(TAG, "[shouldOverrideUrlLoading] old format account logged")

											uri.getQueryParameter("code")?.also()
											{ _codeParameter ->
												_onNavigateToMainView.invoke()

												AccountTokensReceive(_codeParameter,
																	 REDIRECT_URL_WEB_VIEW,
																	 _refreshTokenUpdate)

												return true
											}
										}
									}
									return false
								}
							}
						}
					},
					update = { it.loadUrl(UriToObtainCode(SITE_SANDBOX, REDIRECT_URL_WEB_VIEW).toString()) })
	}
}

@Composable
fun ToolbarTitle()
{
	Text(text = stringResource(id = R.string.app_name),
		 color = MaterialTheme.colorScheme.onPrimary)
}
