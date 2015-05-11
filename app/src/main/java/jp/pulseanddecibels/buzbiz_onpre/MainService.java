package jp.pulseanddecibels.buzbiz_onpre;

import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz_onpre.data.LoginStatus;
import jp.pulseanddecibels.buzbiz_onpre.data.TelNumber;
import jp.pulseanddecibels.buzbiz_onpre.models.IncomingCallControl;
import jp.pulseanddecibels.buzbiz_onpre.models.JsonParser;
import jp.pulseanddecibels.buzbiz_onpre.models.LibEventListener;
import jp.pulseanddecibels.buzbiz_onpre.models.LibOperator;
import jp.pulseanddecibels.buzbiz_onpre.models.LoginChecker;
import jp.pulseanddecibels.buzbiz_onpre.models.File;
import jp.pulseanddecibels.buzbiz_onpre.models.ProximitySensorControl;
import jp.pulseanddecibels.buzbiz_onpre.models.Setting;
import jp.pulseanddecibels.buzbiz_onpre.models.SoundPlayer;
import jp.pulseanddecibels.buzbiz_onpre.models.VibratorControl;
import jp.pulseanddecibels.buzbiz_onpre.models.VolleyOperator;
import jp.pulseanddecibels.buzbiz_onpre.util.Util;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;





/**
 *
 * メインサービス
 *
 * @author 普天間
 *
 */
public class MainService extends Service {

	//------------------------------------------------------------------------
	// 現在のキーパッド画面の状態
	//------------------------------------------------------------------------
	public static final int NOMAL				= 10;
	public static final int CALLING_NO_KAYPUD	= 11;
	public static final int CALLING_KAYPUD		= 12;

	static int curentKaypadScreen = NOMAL;



	//------------------------------------------------------------------------
	// 現在の画面状態
	//------------------------------------------------------------------------
	public static final int COVER			= 10;
	public static final int LOGIN			= 20;
	public static final int INCOMING_CALL	= 21;
	public static final int HOLD			= 22;
	public static final int KAYPUD			= 23;
	public static final int EXTERBAN_TABLE	= 24;
	public static final int INTERNAL_TABLE	= 25;
	public static final int HISTORY			= 26;

	static int CurentScreenState = COVER;





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
				startStayService(R.drawable.login_status_icon);
			} else {
				stopStayService();
				startStayService(R.drawable.logout_status_icon);
			}
		}
	};






	@Override
	public void onCreate() {
		super.onCreate();
        Fabric.with(this, new Crashlytics());
//		Log.e(Util.LOG_TAG,"  メインサービス.onCreate  ");

		// ログアウト状態に戻す
		new LoginStatus(LoginStatus.OFFLINE).save(getApplicationContext());

        // 電話機能を初期化
        try {
            LIB_OP.initVax(getApplicationContext());
        } catch (UnsatisfiedLinkError ex) {
            String msg = "お使いの端末では、BUZBIZを使えない可能性がございます。";
            MainActivity.displayMessage(getApplicationContext(), msg);
        }

		// 画面ON時のお知らせアイコンチェックを登録
		getApplicationContext().registerReceiver(br, filter);
	}





	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.e(Util.LOG_TAG,"  メインサービス.onStartCommand  ");

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
			startStayService(R.drawable.logout_status_icon);

		} else if (msg.equals("unresident")) {
			// サービスを常駐化を終了
			stopStayService();

		} else if (msg.equals("re-resident")) {
			stopStayService();
			startStayService(R.drawable.logout_status_icon);
		}
	}





	/** 本サービスの常駐化確認用フラグ */
	private static boolean stayServiceFlag = false;

	/**
	 * サービスを常駐化する
	 */
//	@SuppressWarnings("deprecation")
	public void startStayService(int iconNum) {
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
		if (curentKaypadScreen == CALLING_NO_KAYPUD ||
			curentKaypadScreen == CALLING_KAYPUD) {
			return true;
		}

		return false;
	}





	/**
	 * SIPサーバーへの登録が成功したときの処理
	 */
	public static void setEventLoginSuccess() {
//		Log.e(Util.LOG_TAG, "  メインサービス.onRegisterSuccess  ");

		// ログイン状態を保存
		new LoginStatus(LoginStatus.ONLINE).save(me.getApplicationContext());

		// 変更予定
		me.stopStayService();
		me.startStayService(R.drawable.login_status_icon);

		// サーバの死活監視スタート
		LoginChecker.start(me.getApplicationContext());


		MainActivity.displayMessage(me.getApplicationContext(), "ログインに成功しました");

		MainActivity.getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 現在ログイン画面であれば、メインに遷移
				if (CurentScreenState == LOGIN) {
					if (LoginActivity.me != null && LoginActivity.me.isFinishing() == false) {
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
//		Log.e(Util.LOG_TAG, "  メインサービス.onRegisterFailure  ");

		// メッセージを表示
		MainActivity.displayMessage(me.getApplicationContext(), "ログインに失敗しました");

		// ログイン状態を保存
		new LoginStatus(LoginStatus.OFFLINE).save(me.getApplicationContext());
	}






	/** 通話終了のメソッドが2度走らないように管理 */
	private static boolean callFlag = false;


	/**
	 * 通話開始時の処理
	 */
	public static void setEventStartCall() {
//		Log.e(Util.LOG_TAG, "  メインサービス.onInviteAnswered  ");


		// 音を止める
		SoundPlayer.INSTANCE.stop();

		// 近接センサーをスタートさせる
		ProximitySensorControl.start(me.getApplicationContext());

		// 通話時間用タイマーをスタート
		KaypadScreen.startTimer();

		// ミュート・スピーカーボタンUIを使用可能に設定
		KaypadScreen.startUseingUiMuteAndSpeakerButton();

		callFlag = true;
	}




	private void onIncomingCall(IncomingCallItem callItem){
		boolean isFirstIncomingCall = !IncomingCallControl.INSTANCE.isDuringIncomingCall();
		IncomingCallControl.INSTANCE.addItem(callItem);

		// 通話中は、画面表示はしない
		if (isCalling()) {
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





	/**
	 * 通話終了時の処理
	 */
	public static void setEventEndCall() {
		if (!callFlag) {
			return;
		}
		callFlag = false;

		// キーパッド画面の状態を通常画面とし保存
		MainService.curentKaypadScreen = NOMAL;

		// 近接センサーをストップさせる
		ProximitySensorControl.stop(me.getApplicationContext());

		// 念のため、着信画面を終了させる
		IncomingCallActivity.end();

		// 通話時間用タイマーを止める
		KaypadScreen.stopTimer();

		if (MainActivity.me != null && MainActivity.me.isFinishing() == false) {
			// 1秒ぐらい結果を表示させ通話終了
			MainActivity.me.firstScreen.kaypadScreen.displayInformationWhenCallEnd();
		}

		// 着信が有れば着信画面へ
		if (IncomingCallControl.INSTANCE.isDuringIncomingCall()) {
			me.showIncomingCallActivity();
			return;
		}

		// 終了音を流す
		SoundPlayer.INSTANCE.startSyuuryou(me.getApplicationContext());
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

// *** 下記は実施しないことになった ***
//		// 他の端末からログインしていれば失敗
//		if(!Util.checkOneUserLogin(mContext)){
//			return false;
//		}

		return true;
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





	/////////////////////////////////////////////////////////////////////////////////////////////////////





    /**
     * Vaxライブラリーのイベントリスナー
     */
	protected static final LibEventListener listenew = new LibEventListener(){
		/**
		 * ログイン成功時
		 */
		public void OnSuccessToRegister() {
//			Log.e(Util.LOG_TAG, "--OnSuccessToRegister--");

			setEventLoginSuccess();
		}





		public void OnSuccessToReRegister() {
//			Log.e(Util.LOG_TAG, "--OnSuccessToReRegister--");
		}

		public void OnSuccessToUnRegister() {
//			Log.e(Util.LOG_TAG, "--OnSuccessToUnRegister--");
		}

		public void OnTryingToRegister() {
//			Log.e(Util.LOG_TAG, "--OnTryingToRegister--");
		}

		public void OnTryingToReRegister() {
//			Log.e(Util.LOG_TAG, "--OnTryingToReRegister--");
		}

		public void OnTryingToUnRegister() {
//			Log.e(Util.LOG_TAG, "--OnTryingToUnRegister--");
		}





		/**
		 * ログイン失敗
		 */
		public void OnFailToRegister(int nStatusCode, String sReasonPhrase) {
//			Log.e(Util.LOG_TAG, "--OnFailToRegister--");
//			android.util.Log.e(Util.LOG_TAG, "--StatusCode--" + nStatusCode);
//			android.util.Log.e(Util.LOG_TAG, "--ReasonPhrase--" + sReasonPhrase);
			setEventLoginFailure();
		}





		public void OnFailToReRegister(int nStatusCode, String sReasonPhrase) {
//			Log.e(Util.LOG_TAG, "--OnFailToReRegister--");
		}

		public void OnFailToUnRegister(int nStatusCode, String sReasonPhrase) {
//			Log.e(Util.LOG_TAG, "--OnFailToUnRegister--");
		}

		public void OnAccepting(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnAccepting--");
		}





		/**
		 * こちら側からの通話終了時の処理
		 */
		public void OnEndCall(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnEndCall--");

			setEventEndCall();
		}





		/**
		 * 架電開始時の処理
		 */
		public void OnConnecting(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnConnecting--");
		}

		/**
		 * 架電開始時にOnConnectingの次の処理
		 */
		public void OnDialing(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnDialing--");
		}





		/**
		 * 架電時のSIP応答をうけっとた時の処理
		 */
		public void OnProvisionalResponse(int nLineNo, int nStatusCode, String sReasonPharase) {
//			Log.e(Util.LOG_TAG, "--OnProvisionalResponse--");

			// ここで180 音をスタート   183 音をストップ
			switch (nStatusCode) {
			case 180:
				SoundPlayer.INSTANCE.startHassin(me.getApplicationContext());
				break;

			case 183:
				SoundPlayer.INSTANCE.stop();
				break;
			}
		}





		public void OnTryingToHold(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnTryingToHold--");
		}

		public void OnTryingToUnHold(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnTryingToUnHold--");
		}

		public void OnFailToHold(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnFailToHold--");
		}

		public void OnFailToUnHold(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnFailToUnHold--");
		}

		public void OnSuccessToHold(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnSuccessToHold--");
		}

		public void OnSuccessToUnHold(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnSuccessToUnHold--");
		}

		public void OnFailToConnect(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnFailToConnect--");
		}





		/**
		 * 着信時	（※ 着信時１番最初に走る）
		 * @param	sCallId			各着信コールの一意の識別子
		 * @param	sDisplayName	表示名
		 * @param	sUserName		ユーザー名
		 * @param	sFromURI		FromURI
		 * @param	sToURI			ToURI
		 */
		public void OnIncomingCall(final String sCallId,
								   final String sDisplayName,
								   final String sUserName,
								   final String sFromURI,
								   final String sToURI) {
//			android.util.Log.e(Util.LOG_TAG, "--OnIncomingCall--");


			// 不審な番号(100)からかかってくることがあるので弾く
			if(IncomingCallControl.checkBugTelNummber(new TelNumber(sUserName))){
				return;
			}

			final TelNumber telNum = new TelNumber(sUserName);

			// 失敗時
			final Response.ErrorListener err = new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					IncomingCallItem callItem =
							new IncomingCallItem(sCallId, sDisplayName, telNum, sFromURI, sToURI, sUserName);
					me.onIncomingCall(callItem);
				}
			};

			// 成功時
			final Response.Listener ok = new Response.Listener() {
				@Override
				public void onResponse(final Object response) {
					String name = null;
					try {
						String json = response.toString();
						name = new JsonParser().parceJsonForSerchName(json, telNum);
					} catch (Exception ex) { }
					if(TextUtils.isEmpty(name)){
						name = sUserName;
					}

					IncomingCallItem callItem =
							new IncomingCallItem(sCallId, sDisplayName, telNum, sFromURI, sToURI, name);
					me.onIncomingCall(callItem);
				}
			};

			VolleyOperator.resolverName(me.getApplicationContext(), telNum, ok, err);
		}





		/**
		 * 着信リンギングスタート時
		 */
		public void OnIncomingCallRingingStart(String sCallId) {
//			Log.e(Util.LOG_TAG, "--OnIncomingCallRingingStart--");
		}





		/**
		 * 着信リンギングストップ時
		 */
		public void OnIncomingCallRingingStop(String sCallId) {
//			android.util.Log.e(Util.LOG_TAG, "--OnIncomingCallRingingStop--");

			// 切れた着信を削除
			IncomingCallControl.INSTANCE.removeItem(sCallId);

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
		 * 通話開始時の処理
		 */
		public void OnConnected(int nLineNo, String sTxRTPIP, int nTxRTPPort, String sCallId) {
//			Log.e(Util.LOG_TAG, "--OnConnected--");

			setEventStartCall();
		}





		/**
		 * 架電に失敗した場合の処理
		 */
		public void OnFailureResponse(int nLineNo, int nStatusCode, String sReasonPharase) {
//			Log.e(Util.LOG_TAG, "--OnFailureResponse--");


//			Log.e(Util.LOG_TAG, "nLineNo--" + nLineNo);
//			Log.e(Util.LOG_TAG, "nStatusCode--"  + nStatusCode);
//			Log.e(Util.LOG_TAG, "sReasonPharase--" + sReasonPharase);


			Context context = me.getApplicationContext();

			// キーパッド画面の状態を通常画面とし保存
			MainService.curentKaypadScreen = NOMAL;

			// 近接センサーをストップさせる
			ProximitySensorControl.stop(context);

			// 音を止める
			SoundPlayer.INSTANCE.stop();

			// マイクを終了
			LIB_OP.closeMic();

			switch (nStatusCode) {
			case 404:
				MainActivity.displayMessage(context, "お掛けになった電話番号は、存在しません");
				break;

			case 486:
				MainActivity.displayMessage(context, "ご利用中です。お掛け直し下さい");
				break;
			}


			if (MainActivity.me != null && MainActivity.me.isFinishing() == false) {
				MainActivity.me.firstScreen.kaypadScreen.displayInformationWhenCallEnd();
			}
		}





		public void OnRedirectResponse(int nLineNo, int nStatusCode, String sReasonPharase, String sContact) {
//			Log.e(Util.LOG_TAG, "--OnRedirectResponse--");
		}





		/**
		 * 相手側からの通話開始時の処理
		 */
		public void OnDisconnectCall(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnDisconnectCall--");

			setEventEndCall();
		}





		public void OnCallTransferAccepted(int nLineNo) {
//			Log.e(Util.LOG_TAG, "--OnCallTransferAccepted--");
		}

		public void OnFailToTransfer(int nLineNo, int nStatusCode, String sReasonPharase) {
//			Log.e(Util.LOG_TAG, "--OnFailToTransfer--");
		}

		public void OnIncomingDiagnostic(String sMsgSIP, String sFromIP, int nFromPort) {
//			Log.e(Util.LOG_TAG, "--OnIncomingDiagnostic--");
		}

		public void OnOutgoingDiagnostic(String sMsgSIP, String sToIP, int nToPort) {
//			Log.e(Util.LOG_TAG, "--OnOutgoingDiagnostic--");
		}
	};





	/** Vax操作オブジェクト */
	public final static LibOperator LIB_OP = new LibOperator(listenew);
}