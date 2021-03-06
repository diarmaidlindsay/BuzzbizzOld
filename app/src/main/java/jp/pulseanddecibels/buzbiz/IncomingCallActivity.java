package jp.pulseanddecibels.buzbiz;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz.models.IncomingCallControl;
import jp.pulseanddecibels.buzbiz.models.SoundPlayer;
import jp.pulseanddecibels.buzbiz.models.VibratorControl;





/**
 *
 * 着信画面
 *
 *
 */
public class IncomingCallActivity extends Activity {

	static IncomingCallActivity me;
	static String LOG_TAG = IncomingCallActivity.class.getSimpleName();
	//Turn on screen using Wakelock
	PowerManager.WakeLock wl;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
		Log.d(LOG_TAG, "onCreate  1");

		// 自分を保存
		me = this;

		// 表示画面を着信画面に変更
		MainService.CurentScreenState = MainService.ScreenStates.INCOMING_CALL;
//		MainService.CurentScreenState = MainService.INCOMING_CALL;

		requestWindowFeature(Window.FEATURE_NO_TITLE);	// タイトルは非表示
		setContentView(jp.pulseanddecibels.buzbiz.R.layout.incoming_call_screen);	// ビューを設定
		Log.d("IncomingCallActivity", "onCreate  2");
	}





	@Override
	public void onResume(){
		super.onResume();
		Window window = this.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		//wake up the screen on an incoming call using a wakelock
		PowerManager pm= (PowerManager) getSystemService(Context.POWER_SERVICE);
		if(wl == null) {
			wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
		}
		if(!wl.isHeld()) {
			wl.acquire();
		}


		Log.d(LOG_TAG, "onResume  ");


		// 呼び出し用バイブレーター開始
		VibratorControl.start(getApplicationContext());

		// 着信音を鳴らす
		SoundPlayer.INSTANCE.startCyakusin(getApplicationContext());

		// 着信リストを設定
		setIncomingCallList();

		//System.gc();


		// 着信が終わっている場合は終了
		if (!IncomingCallControl.INSTANCE.isDuringIncomingCall()) {
			finish();
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "onPause  ");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy  ");
		me = null;
		if(wl != null) {
			wl.release();
		}
		// 呼び出し用バイブレーター終了
		VibratorControl.stop(getApplicationContext());

		// 着信音を止める
		SoundPlayer.INSTANCE.stop();

		super.onDestroy();
	}





	/**
	 * 着信リストを設定する
	 */
	public void setIncomingCallList() {
		//Log.d(LOG_TAG, "setIncomingCallList");
		ArrayList<IncomingCallItem> arr = IncomingCallControl.INSTANCE.getIncomingCallList();
		IncomingCallAdapter adapter = new IncomingCallAdapter(this, arr, jp.pulseanddecibels.buzbiz.R.layout.incoming_call_list_item);
		((ListView) findViewById(jp.pulseanddecibels.buzbiz.R.id.incoming_call_list)).setAdapter(adapter);
	}





	/**
	 * 着信リストを再設定する
	 */
	public static void resetIncomingCallList() {
		//Log.d(LOG_TAG, "resetIncomingCallList");
		// 着信画面が閉じられていれば再表示
//		if (me == null && me.isFinishing()) {
//			MainService.me.showIncomingCallActivity();
//			return;
//		}

		// 既に着信画面がある場合はリストを更新する
		try {
			if(me != null && !me.isFinishing()){
				me.setIncomingCallList();
			}
		} catch (Exception ex) { ex.getStackTrace(); }
	}





	/**
	 * メイン画面に戻る
	 */
	public void returnMainActivity() {
		//Log.d(LOG_TAG, "returnMainActivity");
		// 以前のメイン画面が残っている場合は削除
		MainActivity.end();

		// メインに戻る
		Intent intentGoToMain = new Intent(getApplicationContext(), MainActivity.class);
		intentGoToMain.putExtra("incoming", true);
		intentGoToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intentGoToMain);
		// 本画面を終了
		finish();
	}


	@Override
	public void finish() {
		//don't finish if there is still calls
		if(!IncomingCallControl.INSTANCE.isDuringIncomingCall()) {
			//Log.d(LOG_TAG, "No incoming calls, finishing IncomingCallActivity");
			super.finish();
		} else {
			//Log.d(LOG_TAG, "Still incoming calls, not finishing IncomingCallActivity");
		}
	}

	/**
	 * 本画面を終了させる
	 */
	public static void end() {
		//Log.d(LOG_TAG, "end");
		if(me != null && !me.isFinishing()){
			me.finish();
		}
	}





	/**
	 * 『拒否』ボタン押下時の処理
	 */
	public void clickBtnReject(View v) {
//		Log.e(Util.LOG_TAG,"  着信画面.clickBtnReject  ");

		// 拒否
		IncomingCallControl.INSTANCE.rejectAll();
		// メイン画面に戻る
		returnMainActivity();
	}





	/**
	 * 『戻る』ボタンを無効に設定
	 */
	@Override
	public void onBackPressed() { }
}