package com.deep_link.sample.ui

import android.util.Log
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

private const val TAG = "DeepLinkTest"

fun AccountTokensReceive(_code					: String,
						 _redirectUrl			: String,
						 _refreshTokenUpdate	: (String) -> Unit)
{
	Log.d(TAG, "[AccountTokensReceive] _code/$_code")

	val client = OkHttpClient()

	UriToObtainAuthTokens(SITE_SANDBOX)
			.toString()
			.toHttpUrlOrNull()
			?.newBuilder()
			?.also()
			{ _httpUriBuilder ->
				val url			= _httpUriBuilder.build().toString()
				val request		= Request.Builder()
										.url(url)
										.post(FormBodyToObtainAuthTokens(_code, _redirectUrl))
										.build()

				val callback =	object : Callback
				{
					override fun onFailure(call : Call, e : IOException)
					{
						Log.d(TAG, "[onFailure] ${e.localizedMessage}")
					}
					override fun onResponse(call : Call, response : Response)
					{
						try
						{
							response.body?.string()?.also()
							{ _responseBody ->
								val responseContent = JSONObject(_responseBody)

								Log.d(TAG, "AccessToken 				- ${responseContent.getString("access_token")}")
								Log.d(TAG, "RefreshToken				- ${responseContent.getString("refresh_token")}")
								Log.d(TAG, "Access token expires in		- ${responseContent.getInt("expires_in")}")
								Log.d(TAG, "Refresh token expires in	- ${responseContent.getInt("refresh_expires_in")}")

								_refreshTokenUpdate.invoke(responseContent.getString("refresh_token"))
							}
						}
						catch(_exception : Exception)
						{
							_exception.localizedMessage?.also { Log.d(TAG, it) }
						}
					}
				}

				client.newCall(request).enqueue(callback)
			}
}

fun AccountTokensRevoke(_refreshToken		: String?,
						_refreshTokenUpdate	: (String?) -> Unit)
{
	if(_refreshToken.isNullOrBlank())
	{
		return
	}

	val client = OkHttpClient()

	UriToRevokeAccount(SITE_SANDBOX)
			.toString()
			.toHttpUrlOrNull()
			?.newBuilder()
			?.also()
			{
				val url			= it.build().toString()
				val request		= Request.Builder()
						.url(url)
						.post(FormBodyToRevokeAccount(_refreshToken))
						.build()

				val callback =	object : Callback
				{
					override fun onFailure(call : Call, e : IOException)
					{
						Log.d(TAG, "[onFailure] ${e.localizedMessage}")
					}
					override fun onResponse(call : Call, response : Response)
					{
						if(response.isSuccessful)
						{
							_refreshTokenUpdate.invoke(null)
						}
					}
				}

				client.newCall(request).enqueue(callback)
			}
}
