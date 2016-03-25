package jp.pulseanddecibels.buzbiz;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;

import org.pjsip.pjsua2.CallInfo;

import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz.data.LoginStatus;
import jp.pulseanddecibels.buzbiz.data.TelNumber;
import jp.pulseanddecibels.buzbiz.models.IncomingCallControl;
import jp.pulseanddecibels.buzbiz.models.JsonParser;
import jp.pulseanddecibels.buzbiz.models.LibOperator;
import jp.pulseanddecibels.buzbiz.models.LoginChecker;
import jp.pulseanddecibels.buzbiz.models.ProximitySensorControl;
import jp.pulseanddecibels.buzbiz.models.SoundPlayer;
import jp.pulseanddecibels.buzbiz.models.VibratorControl;
import jp.pulseanddecibels.buzbiz.models.VolleyOperator;
import jp.pulseanddecibels.buzbiz.pjsip.BuzBizCall;
import jp.pulseanddecibels.buzbiz.util.Util;





/**
 *
 * メインサービス
 *
 */
public class MainService extends Service {

	//------------------------------------------------------------------------
	// 現在のキーパッド画面の状態
	//------------------------------------------------------------------------
	static KeyPadStates currentKeypadScreen = KeyPadStates.NOMAL;

	//------------------------------------------------------------------------
	// 現在の画面状態
	//------------------------------------------------------------------------

	public enum KeyPadStates{
		NOMAL,
		CALLING_NO_KAYPUD,
		CALLING_KAYPAD
	}

	public enum ScreenStates{
		COVER,
		LOGIN,
		INCOMING_CALL,
		HOLD,
		KAYPAD,
		EXTERBAN_TABLE,
		INTERNAL_TABLE,
		HISTORY
	}

	static ScreenStates CurentScreenState = ScreenStates.COVER;
	private String LOG_TAG = getClass().getSimpleName();



	/** 自コンストラクタ */
	public static MainService me;



	// 画面ON時のお知らせアイコンチェック
	private final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
	private final BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LoginStatus status = LoginStatus.load(getApplicationContext());
			if (status.isOnLine()) {
				stopStayService();
				startStayService(jp.pulseanddecibels.buzbiz.R.drawable.login_status_icon);
			} else {
				stopStayService();
				startStayService(jp.pulseanddecibels.buzbiz.R.drawable.logout_status_icon);
			}
		}
	};






	@Override
	public void onCreate() {
		super.onCreate();
        Fabric.with(this, new Crashlytics());
		Log.d(LOG_TAG, "onCreate");

		// ログアウト状態に戻す
		new LoginStatus(LoginStatus.OFFLINE).save(getApplicationContext());

        // 電話機能を初期化
        try {
            LIB_OP.init();
        } catch (Exception ex) {
            String msg = "お使いの端末では、BUZBIZを使えない可能性がございます。";
            MainActivity.displayMessage(getApplicationContext(), msg);
        }

		// 画面ON時のお知らせアイコンチェックを登録
		getApplicationContext().registerReceiver(br, filter);
	}





	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.e(Util.LOG_TAG,"  メインサービス.onStartCommand  ");
		Log.d(LOG_TAG, "onStartCommand");
		me = this;

		// インテントのメッセージに対する処理を実行
		exeIntentMessage(intent);

		// 強制終了後は再起動させない
		return START_NOT_STICKY;
	}





	/**
	 * インテントのメッセージに対する処理を実行
	 * @param intent	対象のインテント
	 */
	private void exeIntentMessage(Intent intent) {
		// メッセージを取得
		String msg = Util.STRING_EMPTY;
		try {
			msg = intent.getStringExtra("message");
			intent.removeExtra("message");
		} catch (Exception e) { }
		if (TextUtils.isEmpty(msg)) {
			return;
		}

		// メッセージに対応した処理を実行
		if (msg.equals("resident")) {
			// サービスを常駐化する
			startStayService(jp.pulseanddecibels.buzbiz.R.drawable.logout_status_icon);

		} else if (msg.equals("unresident")) {
			// サービスを常駐化を終了
			stopStayService();

		} else if (msg.equals("re-resident")) {
			stopStayService();
			startStayService(jp.pulseanddecibels.buzbiz.R.drawable.logout_status_icon);
		}
	}





	/** 本サービスの常駐化確認用フラグ */
	private static boolean stayServiceFlag = false;

	/**
	 * サービスを常駐化する
	 */
//	@SuppressWarnings("deprecation")
	public void startStayService(int iconNum) {
		Log.d(LOG_TAG, "startStayService");
		// 既に常駐化している場合は終了
		if (stayServiceFlag) {
			return;
		}

		// タップした場合に起動させるアクティビティ
		Intent notificationIntent = new Intent(this, SplashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		// ノーティフィケーションの作成
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(iconNum)					// アイコン設定
				.setWhen(System.currentTimeMillis())	// 時間
				.setContentTitle("BuzBiz")				// 展開メッセージのタイトル
				.setContentText("起動中")				// 展開メッセージの詳細メッセージ
				.setContentIntent(contentIntent)		// PendingIntent
				.setAutoCancel(false)
				.build();

        // Notification は自動で削除しない
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

        // サービスをフォアグランド状態に変更する
        startForeground(1, notification);

		// フラグを変更
		stayServiceFlag = true;
    }






	/**
	 * サービスの常駐化終了
	 */
	public void stopStayService(){
		Log.d(LOG_TAG, "stopStayService");
//		Log.e(Util.LOG_TAG,"  メインサービス.stopStayService  ");


		stopForeground(true);

		// フラグを変更
		stayServiceFlag = false;
	}





	@Override
	public IBinder onBind(Intent intent) {
//		Log.e(Util.LOG_TAG,"  メインサービス.onBind  ");
		return null;
	}





	/**
	 * 通話中であるかを確認
	 * @return	結果
	 */
	public static boolean isCalling(){

//		if (currentKeypadScreen == CALLING_NO_KAYPUD ||
//			currentKeypadScreen == CALLING_KAYPUD) {
//			return true;
//		}
//
//		return false;
		return currentKeypadScreen != KeyPadStates.NOMAL;

	}





	/**
	 * SIPサーバーへの登録が成功したときの処理
	 */
	public static void setEventLoginSuccess() {
//		Log.e(Util.LOG_TAG, "  メインサービス.onRegisterSuccess  ");
		Log.d("MainService", "setEventLoginSuccess");

		// ログイン状態を保存
		new LoginStatus(LoginStatus.ONLINE).save(me.getApplicationContext());

		// 変更予定
		me.stopStayService();
		me.startStayService(jp.pulseanddecibels.buzbiz.R.drawable.login_status_icon);

		// サーバの死活監視スタート
		LoginChecker.start(me.getApplicationContext());


		MainActivity.displayMessage(me.getApplicationContext(), "ログインに成功しました");

		MainActivity.getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 現在ログイン画面であれば、メインに遷移

//				if (CurentScreenState == LOGIN) {
				if (CurentScreenState == ScreenStates.LOGIN) {
					if (LoginActivity.me != null && !LoginActivity.me.isFinishing()) {
						LoginActivity.me.onBackPressed();
					}
				}
			}
		}, 2000);
	}





	/**
	 * SIPサーバーへの登録が失敗したときの処理
	 */
	public static void  setEventLoginFailure() {
		Log.e("MainService", "setEventLoginFailure");

		// メッセージを表示
		MainActivity.displayMessage(me.getApplicationContext(), "ログインに失敗しました");

		// ログイン状態を保存
		new LoginStatus(LoginStatus.OFFLINE).save(me.getApplicationContext());
	}






	/**
	 * 通話開始時の処理
	 */
	public static void setEventStartCall() {
		Log.d("MainService", "setEventStartCall");
		// 近接センサーをスタートさせる
		//sometimes causes crash because it's initialising on a different thread to PJSIP
		ProximitySensorControl.start(me.getApplicationContext());

		// 音を止める
		SoundPlayer.INSTANCE.stop();

		// 通話時間用タイマーをスタート
		KeypadScreen.startTimer();

//		// ミュート・スピーカーボタンUIを使用可能に設定
		KeypadScreen.startUsingUiMuteAndSpeakerButton();
	}




	/**
	 * 通話終了時の処理
	 */
	public static void setEventEndCall() {

		Log.d("MainService", "setEventEndCall");
		//TODO フラグの存在意義を調査

		// キーパッド画面の状態を通常画面とし保存
//		MainService.currentKeypadScreen = NOMAL;
		currentKeypadScreen = KeyPadStates.NOMAL;

		// 念のため、着信画面を終了させる
		IncomingCallActivity.end();

		// 通話時間用タイマーを止める
		KeypadScreen.stopTimer();

		if (MainActivity.me != null && !MainActivity.me.isFinishing()) {
			// 1秒ぐらい結果を表示させ通話終了
			MainActivity.me.runOnUiThread(new Runnable() {
				public void run() {
					MainActivity.me.firstScreen.keypadScreen.displayInformationWhenCallEnd();
				}
			});
		}

		// 着信が有れば着信画面へ
		if (IncomingCallControl.INSTANCE.isDuringIncomingCall()) {
			me.showIncomingCallActivity();
			return;
		}

		// 終了音を流す
		SoundPlayer.INSTANCE.startSyuuryou(me.getApplicationContext());

		// 近接センサーをストップさせる
		//sometimes causes crash because it's on a different thread to PJSIP
		ProximitySensorControl.stop(me.getApplicationContext());

		LIB_OP.setCurrentCall(null);
	}


	/**
	 * 電話を掛けれるかチェック
	 */
	public static boolean isEnableCall(){
//		Log.e(Util.LOG_TAG,"  メインサービス.isEnableCall  ");


		Context context = me.getApplicationContext();

		int loginStatus = LoginStatus.load(context).toCode();

		// ログインしていない場合は失敗
		if (loginStatus == LoginStatus.OFFLINE) {
			MainActivity.displayMessage(context, "ログインしていません");
			return false;

		// サーバ接続に失敗している場合は失敗
		} else if (loginStatus == LoginStatus.PING_NG) {
			MainActivity.displayMessage(context, "サーバへ接続できませんでした");
			return false;
		}

		return true;
	}

	private void onIncomingCall(IncomingCallItem callItem){
		Log.d("MainService", "onIncomingCall 1");
		boolean isFirstIncomingCall = !IncomingCallControl.INSTANCE.isDuringIncomingCall();
		IncomingCallControl.INSTANCE.addItem(callItem);

		// 通話中は、画面表示はしない
		if (isCalling()) {
			//send busy signal if we're already on a call
			LIB_OP.busyCall(callItem.call);
			return;
		}

		// 初回着信時は、着信画面を表示
		if (isFirstIncomingCall) {
			me.showIncomingCallActivity();

			// 2回め以降の着信時は、着信画面を更新
		} else {
			IncomingCallActivity.resetIncomingCallList();
		}
	}

	public void onIncomingCall(final BuzBizCall call) {
		Log.d(LOG_TAG, "onIncomingCall 2");

		try {
			CallInfo info = call.getInfo();
			final String callId = Integer.toString(call.getId());
			final String remoteContact = info.getRemoteContact();
			final String remoteUri = info.getRemoteUri();
			final String localUri = info.getLocalUri();
			String callIdString = info.getCallIdString(); //maybe can be used for something
			//force delete to ensure we don't get PJSIP non-registered thread crash
			info.delete();

			//"2809" <sip:2809@192.168.1.230>
			final TelNumber telNum = new TelNumber(remoteContact.substring(remoteContact.indexOf(":")+1, remoteContact.indexOf("@")));

			final Response.ErrorListener err = new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e(LOG_TAG, "onErrorResponse");
					IncomingCallItem callItem =
							new IncomingCallItem(callId, remoteContact, telNum, remoteUri, localUri, remoteContact, call);
					me.onIncomingCall(callItem);
				}
			};

			// 成功時
			final Response.Listener ok = new Response.Listener() {
				@Override
				public void onResponse(final Object response) {
					Log.d(LOG_TAG, "Response.Listener ok1");
					String name = null;
					try {
						String json = response.toString();
						name = new JsonParser().parceJsonForSerchName(json, telNum);
					} catch (Exception ex) { }
					if(TextUtils.isEmpty(name)){
						name = remoteContact;
					}

					IncomingCallItem callItem =
							new IncomingCallItem(callId, remoteContact, telNum, remoteUri, localUri, name, call);
					me.onIncomingCall(callItem);
					Log.d(LOG_TAG, "Response.Listener ok2");
				}
			};

			Log.d(LOG_TAG, "onIncomingCall 3");
			VolleyOperator.resolverName(me.getApplicationContext(), telNum, ok, err);
//			//crashes if we don't add this Thread.sleep()
//			Thread.sleep(1000);
			Log.d(LOG_TAG, "onIncomingCall 4");
			//if we don't gc before answering the call, and gc happens when call is answered, the app will crash!
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void OnIncomingCallRingingStop() {
//			android.util.Log.e(Util.LOG_TAG, "--OnIncomingCallRingingStop--");

		// 切れた着信を削除
		IncomingCallControl.INSTANCE.clearCallList();

		// 着信が無くなっていれば、
		if (!IncomingCallControl.INSTANCE.isDuringIncomingCall()) {
			// 呼び出し用バイブレーター終了
			VibratorControl.stop(me.getApplicationContext());

			// 着信音を止める
			SoundPlayer.INSTANCE.stop();

			// 着信画面を終了させる
			IncomingCallActivity.end();

			// 他の着信が残っていれば、着信リストを更新
		} else {
			IncomingCallActivity.resetIncomingCallList();
		}
	}


	/**
	 * 着信画面を表示する
	 */
	void showIncomingCallActivity() {
		// ※	アプリを強制終了された時、挙動がおかしくなるためコメントアウト
		// 着信画面を表示する為、一旦レシーバに処理を渡す
//		me.sendBroadcast(new Intent("MainServiceAction"));

		// 着信画面を表示
		Intent intentGoToMain = new Intent(getApplicationContext(), IncomingCallActivity.class);
		intentGoToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intentGoToMain);
	}


//	/** PJSIP操作オブジェクト */
	public final static LibOperator LIB_OP = new LibOperator();
}