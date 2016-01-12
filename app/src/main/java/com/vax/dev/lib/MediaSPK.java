package com.vax.dev.lib;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MediaSPK extends Thread
{
	private static final int RECORDER_SAMPLERATE = 16000;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
			
	Handler mHandler;
	
	boolean m_bMuteSpk = false;
	AudioTrack m_objAudioTrack = null;
	
	boolean m_bStopThread = false;
	int m_nPostPlayCount = 0;
	
	public MediaSPK()
	{
		super.start();
		super.setPriority(MAX_PRIORITY);
	}
	
	public void run() 
	{
		Looper.prepare();
				
        mHandler = new Handler() 
        {
            public void handleMessage(Message msg) 
            {
            }
        };

        Looper.loop();
    }
	

	class OnOpenSpk implements Runnable 
	{
		public void run() 
		{
			m_nPostPlayCount = 0;
			int nMinBuffSize = AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
			m_objAudioTrack = new  AudioTrack(AudioManager.STREAM_VOICE_CALL, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, nMinBuffSize, AudioTrack.MODE_STREAM);
			m_objAudioTrack.play();
			
			RunnableNotify(this);
		}
	};
	
	public void OpenSpk() 
	{
		m_bStopThread = false;
		
		OnOpenSpk objOpenSpk = new OnOpenSpk(); 
		
		mHandler.post(objOpenSpk);
		RunnableWait(objOpenSpk);
	}


	class OnPlaySpk implements Runnable 
	{
		public byte[] m_aDataPCM = null;
		public int m_nSizePCM;
				
		public void run() 
		{
			if(m_bStopThread || m_objAudioTrack == null)
				return;
			
			m_nPostPlayCount--;
			
			if(m_nPostPlayCount > 4)
				return;
			
			m_objAudioTrack.write(m_aDataPCM, 0, m_nSizePCM);
		}
	};

	public void PlaySpk(byte[] aData, int nDataSize)
	{
		if(m_bStopThread || m_objAudioTrack == null)
			return;
		
		OnPlaySpk objPlaySpk = new OnPlaySpk();
		
		objPlaySpk.m_aDataPCM = new byte[nDataSize];
		objPlaySpk.m_nSizePCM = nDataSize;
		
		if(!m_bMuteSpk)
		{
			System.arraycopy(aData, 0, objPlaySpk.m_aDataPCM, 0, nDataSize);
		}
		
		m_nPostPlayCount++;
		
		mHandler.post(objPlaySpk);
	}
	

	class OnCloseSpk implements Runnable 
	{
		public void run() 
		{
			if(m_objAudioTrack != null)
			{
				m_objAudioTrack.stop();
				m_objAudioTrack.release();
				m_objAudioTrack = null;
			}
			
			RunnableNotify(this);
		}
	};
	public void CloseSpk() 
	{
		m_bStopThread = true;
//		Sleep(200);
		Sleep(20);
		
		OnCloseSpk objCloseSpk = new OnCloseSpk();
		
		mHandler.post(objCloseSpk);
		RunnableWait(objCloseSpk);
	}

	public void Mute(boolean bEnable)
    {
    	m_bMuteSpk = bEnable;
    }
	
	void RunnableNotify(Runnable runnable)
	{
//		Sleep(50);
		Sleep(5);
		
		synchronized(runnable)
		{
			runnable.notify();
		}
	}
	
	void RunnableWait(Runnable runnable)
	{
		try 
		{
			synchronized(runnable)
			{
				runnable.wait();
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	private void Sleep(int nMilliSec)
	{
		try 
		{
			Thread.sleep(nMilliSec);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}
