package com.schooltrack.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.schooltrack.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.schooltrack.SplashActivity;

public class GcmBroadcastReceiver extends BroadcastReceiver {

	Context context = null;
	
	private String message = null;
	private String category = null;
	private String mImageBigURL;
	private String mShareURL;
	private String mTitle;
	protected Bitmap remote_picture;
	private String mRssUrl = "";

	//Constants for save and share articale

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		this.context = context;
		String postUrl = null;
		String post_type = "news";
		String postId = null;

		SharedPreferences sp = context.getSharedPreferences("pn", Context.MODE_PRIVATE);

		if (!sp.getBoolean("pncheck", true))
			return;

		Log.i(" inside onReceive", "onReceive +++  ");
		try {
			String intentMessage = intent.getExtras().getString("message");
			if(TextUtils.isEmpty(intentMessage)){
				return;
			}
			/*JSONObject json = new JSONObject(intentMessage);

			if (json.has("message")) {
				message = json.getString("message");
			}
*/
			mTitle=intentMessage;
			if (intentMessage != null && !intentMessage.equalsIgnoreCase("") && !intentMessage.equalsIgnoreCase("null")){
				new CreateNotification(intentMessage).execute();
			}		

		} catch (Exception e) {
			e.printStackTrace();
		}

		setResultCode(Activity.RESULT_OK);

	}
	
	public class CreateNotification extends AsyncTask<Void, Void, Void> {
		String message,post_type,post_id,bigImageURL,shareURL,rssUrl;
		
		public CreateNotification(String message) {
			this.message=message;
			}

		/**
		 * Creates the notification object.
		 *
		 * @see #setNormalNotification
		 */
		@Override
		protected Void doInBackground(Void... params) {
			try {
				sendnotification("Firstpost Breaking News", message);
			} catch (Exception e) {
				e.printStackTrace();
			}catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public void sendnotification(String title, String message) {
		String ns = Context.NOTIFICATION_SERVICE;

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

		int icon = R.mipmap.app_icon;
/*
		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);
		contentView.setImageViewResource(R.id.image, R.drawable.fp_launch_icon);
		contentView.setTextViewText(R.id.text, message);*/

		/*CharSequence tickerText = Html.fromHtml(message).toString();
		long when = System.currentTimeMillis();
*/
		Notification notification = null;
	int id=getRandomNumber();
		if(notification==null){
			notification=setNormalNotification(context, message, id,"","");
		}
		
		mNotificationManager.notify(id, notification);
	}

	


	private String getVersionName(){
		// It will return string in format "&version=2.5" that will be directly appended to consumption page url
		String versionName="";
		try{
			versionName = "&version="+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}catch(Exception e){
			Log.e("url exception", "Getting exception in apending version name.");
			e.printStackTrace();
		}
		return versionName;
	}
	

	private int getRandomNumber()
	{
		try {
			int min=1,max=500;
			int range = (max - min) + 1;     
			return (int)(Math.random() * range) + min;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return (int) (System.currentTimeMillis()/10000);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return 467;
		}
	}
	
	private Notification setNormalNotification(Context context,String message,int id,String bigImageURL,String shareURL) {
		int id1=id*id+1;
		int id2=id*id+2;
		int id3=id*id+3;
		NotificationCompat.Builder notificationBuilder=null;

		Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
				R.mipmap.app_icon);
		// Setup an explicit intent for an ResultActivity to receive.
		Intent resultIntent = new Intent(context, SplashActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, id1, resultIntent, PendingIntent.FLAG_ONE_SHOT);


/*
		Intent shareIntent1 = getShareIntent(id, message, shareURL,bigImageURL);;// new Intent(context, NotificationShareActionListener.class);
		PendingIntent sharePendingIntent = PendingIntent.getBroadcast(context, id2, shareIntent1, PendingIntent.FLAG_ONE_SHOT);
*/


		notificationBuilder= new NotificationCompat.Builder(context);
		notificationBuilder.setSmallIcon(R.mipmap.app_icon);
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.setContentIntent(resultPendingIntent);

/*
		if (shareURL!=null && !shareURL.equalsIgnoreCase("")||shareURL.equalsIgnoreCase("null")) {
			notificationBuilder.addAction(R.drawable.ic_action_share, "Share", sharePendingIntent);
		}
*/

		notificationBuilder.setContentTitle("First Post");
		notificationBuilder.setContentText(message);
		notificationBuilder.setWhen(0);
		notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
		notificationBuilder.setPriority(Notification.PRIORITY_MAX);
		if (bigImageURL==null || bigImageURL.equalsIgnoreCase("")||bigImageURL.equalsIgnoreCase("null")) {
			NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
			notiStyle.bigText(message);
			notificationBuilder.setStyle(notiStyle);

		} else {
			//notificationBuilder.setStyle(NotificationUtils.getInstance().getBigPictureNotificationStyle(message, bigImageURL));
		}
		return notificationBuilder.build();
	}

}
