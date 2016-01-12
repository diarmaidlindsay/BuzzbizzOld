package jp.pulseanddecibels.buzbiz.data;

import android.text.TextUtils;

import jp.pulseanddecibels.buzbiz.util.Util;

/**
 *
 * 電話番号
 *
 *
 */
public class TelNumber {
	/** 元の文字列 */
	private final String baseString;





	/**
	 * コンストラクタ
	 */
	public TelNumber(String base) {
		if (!TextUtils.isEmpty(base)) {
			this.baseString = base;
		}else{
			this.baseString = Util.STRING_EMPTY;
		}
	}





	public String getBaseString() {
		return baseString;
	}





	public boolean isEmpty() {
		if (TextUtils.isEmpty(baseString) ||
			baseString.equals("null")) {
			return true;
		} else {
			return false;
		}
	}





	/**
	 * 非通知かを確認
	 */
	public boolean isHide(){
		if (TextUtils.isEmpty(baseString) ||
			baseString.equals("null") ||
			baseString.equals("anonymous")) {
			return true;
		} else {
			return false;
		}
	}





	/**
	 * 外線かを確認
	 */
	public boolean isExternal(){
		// 非通知は外線とみなさない
		if (isHide()) {
			return false;
		}

		//---------------------------------------------
		// 『判断基準』
		//		1文字目が0か1     ⇒  外線
		//		1文字目がそれ以外  ⇒  内線
		//---------------------------------------------
		char c = baseString.charAt(0);
		if (c == '0' || c == '1') {
			return true;
		} else {
			return false;
		}
	}





	/**
	 * 内線かを確認
	 */
	public boolean isInternal(){
		// 非通知は内線とみなさない
		if (isHide()) {
			return false;
		}

		char c = baseString.charAt(0);
		if ('2' <= c && c <= '9') {
			return true;
		} else {
			return false;
		}
	}





	/**
	 * 回線種別を文字列で取得
	 * @return
	 */
	public String getLineTypeString(){
		if (isExternal()) {
			return "外線";
		}
		if (isInternal()) {
			return "内線";
		}
		return "不明";
	}
}