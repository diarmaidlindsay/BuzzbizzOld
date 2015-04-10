package jp.pulseanddecibels.buzbiz_onpre;

import jp.pulseanddecibels.buzbiz_onpre.data.TelNumber;
import jp.pulseanddecibels.buzbiz_onpre.util.Logger;
import jp.pulseanddecibels.buzbiz_onpre.util.Util;

/**
 *
 * 着信情報
 *
 * @author 普天間
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
							String label) {

		this.callId 	 = callId;
		this.displayName = displayName;
		this.telNum 	 = userName;
		this.fromURI 	 = fromURI;
		this.toURI 		 = toURI;
		this.label 		 = label;

		// debug();
	}





	/**
	 * デッバッグ用
	 */
	@SuppressWarnings("unused")
	private void debug() {
        Logger.e("callId "		+ callId);
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
									label);
	}
}