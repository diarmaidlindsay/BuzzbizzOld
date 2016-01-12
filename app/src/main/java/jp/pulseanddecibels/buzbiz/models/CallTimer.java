package jp.pulseanddecibels.buzbiz.models;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import jp.pulseanddecibels.buzbiz.MainActivity;
import jp.pulseanddecibels.buzbiz.KaypadScreen;
import jp.pulseanddecibels.buzbiz.MainService;

/**
 *
 * 通話時の時間計測クラス
 *
 *
 */
public class CallTimer {
//	/** タイマー */
//	private static Timer timer = null;
	private static long startTime;
	private static long callTime;

	private static final SimpleDateFormat shortTimeFormat	= new SimpleDateFormat("mm:ss",		Locale.JAPANESE);
	private static final SimpleDateFormat longTimeFormat	= new SimpleDateFormat("HH:mm:ss",	Locale.JAPANESE);





	static {
		shortTimeFormat	.setTimeZone(TimeZone.getTimeZone("GMT"));
		longTimeFormat	.setTimeZone(TimeZone.getTimeZone("GMT"));
	}





	/**
	 * サーバ死活監視タイマースタート
	 */
	public static void start(){
		// ２重起動させないために、１度ストップさせる
		startTime = 0;
		stop();

		startTime = System.currentTimeMillis();

		final Runnable run = new Runnable() {
			@Override
			public void run() {
				if (!MainService.isCalling()) {
					MainService.LIB_OP.endCall();
					return;
				}


				long currentTime = System.currentTimeMillis();
				long diff = currentTime - startTime;


				if (diff >= 3600000) {
					KaypadScreen.setDisplayCallerTimer(longTimeFormat.format(diff));
				} else {
					KaypadScreen.setDisplayCallerTimer(shortTimeFormat.format(diff));
				}

				MainActivity.getHandler().postDelayed(this, 1000);
			}
		};
		MainActivity.getHandler().postDelayed(run, 1000);
	}





	/**
	 * 通話タイマーストップ
	 */
	public static void stop(){
		if (startTime != 0) {
			long endTime = System.currentTimeMillis();
			callTime = endTime - startTime;
		}
	}





	/**
	 * 通話時間を表示
	 */
	public static void showCallTime(){
		if(callTime < 1000 || startTime == 0){
			startTime	= 0;
			callTime	= 0;
			return;
		}


		if (callTime >= 3600000) {
			MainActivity.displayMessage(MainService.me.getApplicationContext(),
										"通話時間 " + longTimeFormat.format(callTime));
		} else {
			MainActivity.displayMessage(MainService.me.getApplicationContext(),
										"通話時間 " + shortTimeFormat.format(callTime));
		}

		startTime	= 0;
		callTime	= 0;
	}
}