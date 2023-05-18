package com.deep_link.sample.ui

import android.net.Uri
import okhttp3.FormBody

/**
 * Test Client Credentials
 * You can use your own Client ID / Client Secret pair, registered via the HyperID Developer's Portal
 **/
const val SITE_SANDBOX			= "login-sandbox.hypersecureid.com"
const val clientId				= "auth-test"
const val clientSecret			= "1mFnix5xk53Q620Jvn1XLecMluAwDQ9W"

/** Important
 *
 * Manifest.xml must contain <data android:scheme/> to register application for processing redirect URI. For test example:
 * <data android:scheme="com.deeplink.sample"/>
 *
 * Additionally, it recommended to use
 * Android Deep Link (https://developer.android.com/studio/write/app-link-indexing})
 * or Firebase Dynamic Links (https://firebase.google.com/docs/dynamic-links)
 * for external browser or CustomTabs.
 *
 * This ensures that the link is processed only by our application and not accessible to anyone else.
 *
 **/

/**
 * redirect_url for WebView example
 * In case of custom ClientID, ensure you've registered this redirect URI via the HyperID Developer's Portal
 **/
const val REDIRECT_URL_WEB_VIEW	= "https://localhost:8443/auth/hyper-id/callback/"

/**
 * redirect_url for external browser or CustomTabs (https://developer.chrome.com/docs/android/custom-tabs/)
 * In case of custom ClientID, ensure you've registered this redirect URI via the HyperID Developer's Portal
 **/
const val REDIRECT_URL_BROWSER	= "com.deeplink.sample://com.deeplink.sample.com/auth/hyper-id/callback/"

fun UriToObtainCode(_dstSite		: String,
					_redirectURL	: String)	: Uri
{
	return Uri.Builder()
				.scheme("https")
				.authority(_dstSite)
				.appendPath("auth")
				.appendPath("realms")
				.appendPath("HyperID")
				.appendPath("protocol")
				.appendPath("openid-connect")
				.appendPath("auth")
				.appendQueryParameter("client_id",			clientId)
				.appendQueryParameter("scope",				"openid email")
				.appendQueryParameter("response_type",		"code")
				.appendQueryParameter("redirect_uri",		_redirectURL)
				.appendQueryParameter("ui_locales",			"en")
				.build()
}

fun UriToObtainAuthTokens(_dstSite		: String) : Uri
{
	return Uri.Builder()
			.scheme("https")
			.authority(_dstSite)
			.appendPath("auth")
			.appendPath("realms")
			.appendPath("HyperID")
			.appendPath("protocol")
			.appendPath("openid-connect")
			.appendPath("token")
			.build()
}

fun FormBodyToObtainAuthTokens(_code		: String,
							   _redirectURL	: String)	: FormBody
{
	return FormBody.Builder()
				.add("grant_type",			"authorization_code")
				.add("client_id",			clientId)
				.add("code",				_code)
				.add("redirect_uri",		_redirectURL)
				.add("ui_locales",			"en")
				.add("client_secret",		clientSecret)
				.build()
}
/**
 * Request to refresh auth token
**/
//fun UriToObtainTokenRefresh(_dstSite		: String,
//							_refreshToken	: String,
//							_redirectURL	: String) : Uri
//{
//	return Uri.Builder()
//			.scheme("https")
//			.authority(_dstSite)
//			.appendPath("auth")
//			.appendPath("realms")
//			.appendPath("HyperID")
//			.appendPath("protocol")
//			.appendPath("openid-connect")
//			.appendPath("token")
//			.build()
//}

//fun FormBodyToObtainToTokenRefresh(_refreshToken	: String,
//								   _redirectURL		: String)	: FormBody
//{
//	return FormBody.Builder()
//			.add("grant_type",			"refresh_token")
//			.add("client_id",			clientId)
//			.add("refresh_token",		_refreshToken)
//			.add("redirect_uri",		_redirectURL)
//			.add("ui_locales",			"en")
//			.add("client_secret",		clientSecret)
//			.build()
//}

fun UriToRevokeAccount(_dstSite : String) : Uri
{
	return Uri.Builder()
			.scheme("https")
			.authority(_dstSite)
			.appendPath("auth")
			.appendPath("realms")
			.appendPath("HyperID")
			.appendPath("protocol")
			.appendPath("openid-connect")
			.appendPath("logout")
			.build()
}

fun FormBodyToRevokeAccount(_refreshToken	: String)	: FormBody
{
	return FormBody.Builder()
			.add("refresh_token",		_refreshToken)
			.add("client_id",			clientId)
			.add("client_secret",		clientSecret)
			.build()
}
