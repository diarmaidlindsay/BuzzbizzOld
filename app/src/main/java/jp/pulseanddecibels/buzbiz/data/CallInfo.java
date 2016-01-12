package jp.pulseanddecibels.buzbiz.data;

import android.text.TextUtils;
import jp.pulseanddecibels.buzbiz.util.Util;

/**
 *
 * 通話中情報保存クラス
 *
 *
 */
public enum CallInfo {
	INSTANCE;


	/** キーパッド入力された電話番号 */
	public String inputTelNum			= Util.STRING_EMPTY;


	/** 通話の情報（表示用）の名前部分 */
	public String displayCallerName		= Util.STRING_EMPTY;
	/** 通話の情報（表示用）の電話番号部分 */
	public String displayCallerNumber	= Util.STRING_EMPTY;
	/** 通話相手の電話番号 */
	public String callerNumber			= Util.STRING_EMPTY;

	/**
	 * データの初期化
	 */
	public static void ClearData(){
		INSTANCE.callerNumber			= Util.STRING_EMPTY;
		INSTANCE.displayCallerName		= Util.STRING_EMPTY;
		INSTANCE.displayCallerNumber	= Util.STRING_EMPTY;
	}


	/**
	 * キーパッド入力による電話番号が存在するか確認
	 * @return
	 */
	public static boolean ExistInputTelNum() {
		if (TextUtils.isEmpty(INSTANCE.inputTelNum)) {
			return false;
		}
		return true;
	}
}