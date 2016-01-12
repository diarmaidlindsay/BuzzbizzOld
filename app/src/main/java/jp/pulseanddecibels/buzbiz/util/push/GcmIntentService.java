package jp.pulseanddecibels.buzbiz.util.push;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import jp.pulseanddecibels.buzbiz.R;
import jp.pulseanddecibels.buzbiz.SplashActivity;

//プッシュ通知を受けた場合の処理
public class GcmIntentService extends IntentService {
	public static int num = 2;


	public GcmIntentService() {
		super("GcmIntentService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}

		Bundle extras = intent.getExtras();
		if (extras.isEmpty()) {
			return;
		}

		// 通常メッセージ形式が通知された場合
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			sendNotification(extras.getString("default"));
		}

		WakefulBroadcastReceiver.completeWakefulIntent(intent);
	}





	// 受け取ったメッセージの処理
	private void sendNotification(String msg) {

		String name = msg;
		if(TextUtils.isEmpty(name)){
			name = "不明";
		}

		num++;

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// タップした場合に起動させるアクティビティ
		Intent notificationIntent = new Intent(this, SplashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		// ノーティフィケーションの作成
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.buzbiz_top_icon)	// アイコン設定
				.setTicker("BuzBiz 着信")				    // 通知バーに表示する簡易メッセージ
				.setWhen(System.currentTimeMillis())	    // 時間
				.setContentTitle("BuzBiz 着信")			    // 展開メッセージのタイトル
				.setContentText(name)					    // 展開メッセージの詳細メッセージ
				.setContentIntent(contentIntent)		    // PendingIntent
				// 通知時の音・バイブ・ライト
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
				// タップするとキャンセル
				.setAutoCancel(true)
				.build();

		notificationManager.notify(num, notification);
	}
}