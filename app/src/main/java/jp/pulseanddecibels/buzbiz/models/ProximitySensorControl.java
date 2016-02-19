package jp.pulseanddecibels.buzbiz.models;

import java.util.List;

import jp.pulseanddecibels.buzbiz.KeypadScreen;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
//import android.util.Log;

/**
 *
 * 通話中の近接センサー制御クラス
 *
 *
 */
public class ProximitySensorControl {
	/** 近接センサー用リスナー */
	private static SensorEventListener myProximitySensorListener;





	/**
	 * 近接センサーをスタートさせる
	 * @param context コンテキスト
	 */
	public static void start(final Context context){
//		Log.e(Util.LOG_TAG,"  近接センサー.start  ");


		// 既にリスナーが存在する場合は、何もしない
		if(myProximitySensorListener != null){
			return;
		}

		// センサーマネジャーを取得
		SensorManager sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		// 近接センサーを取得
		List<Sensor> myProximitySensors = sm.getSensorList(Sensor.TYPE_PROXIMITY);

		// 近接センサーが存在しない場合は終了
		if (myProximitySensors.size() < 1) {
			return;
		}

		// 近接センサーに画面制御用リスナーをセット
		myProximitySensorListener = makeListener(context);
		for (Sensor sensor : myProximitySensors) {
			sm.registerListener(myProximitySensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
		}
	}





	/**
	 * 近接センサーをストップさせる
	 * @param context コンテキスト
	 */
	public static void stop(Context context){
//		Log.e(Util.LOG_TAG,"  近接センサー.stop  ");


		KeypadScreen.unlockFromProximitySensor();
		powerOffEnd();

		// リスナーが存在しない場合は終了
		if(myProximitySensorListener == null){
			return;
		}

		// リスナーを削除
		SensorManager sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sm.unregisterListener(myProximitySensorListener);
		myProximitySensorListener = null;
	}





	/**
	 * 画面の点灯制御用センサーイベントリスナーを作成
	 * @param context
	 * @return
	 */
	private static SensorEventListener makeListener(final Context context){
		return new SensorEventListener(){
			// 電源マネージャーの取得
			PowerManager pm= (PowerManager) context.getSystemService(Context.POWER_SERVICE);

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// 近づいたら画面OFF
				if (event.values[0] < 5) {
					KeypadScreen.lockFromProximitySensor();
					powerOff(pm);

				// 離れたら画面OFF終了
				}else {
					KeypadScreen.unlockFromProximitySensor();
					powerOffEnd();
				}
			}
		};
	}





	/** デバイスの電源状態をコントロールするためのインスタンス */
	private static PowerManager.WakeLock myWakeLock;


	/**
	 * 画面の点灯OFF
	 */
	private static void powerOff(PowerManager pm) {
		powerOffEnd();
		try{
			myWakeLock = pm.newWakeLock(32, "lock");	// PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK が 32
			myWakeLock.acquire();
		} catch (Exception e) {
			powerOffEnd();
		}
	}





	/**
	 * 画面の点灯Offを終了させる
	 */
	private static void powerOffEnd() {
		if (myWakeLock == null) {
			return;
		}

		try {
			myWakeLock.release();
			myWakeLock = null;
		} catch (Exception e) { }
	}
}