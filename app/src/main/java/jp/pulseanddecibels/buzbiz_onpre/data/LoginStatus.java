package jp.pulseanddecibels.buzbiz_onpre.data;

import jp.pulseanddecibels.buzbiz_onpre.models.File;
import android.content.Context;

/**
 *
 * サーバへのログイン状態
 *
 * @author 普天間
 *
 */
public class LoginStatus {
	private int code;


	/** ログイン中 */
	public static final int ONLINE	= 1;
	/** ログオフ */
	public static final int OFFLINE	= 0;
	/** ネットワークダウン */
	public static final int PING_NG	= -1;


	/** 保存用タブ */
	public static final String SAVE_TAB = "LOGIN_STATUS";





	/**
	 * コンストラクタ
	 * @param code	現在のログイン状態
	 */
	public LoginStatus(int code) {
		if (code < PING_NG || ONLINE < code) {
			this.code = OFFLINE;
		} else {
			this.code = code;
		}
	}





	public int toCode(){
		return code;
	}





	public boolean isOnLine(){
		if (code == ONLINE) {
			return true;
		} else {
			return false;
		}
	}





	/**
	 * ファイルに現在の状態を保存
	 */
	public void save(Context context){
		File.saveData(context, SAVE_TAB, code);
	}





	/**
	 * ファイルより状態をロード
	 */
	public static LoginStatus load(Context context){
		int savedCode = File.getInt(context, SAVE_TAB);
		return new LoginStatus(savedCode);
	}
}