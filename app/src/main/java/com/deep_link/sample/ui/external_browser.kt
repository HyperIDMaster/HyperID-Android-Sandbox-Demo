package com.deep_link.sample.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

private const val TAG = "DeepLinkTest"

fun OpenExternalBrowser(_context		: Context,
						_site			: String,
						_redirectURL	: String)
{
	val url			= UriToObtainCode(_site, _redirectURL)

	Log.d(TAG, "[OpenExternalBrowser] link/$url")

	val intent		= Intent(Intent.ACTION_VIEW, url)
	val activities	= _context.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_ALL)
	if(activities?.isNullOrEmpty() == true)
	{
		Log.d(TAG, "[OpenExternalBrowser] no browser to open link")
	}
	else
	{
		_context.startActivity(Intent.createChooser(intent, "Open URL").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
	}
}
