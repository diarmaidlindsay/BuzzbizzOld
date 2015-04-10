package jp.pulseanddecibels.buzbiz_onpre.models;

import jp.pulseanddecibels.buzbiz_onpre.MainService;
import jp.pulseanddecibels.buzbiz_onpre.R;
import jp.pulseanddecibels.buzbiz_onpre.data.DtmfCode;
import jp.pulseanddecibels.buzbiz_onpre.data.TelNumber;
import android.content.Context;
import android.media.AudioManager;
import android.telephony.TelephonyManager;

import com.vax.dev.lib.VaxSIPUserAgent;

/**
 *
 * 電話ライブラリー操作クラス
 *
 * @author 普天間
 *
 */
public class LibOperator {
	private final VaxSIPUserAgent VAX;

	/** 最大ライン数 */
	private static final int MAX_LINE = 1;
	/** 使用するライン */
	private static final int MY_LINE = 0;





	/**
	 * コンストラクタ
	 * @param listener	Vax用のイベントリスナー
	 */
	public LibOperator(LibEventListener listener) {
		VAX = new VaxSIPUserAgent(listener);
	}





	/**
	 * ライブラリーの初期化
	 */
	public void initVax(Context context) {
        VAX.DeselectAllVoiceCodec();

        // 各コーデックの設定
        VAX.SetVoiceCodec(false, VaxSIPUserAgent.G711U_CodecNo);
        VAX.SetVoiceCodec(false, VaxSIPUserAgent.G711A_CodecNo);
        VAX.SetVoiceCodec(false, VaxSIPUserAgent.Opus_CodecNo);

        // ---------------------------------------------------------
        // 注意
        //   コーデックを通信キャリアで分ける
        //   (softbankではVoIPが通信速度制限されるため)
        //
        //     softbank : iLBC
        //     その他   : GSM
        // ---------------------------------------------------------
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimOperator().equals("44020")) {
            VAX.SetVoiceCodec(true, VaxSIPUserAgent.iLBC_CodecNo);
            VAX.SetVoiceCodec(false, VaxSIPUserAgent.GSM_CodecNo);
        } else {
            VAX.SetVoiceCodec(false, VaxSIPUserAgent.iLBC_CodecNo);
            VAX.SetVoiceCodec(true, VaxSIPUserAgent.GSM_CodecNo);
        }


        // エコーキャンセラー
        VAX.SetEchoCancellation(true);

        VAX.SpkSetSoftBoost(true);
        VAX.SpkSetAutoGain(2); // 2以外だと音質が悪くなる

        VAX.MicSetSoftBoost(true);
        VAX.MicSetAutoGain(2); // 2以外だと音質が悪くなる

        VAX.EnableKeepAlive(10);
    }





	/**
	 * ライスンスキーをセット
	 */
	public void setKey(String licenceKey) {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.setKey  ");

        // ライセンスキーを設定
        boolean b = VAX.SetLicenceKey(licenceKey);
        if (!b) {
            throw new RuntimeException("ライセンスキー登録に失敗");
        }
    }





	/**
	 * ログイン
	 * @param userName		ユーザ名
	 * @param userPassword	パスワード
	 * @param server		サーバ (IP or Domain)
	 * @return	結果
	 */
	public boolean login(String userName,
						 String userPassword,
						 String server) {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.login  ");

		if (VAX.VaxIsOnline() == false) {
			VAX.VaxInit(userName, userName, userName, userPassword, server, server, true, MAX_LINE);
		} else {
			MainService.me.startStayService(R.drawable.login_status_icon);
		}

		return VAX.VaxIsOnline();
	}





	/**
	 * 再ログインログイン
	 */
	public boolean reLogin() {
		return VAX.reLogin();
	}





	/**
	 * ログオフ
	 * @return	結果
	 */
	public boolean logout(){
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.logoff  ");

		if (VAX.VaxIsOnline()) {
			VAX.VaxUnInit();
		} else {
			MainService.me.startStayService(R.drawable.logout_status_icon);
		}

		return VAX.VaxIsOnline();
	}





	/**
	 * ログインしているかを確認
	 * @return
	 */
	public boolean isLogined() {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.isLogined  ");

		return VAX.VaxIsOnline();
	}





	/**
	 * 着信に応答する
	 */
	public void anserCall(String incomingCallId) {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.anserCall  ");

		VAX.AcceptCall(MY_LINE, incomingCallId, -1, -1);
	}





	/**
	 * 着信を拒否する
	 * @param incomingCallId 着信拒否するCallId
	 */
	public void rejectCall(String incomingCallId) {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.rejectCall  ");

		VAX.RejectCall(incomingCallId);
	}





	/**
	 * 架電開始
	 * @param telNum	架電先の電話番号
	 */
	public void startCall(TelNumber telNum) {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.startCall  ");

		String called = telNum.getBaseString();
		VAX.DialCall(MY_LINE, called, -1, -1);
	}





	/**
	 * 通話終了
	 */
	public void endCall() {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.endCall  ");

		VAX.Disconnect(MY_LINE);
	}





	/**
	 * マイクを終了させる
	 */
	public void closeMic() {
		VAX.CloseMic();
	}





	/**
	 * 保留
	 */
	public void hold(final Context context) {
//		Log.e(Util.LOG_TAG, "  VAX操作用クラス.hold  ");

		// 通話中でない場合は終了
		if (VAX.IsLineConnected(MY_LINE) == false){
			return;
		}


//		// 自分が保留状態でないかを確認
//		RunnableForGettingInfo run = new RunnableForGettingInfo(){
//			@Override
//			public void run() {
//				// HTTP 通信開始
//				String url = String.format(context.getString(R.string.hold_url), MainService.getSipServerIp());
//				String[] keys = { "user_id", "user_pass" };
//				String[] values = {File.getValue(context, File.LOGIN_SUCCESS_BUZBIZ_NAME),
//								   File.getValue(context, File.LOGIN_SUCCESS_BUZBIZ_SIPPASSWORD) };
//				// TODO info = OldNwControl.doPost(url, keys, values, context);
//			}
//		};
//		Thread th = new Thread(run);
//		th.start();
//		try{
//			th.join(4000);
//		}catch(Exception ex){
//			MainActivity.displayMessage(context, "保留に失敗しました。");
//		}
//
//
//		// TODO
////		if (OldNwControl.checkHoldName(run.info, File.getValue(context.getApplicationContext(), File.LOGIN_SUCCESS_SIP_NAME))) {
////			return;
////		}


		// 保留パークに転送し終了
		VAX.TransferCallEx(MY_LINE, "700");
		VAX.Disconnect(MY_LINE);
	}





	/**
	 * DTMFを送信する
	 * @param dtmfCode	送信するDTMFコード
	 */
	public void sendDtmf(DtmfCode dtmfCode) {
		VAX.DigitDTMF(MY_LINE, dtmfCode.getDtmfInt());
	}





	/**
	 * ミュートを設定
	 * @param muteFlag	ミュートのスイッチ
	 */
	public void mute(final boolean muteFlag) {
		new Thread(new Runnable() {
			public void run() {
				VAX.MuteLineMIC(MY_LINE, muteFlag);
			}
		}).start();
	}





	/**
	 * スピーカーを設定
	 * @param speekerFlag	スピーカーのスイッチ
	 */
	public void speeker(final Context context, final boolean speekerFlag) {
		new Thread(new Runnable() {
			public void run() {
				AudioManager m_AudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				m_AudioManager.setSpeakerphoneOn(speekerFlag);
			}
		}).start();
	}
}