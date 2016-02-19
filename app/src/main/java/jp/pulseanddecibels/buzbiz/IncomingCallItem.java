package jp.pulseanddecibels.buzbiz;

import jp.pulseanddecibels.buzbiz.pjsip.BuzBizCall;
import jp.pulseanddecibels.buzbiz.util.Logger;
import jp.pulseanddecibels.buzbiz.util.Util;
import jp.pulseanddecibels.buzbiz.data.TelNumber;

/**
 *
 * 着信情報
 *
 *
 */
public class IncomingCallItem {
	/** 各着信コールの一意の識別子 */
	public final String callId;
	/** 表示名 */
	public final String displayName;
	/** 電話番号 */
	public final TelNumber telNum;

	public final String fromURI;
	public final String toURI;

	//if call is not answered, should we dispose of this?
	public final BuzBizCall call;


	/** 表示用ラベル */
	public String label = Util.STRING_EMPTY;





	/**
	 * コンストラクタ
	 */
	public IncomingCallItem(String callId,
							String displayName,
							TelNumber userName,
							String fromURI,
							String toURI,
							String label,
							BuzBizCall call) {

		this.callId 	 = callId;
		this.displayName = displayName;
		this.telNum 	 = userName;
		this.fromURI 	 = fromURI;
		this.toURI 		 = toURI;
		this.label 		 = label;
		this.call	     = call;

		// debug();
	}





	/**
	 * デッバッグ用
	 */
	@SuppressWarnings("unused")
	private void debug() {
        Logger.e("callId " + callId);
        Logger.e("displayName " + displayName);
        Logger.e("telNum "		+ telNum.getBaseString());
        Logger.e("fromURI "		+ fromURI);
        Logger.e("toURI "		+ toURI);
	}





	/**
	 * 表示用情報を取得
	 * @return
	 */
	public String getDisplayInfo() {
		return label;
	}





	/**
	 * 新しいインスタンスで、コピーを入手する
	 */
	public IncomingCallItem getNewCopy() {
		return new IncomingCallItem(callId,
									displayName,
									telNum,
									fromURI,
									toURI,
									label,
								    call);
	}
}