package jp.pulseanddecibels.buzbiz_onpre.models;

import jp.pulseanddecibels.buzbiz_onpre.util.Util;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *
 * データ保存用ファイル
 *
 * @author 普天間
 *
 */
public class File {

	private final static String PREFERENCES_FILE_NAME	= "BUZBIZPreferencesFile";	// プレファレンスファイル名



	public static final String TAB_POSITION		= "TAB_POSITION";
	public static final String TAB_OPERATION	= "TAB_OPERATION";





	/**
	 * プレファレンスファイルに保存する
	 * @param context	コンテキスト
	 * @param key		キー
	 * @param value		値
	 */
	public static void saveData(Context context, String key, String value) {
		SharedPreferences saveFile = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);

		Editor saveFileEditor = saveFile.edit();
		if (saveFileEditor == null) {
			return;
		}

		saveFileEditor.putString(key, value);
		saveFileEditor.commit();
	}





	/**
	 * プレファレンスファイルに保存する
	 * @param context	コンテキスト
	 * @param key		キー
	 * @param value		値
	 */
	public static void saveData(Context context, String key, int value) {
		SharedPreferences saveFile = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);

		Editor saveFileEditor = saveFile.edit();
		if (saveFileEditor == null) {
			return;
		}

		saveFileEditor.putInt(key, value);
		saveFileEditor.commit();
	}





	/**
	 * プレファレンスファイルよりキーをもとに値を取得する
	 * @param context	コンテキスト
	 * @param key		キー
	 * @return			値
	 */
	public static String getValue(Context context, String key) {
		SharedPreferences saveFile = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		String str = saveFile.getString(key, Util.STRING_EMPTY);
		return str.trim();
	}





	/**
	 * プレファレンスファイルよりキーをもとに値を取得する
	 * @param context	コンテキスト
	 * @param key		キー
	 * @return			値
	 */
	public static int getInt(Context context, String key) {
		SharedPreferences saveFile = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		return saveFile.getInt(key, Util.INT_EMPTY);
	}
}