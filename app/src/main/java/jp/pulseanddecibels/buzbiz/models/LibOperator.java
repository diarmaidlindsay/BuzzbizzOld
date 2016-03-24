package jp.pulseanddecibels.buzbiz.models;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountInfo;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.PresenceStatus;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua_buddy_status;

import jp.pulseanddecibels.buzbiz.MainService;
import jp.pulseanddecibels.buzbiz.data.AsteriskAccount;
import jp.pulseanddecibels.buzbiz.data.DtmfCode;
import jp.pulseanddecibels.buzbiz.data.TelNumber;
import jp.pulseanddecibels.buzbiz.pjsip.BuzBizCall;
import jp.pulseanddecibels.buzbiz.pjsip.BuzbizAccount;

/**
 *
 * 電話ライブラリー操作クラス
 *
 *
 */
public class LibOperator {
	private BuzBizCall currentCall;
	private Endpoint pjsEndpoint;
	private BuzbizAccount account;

	private final String LOG_TAG = this.getClass().getSimpleName();

	/** 最大ライン数 */
	private static final int MAX_LINE = 1;
	/** 使用するライン */
	private static final int MY_LINE = 0;





	/**
	 * コンストラクタ
	 */
	public LibOperator() {
		//VAX = new VaxSIPUserAgent(listener);
	}


	/**
	 * Load PJSIP Native Library
	 */
	public void init() throws Exception {
		System.loadLibrary("pjsua2");
	}



	/**
	 * ライスンスキーをセット
	 */
	public void setKey(String licenceKey) {
//		Log.e(Util.LOG_TAG,"  VAX操作用クラス.setKey  ");

        // ライセンスキーを設定
//        boolean b = VAX.SetLicenceKey(licenceKey);
//        if (!b) {
//            throw new RuntimeException("ライセンスキー登録に失敗");
//        }
    }


	/**
	 * http://www.pjsip.org/docs/book-latest/html/endpoint.html
	 *
	 * Endpoint is the Core class of PJSUA2.
	 * Need to instantiate this class before anything else.
	 */
	private void initEndPoint() throws Exception {
		Log.e(LOG_TAG, "initEndPoint 1");
		pjsEndpoint = new Endpoint();
		pjsEndpoint.libCreate();

		//Configure Endpoint using EpConfig object
		EpConfig epConfig = new EpConfig();
		//log level
		epConfig.getLogConfig().setLevel(5);
		//maximum calls, 4 is default. Can be increased to 32.
		epConfig.getUaConfig().setMaxCalls(4);
		//conference bridge clockrate, default is 16KHz
		epConfig.getMedConfig().setSndClockRate(16000);
		//Initialise Endpoint with configuration
		pjsEndpoint.libInit(epConfig);
		//cleanup config object to prevent crash on Java GC
		epConfig.delete();

		//enable GSM and PCMU
		pjsEndpoint.codecSetPriority("GSM/8000", (short) 255);
		pjsEndpoint.codecSetPriority("PCMU/8000", (short) 255);
		//disable other codecs
		pjsEndpoint.codecSetPriority("speex/16000", (short) 0);
		pjsEndpoint.codecSetPriority("speex/8000", (short) 0);
		pjsEndpoint.codecSetPriority("speex/32000", (short) 0);
		pjsEndpoint.codecSetPriority("PCMA/8000", (short) 0);
		pjsEndpoint.codecSetPriority("G722/16000", (short) 0);
		pjsEndpoint.codecSetPriority("iLBC/8000", (short) 0);

        /*
		for(int i = 0; i < pjsEndpoint.codecEnum().size(); i++) {
			System.err.println(pjsEndpoint.codecEnum().get(i).getCodecId());
			System.err.println(pjsEndpoint.codecEnum().get(i).getPriority());
		}*/

		// Create SIP transport. Error handling sample is shown
		TransportConfig sipTpConfig = new TransportConfig();
		sipTpConfig.setPort(5060);
		pjsEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
		sipTpConfig.delete();
		// Start the library
		pjsEndpoint.libStart();
		Log.e(LOG_TAG, "initEndPoint 2");
	}

	/**
	 * De-initialise PJSIP library when closing to prevent crash
	 */
	private void deinitEndPoint() throws Exception {
		Log.e(LOG_TAG, "deInitEndPoint 1");
		if(pjsEndpoint != null) {
			pjsEndpoint.libDestroy();
			pjsEndpoint.delete();
		}
		pjsEndpoint = null;
		Log.e(LOG_TAG, "deInitEndPoint 2");
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

		Log.e(LOG_TAG, "login 1");

		try {
			initEndPoint();

			AccountConfig accountConfig = new AccountConfig();
			accountConfig.setIdUri("sip:" + userName + "@" + server);
			accountConfig.getRegConfig().setRegistrarUri("sip:" + server+ ":56131");
			AuthCredInfo cred = new AuthCredInfo("digest", "*", userName, 0, userPassword);
			accountConfig.getSipConfig().getAuthCreds().add(cred);
			// Create the account
			account = new BuzbizAccount();
			account.create(accountConfig);
			PresenceStatus ps = new PresenceStatus();
			ps.setStatus(pjsua_buddy_status.PJSUA_BUDDY_STATUS_ONLINE);
			account.setOnlineStatus(ps);
			MainService.setEventLoginSuccess();
			Log.e(LOG_TAG, "login 2");
			ps.delete();
			cred.delete();
			accountConfig.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if(account != null) {
				account.delete();
			}
			MainService.setEventLoginFailure();
		}

		return false;
	}





	/**
	 * 再ログインログイン
	 */
	public boolean reLogin(Context context) {
		Setting setting = new Setting();
		AsteriskAccount asteriskAccount = setting.loadAsteriskAccount(context);
		String user   = asteriskAccount.sipId;
		String pass   = asteriskAccount.sipPass;
		String server = setting.loadCurrentUseServerDomain(context);

		logout();
		return login(user, pass, server);
	}





	/**
	 * ログオフ
	 * @return	結果
	 */
	public void logout(){
		Log.e(LOG_TAG, "  PJSIP操作用クラス.logoff  ");
		if(account != null) {
			account.delete();
		}
		if(currentCall != null) {
			currentCall.delete();
		}

		// Explicitly destroy and delete endpoint
		Runtime.getRuntime().gc();
		try {
			deinitEndPoint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	/**
	 * ログインしているかを確認
	 * @return
	 */
	public boolean isLogined() {
		Log.d(LOG_TAG, "  PJSIP操作用クラス.isLogined  ");

//		return VAX.VaxIsOnline();
		try {
			if(account != null) {
				AccountInfo info = account.getInfo();
				boolean isLoggedIn = info.getOnlineStatus();
				info.delete();
				Log.d(LOG_TAG,"Logged in? : "+isLoggedIn);
				return isLoggedIn;
			}
		} catch (Exception e) {
			//do nothing
		}
		Log.d(LOG_TAG, "Not logged in");
		return false;
	}


	/**
	 * 着信に応答する
	 */
	public void answerCall(BuzBizCall call) {
		Log.e(LOG_TAG, "answerCall 1");
		setCurrentCall(call);
		CallOpParam callOpParam = new CallOpParam();
		callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
		try {
			currentCall.answer(callOpParam);
		} catch (Exception e) {
			currentCall.delete();
			e.printStackTrace();
		}
		callOpParam.delete();
		Log.e(LOG_TAG, "answerCall 2");
	}





	/**
	 * 着信を拒否する
	 */
	public void rejectCall(BuzBizCall call) {
		Log.e(LOG_TAG, "rejectCall 1");
		CallOpParam callOpParam = new CallOpParam();
		callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
		try {
			call.hangup(callOpParam);
		} catch (Exception e) {
			call.delete();
			e.printStackTrace();
		}
		callOpParam.delete();
		Log.e(LOG_TAG, "rejectCall 2");
	}





	/**
	 * 架電開始
	 * @param telNum	架電先の電話番号
	 */
	public void startCall(TelNumber telNum, Context context) {
		Log.e(LOG_TAG, "startCall 1");
		setCurrentCall(new BuzBizCall(account));
		CallOpParam callOpParam = new CallOpParam(true);
		try {
			Setting setting = new Setting();
			String sipServer;

			if (setting.isExistSavedLocalServerInfo(context)) {
				sipServer = setting.loadLocalServerDomain(context);
			} else {
				sipServer = setting.loadRemoteServerDomain(context);
			}
			//SIP URI is sip:<number>@<server>:<port>
			currentCall.makeCall("sip:" + telNum.getBaseString() + "@" + sipServer + ":56131", callOpParam);
		} catch (Exception e) {
			currentCall.delete();
			e.printStackTrace();
			return;
		}
		callOpParam.delete();
		Log.e(LOG_TAG, "startCall 2");
	}





	/**
	 * 通話終了
	 */
	public void endCall() {
		Log.d(LOG_TAG, "endCall");
		if(currentCall != null) {
			CallOpParam prm = new CallOpParam();
			prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
			try {
				currentCall.hangup(prm); //only need to do when the user manually hangs up
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				prm.delete();
			}
		}
	}




	/**
	 * 保留
	 */
	public void hold(Context context) {
		Log.e(LOG_TAG, "hold");
		CallOpParam prm = new CallOpParam();
		prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
		try {
			Setting setting = new Setting();
			String sipServer;

			if (setting.isExistSavedLocalServerInfo(context)) {
				sipServer = setting.loadLocalServerDomain(context);
			} else {
				sipServer = setting.loadRemoteServerDomain(context);
			}
			//transfer to "parking room" on server
			currentCall.xfer("sip:700@" + sipServer, prm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		prm.delete();
		}

}


	/**
	 * DTMFを送信する
	 * @param dtmfCode	送信するDTMFコード
	 */
	public void sendDtmf(DtmfCode dtmfCode) throws Exception {
		currentCall.dialDtmf(dtmfCode.getDtmfString());
	}




	/**
	 * ミュートを設定
	 * @param muteFlag	ミュートのスイッチ
	 */
	public void mute(final boolean muteFlag) {
		if(currentCall != null) {
			currentCall.muteMic(muteFlag);
		}
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

	public void setCurrentCall(BuzBizCall currentCall) {
		Log.e(LOG_TAG, "setCurrentCall : "+currentCall);
		this.currentCall = currentCall;
	}

	public boolean isCurrentCall(BuzBizCall call) {
		return currentCall == call;
	}
}