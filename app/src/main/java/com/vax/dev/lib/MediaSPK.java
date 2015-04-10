package com.vax.dev.lib;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

public class MediaSPK
{
	private static final int RECORDER_SAMPLERATE = 16000;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

// 解決した為コメントアウト      注意	：	ここが16000だとhtc j oneで遅延が発生する
//	static {
//		if(android.os.Build.MODEL.equals("HTL22")){	// htc j one
//			RECORDER_SAMPLERATE = 15999;
//		}else{
//			RECORDER_SAMPLERATE = 16000;
//		}
//	}


	final VaxSIPUserAgent m_objVaxSIPUserAgent;

	boolean m_bMuteSpk = false;

	int m_nMinBuffSize = 0;

	AudioTrack m_objAudioTrack = null;

	public MediaSPK(VaxSIPUserAgent objVaxSIPUserAgent)
	{
		m_objVaxSIPUserAgent = objVaxSIPUserAgent;
	}

	public void OpenSpk()
	{
//		try {
//			m_nMinBuffSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
//			m_objAudioTrack = new  AudioTrack(AudioManager.STREAM_VOICE_CALL,
//											  RECORDER_SAMPLERATE,
//											  AudioFormat.CHANNEL_OUT_MONO,
//											  RECORDER_AUDIO_ENCODING,
//											  m_nMinBuffSize,
//											  AudioTrack.MODE_STREAM);
//
//		} catch (IllegalStateException ex) {
//			m_nMinBuffSize = AudioRecord.getMinBufferSize(16000, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
//			m_objAudioTrack = new  AudioTrack(AudioManager.STREAM_VOICE_CALL, 16000, AudioFormat.CHANNEL_OUT_MONO, RECORDER_AUDIO_ENCODING, m_nMinBuffSize, AudioTrack.MODE_STREAM);
//		}

		m_nMinBuffSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		m_objAudioTrack = new  AudioTrack(AudioManager.STREAM_VOICE_CALL,
										  RECORDER_SAMPLERATE,
										  AudioFormat.CHANNEL_OUT_MONO,
										  RECORDER_AUDIO_ENCODING,
										  m_nMinBuffSize,
										  AudioTrack.MODE_STREAM);

		m_objAudioTrack.play();
	}

	public void PlaySpk(byte[] aData, int nDataSize)
	{
		if(m_bMuteSpk)
		{
			byte[] aDataSilence = new byte[nDataSize];
			try{
				m_objAudioTrack.write(aDataSilence, 0, nDataSize);
			}catch(Exception ex){
				ex.printStackTrace();
				android.util.Log.e("MediaSPK", ex.toString());
			}
		}
		else
		{
			try{
				m_objAudioTrack.write(aData, 0, nDataSize);
			}catch(Exception ex){
				ex.printStackTrace();
				android.util.Log.e("MediaSPK", ex.toString());
			}
		}
	}

	public void Mute(boolean bEnable)
    {
    	m_bMuteSpk = bEnable;
    }

	public void CloseSpk()
	{
		if(m_objAudioTrack == null)
			return;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) { }

				try
				{
					m_objAudioTrack.stop();
					m_objAudioTrack.release();

					m_objAudioTrack = null;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					android.util.Log.e("MediaSPK", ex.toString());
				}
			}
		}).start();
	}
}
