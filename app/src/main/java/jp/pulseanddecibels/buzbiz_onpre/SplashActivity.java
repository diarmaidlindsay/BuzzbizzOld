package jp.pulseanddecibels.buzbiz_onpre;

import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz_onpre.models.IncomingCallControl;
import jp.pulseanddecibels.buzbiz_onpre.models.SoundPlayer;
import jp.pulseanddecibels.buzbiz_onpre.models.VibratorControl;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;

import com.crashlytics.android.Crashlytics;





/**
 *
 * スプラッシュ画面
 *
 * @author 普天間
 *
 */
public class SplashActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.buzbiz_splash);

		// メインActivityが残っている場合は終了させる
		MainActivity.end();
		System.gc();

		if (MainService.me == null) {
			new Handler().postDelayed(run, 2000);
		} else {
			new Handler().postDelayed(run, 1000);
		}
	}





    // メニューを非表示に
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }





	private final Runnable run = new Runnable() {
		public void run() {
			moveNextActivity();
		}
	};





	/**
	 * 次の画面へ遷移
	 */
	private void moveNextActivity() {
		// 着信中は着信画面へ遷移
		if (IncomingCallControl.INSTANCE.isDuringIncomingCall()) {
			startActivity(new Intent(getApplicationContext(), IncomingCallActivity.class));

		// 着信していない場合はメイン画面へ遷移
		} else {
			VibratorControl.stop(getApplicationContext());
			SoundPlayer.INSTANCE.stop();
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
		}

		// スプラッシュ画面終了
		finish();
	}





    // -------------- イベントハンドラ --------------
    /**
     * 『戻る』ボタンを無効に設定
     */
    @Override
    public void onBackPressed() { }
}