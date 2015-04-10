package jp.pulseanddecibels.buzbiz_onpre;



import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz_onpre.models.IncomingCallControl;
import jp.pulseanddecibels.buzbiz_onpre.models.SoundPlayer;
import jp.pulseanddecibels.buzbiz_onpre.models.VibratorControl;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;





/**
 *
 * 着信画面
 *
 * @author 普天間
 *
 */
public class IncomingCallActivity extends Activity {

	static IncomingCallActivity me;





	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
//		Log.e(Util.LOG_TAG,"  着信画面.onCreate  ");


		// 自分を保存
		me = this;

		// 表示画面を着信画面に変更
		MainService.CurentScreenState = MainService.INCOMING_CALL;

		requestWindowFeature(Window.FEATURE_NO_TITLE);	// タイトルは非表示
		setContentView(R.layout.incoming_call_screen);	// ビューを設定
	}





	@Override
	public void onResume(){
		super.onResume();
//		Log.e(Util.LOG_TAG,"  着信画面.onResume  ");


		// 呼び出し用バイブレーター開始
		VibratorControl.start(getApplicationContext());

		// 着信音を鳴らす
		SoundPlayer.INSTANCE.startCyakusin(getApplicationContext());

		// 着信リストを設定
		setIncomingCallList();

		System.gc();


		// 着信が終わっている場合は終了
		if (!IncomingCallControl.INSTANCE.isDuringIncomingCall()) {
			finish();
		}
	}





	@Override
	public void onDestroy(){
//		Log.e(Util.LOG_TAG,"  着信画面.onDestroy  ");
		me = null;

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
		ArrayList<IncomingCallItem> arr = IncomingCallControl.INSTANCE.getIncomingCallList();
		IncomingCallAdapter adapter = new IncomingCallAdapter(this, arr, R.layout.incoming_call_list_item);
		((ListView) findViewById(R.id.incoming_call_list)).setAdapter(adapter);
	}





	/**
	 * 着信リストを再設定する
	 */
	public static void resetIncomingCallList() {
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
		// 以前のメイン画面が残っている場合は削除
		MainActivity.end();

		// メインに戻る
		Intent intentGoToMain = new Intent(getApplicationContext(), MainActivity.class);
		intentGoToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intentGoToMain);

		// 本画面を終了
		finish();
	}





	/**
	 * 本画面を終了させる
	 */
	public static void end() {
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