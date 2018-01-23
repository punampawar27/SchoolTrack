package com.schooltrack.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.schooltrack.utils.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegistration {

	private static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
	private static final String APPNAME = "sample";

	/**
	 * Default lifespan (7 days) of a reservation until it is considered
	 * expired.
	 */
	private static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

	/**
	 * Substitute you own sender ID here.
	 */
	private final String SENDER_ID = "352962185276";

	/**
	 * Tag used on log messages.
	 */
	private static final String TAG = "GCM";

	private GoogleCloudMessaging gcm;
	private Context context;

	private String regid, url;
	private SharedPreferences mPrefernce;
	public void setRegistrationId(Context context, String url) {

		this.context = context.getApplicationContext();
		this.url = url;

		regid = getCachedRegistrationId();
		mPrefernce = context.getSharedPreferences("GEOTRACKDATA", 0);
		gcm = GoogleCloudMessaging.getInstance(context);

		if (regid.length() == 0) {
			registerBackground();
		} else {
			new SendRegistrationIdTask().execute();
		}

	}

	private String getCachedRegistrationId() {
		final SharedPreferences prefs = getGCMPreferences();
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");

		if (registrationId.length() == 0) {
			Log.v(TAG, "Registration not found.");
			return "";
		}
		// check if app was updated; if so, it must clear registration id to
		// avoid a race condition if GCM sends a message
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion || isRegistrationExpired()) {
			Log.v(TAG, "App version changed or registration expired.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences() {
		return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private boolean isRegistrationExpired() {
		final SharedPreferences prefs = getGCMPreferences();
		// checks if the information is not stale
		long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
		return System.currentTimeMillis() > expirationTime;
	}

	private void registerBackground() {
		new getRegistrationId().execute();
	}

	private final class getRegistrationId extends AsyncTask<Object, Void, Object> {
		String msg;

		@Override
		protected void onPostExecute(Object result) {
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration id=" + regid;

				// You should send the registration ID to your server over
				// HTTP,
				// so it can use GCM/HTTP or CCS to send messages to your
				// app.
				// Save the regid - no need to register again.
				setRegistrationId(regid);
				new SendRegistrationIdTask().execute();

			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
			}
			return msg;
		}
	}

	private void setRegistrationId(String regId) {
		final SharedPreferences prefs = getGCMPreferences();
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;
		editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
		editor.commit();
	}

	private final class SendRegistrationIdTask extends AsyncTask<Void, Void, String> {
		private String response;
		InputStream is;

		@Override
		protected String doInBackground(Void... blank) {


			url="http://45.40.137.142:8080/webservices/addDeviceForPushNotification?JSESSIONID="+mPrefernce.getString(Util.SESSION_ID,"")+"&IMEI="+getDeviceUid(context)+"&RegistrationNo="+regid+"&IsAndroid=true";
					//"http://45.40.137.142:8080/webservices/addDeviceForPushNotification?JSESSIONID="
			Log.e("regid", regid);
			HttpGet httpGet = new HttpGet(url);
			final String androidVersion = android.os.Build.VERSION.RELEASE;
			httpGet.setHeader("User-Agent", "osVersion=" + androidVersion);

			HttpParams httpParams = new BasicHttpParams();

			SharedPreferences shared = context.getSharedPreferences("appdata", 0);
			String timeout = shared.getString("timeOut", "60000");
			int some_reasonable_timeout = Integer.parseInt(timeout);

			if (some_reasonable_timeout < 1000)
				some_reasonable_timeout = some_reasonable_timeout * 1000;

			HttpConnectionParams.setConnectionTimeout(httpParams, some_reasonable_timeout);
			HttpConnectionParams.setSoTimeout(httpParams, some_reasonable_timeout);
			HttpClient client = new DefaultHttpClient(httpParams);

			HttpResponse httpResponse = null;
			try {
				httpResponse = client.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				response = sb.toString();
				if (response != null) {
					Log.i("Api Response-GET->", url);
				} else {
					Log.e("Api Failed-GET->", url);
				}
				return response;
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {

			}
		}

	}

	public static String getDeviceUid(final Context _act) {
		String androidDeviceId = "";
		try {
			final TelephonyManager mTelephony = (TelephonyManager) _act.getSystemService(Context.TELEPHONY_SERVICE);// for
																													// mobiles
			if (mTelephony.getDeviceId() != null)
				androidDeviceId = mTelephony.getDeviceId();
			else
				// Tablets
				androidDeviceId = Secure.getString(_act.getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return androidDeviceId;
	}

}
