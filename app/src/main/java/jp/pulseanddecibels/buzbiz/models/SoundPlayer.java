package jp.pulseanddecibels.buzbiz.models;

import jp.pulseanddecibels.buzbiz.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

/**
 *
 * サウンドプレイヤー
 *
 *
 */
public enum SoundPlayer {
	INSTANCE;




	private MediaPlayer player;

	private final Object look = new Object();





	/**
	 * 音を鳴らす
	 * @param context			コンテクスト
	 * @param uri				鳴らす音
	 * @param audioStreamType	出力ストリームのタイプ
	 * @param isloop			ループするかどうか
	 */
	private void start(Context context, Uri uri, int audioStreamType, boolean isloop) {
		synchronized (look) {
			// なっていれば止める
			stop();

			// 呼び出し音を受話スピーカーより鳴らす
			try {
				player = new MediaPlayer();
				player.setDataSource(context, uri);
				player.setAudioStreamType(audioStreamType);
				player.setLooping(isloop);
				player.prepare();
				player.start();
			} catch (Exception ex) {
				ex.getStackTrace();
			}
		}
	}





	/**
	 * 音をストップさせる
	 */
	public void stop() {
		synchronized (look) {
			try{
				if (player == null) {
					return;
				}
				if (player.isPlaying()) {
					player.stop();
				}
				player.release();
				//System.gc();
			} catch (Exception ex) {
				ex.getStackTrace();
			} finally{
				player = null;
			}
		}
	}





	/**
	 * マナーモードか確認する
	 */
	private boolean isMannersMode(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		// ノーマルモードであればマナーモードとみなさない
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			return false;
		// サイレントモード , バイブレートモードであればマナーモードとみなす
		} else {
			return true;
		}
	}





	/**
	 * 発信準備音をスタートさせる
	 */
	public void startHassinjyunbi(Context context){
		start(context,
			  Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.hassinjyunbi),
			  AudioManager.STREAM_VOICE_CALL,
			  true);
	}





	/**
	 * 発信音をスタートさせる
	 */
	public void startHassin(Context context){
		start(context,
			  Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.hassin),
			  AudioManager.STREAM_VOICE_CALL,
			  true);
	}





	/**
	 * 通話終了音をスタートさせる
	 */
	public void startSyuuryou(Context context) {
		start(context,
			  Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.syuuryou),
			  AudioManager.STREAM_VOICE_CALL,
			  false);
	}





	/**
	 * 着信音を鳴らす
	 */
	public void startCyakusin(Context context) {
		// マナーモードの場合、着信音は鳴らさない
		if (isMannersMode(context)) {
			return;
		}

		start(context,
				Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.chakushin),
			  AudioManager.STREAM_RING,
			  true);
	}
}