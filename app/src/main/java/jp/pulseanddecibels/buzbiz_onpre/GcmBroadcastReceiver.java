package jp.pulseanddecibels.buzbiz_onpre;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

//プッシュ通知受信用レシーバー
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// GcmIntentServiceクラスの呼び出し
		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));


		setResultCode(Activity.RESULT_OK);
	}
}