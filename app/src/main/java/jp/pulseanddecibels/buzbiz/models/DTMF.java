package jp.pulseanddecibels.buzbiz.models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import jp.pulseanddecibels.buzbiz.KaypadScreen;
import jp.pulseanddecibels.buzbiz.MainActivity;
import jp.pulseanddecibels.buzbiz.MainService;
import jp.pulseanddecibels.buzbiz.data.DtmfCode;


public class DTMF {


	public static boolean flag = true;


	/**
	 * サーバ死活監視タイマースタート
	 */
	public static void start() {
		// ２重起動させないために、１度ストップさせる

		//TODO DTMF
		Log.d("AAAAAAA", "BBBBBBBBBBBBBBBBBB");

//		if (flag) {
//			Log.d("AAAAAAA", "CCCCCCCCCCCCCCCC");
//			flag = false;
//
//			final Runnable run5 = new Runnable() {
//				@Override
//				public void run() {
//					new DTMF().sleep("5");
//				}
//			};
//
//			final Runnable run1 = new Runnable() {
//				@Override
//				public void run() {
//					new DTMF().sleep("1");
//				}
//			};
//
//			final Runnable run0 = new Runnable() {
//				@Override
//				public void run() {
//					new DTMF().sleep("0");
//				}
//			};
//
//
//
//			MainActivity.getHandler().postDelayed(run5, 2000);
//
//			MainActivity.getHandler().postDelayed(run1, 2200);
//
//			MainActivity.getHandler().postDelayed(run0, 2400);
//
//			MainActivity.getHandler().postDelayed(run1, 2600);
//		}
	}

	public synchronized void sleep(String str)
	{
//		try
//		{
//			wait(2000);

			DtmfCode dtmf = DtmfCode.chengeToDtmfCode(str);
			MainService.LIB_OP.sendDtmf(dtmf);

			Log.d("AAAAAAA", str);


//		}catch(InterruptedException e){}
	}


}