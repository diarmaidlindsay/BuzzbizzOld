package jp.pulseanddecibels.buzbiz.models;

import android.content.Context;
import android.os.Vibrator;

/**
 *
 * バイブレータのコントロール
 *
 *
 */
public class VibratorControl {
	private static Vibrator vibrator;

	/** 振動のパターン */
	private final static long vibratorPattern[] = {1000, 200, 1000, 200, 1000, 200 };





	/**
	 * バイブレータを初期化
	 * @param context
	 */
	private static void initVibrator(Context context) {
		if (vibrator == null) {
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		}
		vibrator.cancel();
	}





	/**
	 * バイブレータを開始
	 * @param context	コンテキスト
	 */
	public static void start(Context context) {
		initVibrator(context);
		vibrator.vibrate(vibratorPattern, 0);
	}





	/**
	 * バイブレータを終了
	 * @param context	コンテキスト
	 */
	public static void stop(Context context) {
		initVibrator(context);
	}
}