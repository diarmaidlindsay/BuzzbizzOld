package com.vax.dev.lib;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
//import android.util.Log;

public class MediaMic 
{

	//このクラスのsleeを全コメントアウトするとcp使用率が15から30に上がった
	//このクラスのsleeを全て1/10にするときれい
	static final int RECORDER_SAMPLERATE = 16000; //2000->強制狩猟 8000-ビジホ、スマホ共に切れ切れで聞こえる 32000->切れ切れ 64000->強制終了
//	static final int RECORDER_SAMPLERATE = 16000;
    static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    
    static final int UNIT_PCM_SIZE = 640;
    
    boolean m_bMuteMic = false;
    VaxSIPUserAgent m_objVaxSIPUserAgent;
    
    boolean m_bIsRecording = false;
       
    Handler m_objHandler = new Handler(Looper.myLooper());
    
    Thread m_objMicThread = null;
    Thread m_objTickThread = null;
    
    CircularBUFF m_objCirculareBUFF = null;
        
    public MediaMic(VaxSIPUserAgent objVaxSIPUserAgent)
	{
    	m_objVaxSIPUserAgent = objVaxSIPUserAgent;
	}
     
    public void OpenMic()
    {
    	m_objCirculareBUFF = new CircularBUFF();
    	m_objCirculareBUFF.SetSizeBUFF(UNIT_PCM_SIZE * 20);
    	
    	m_objMicThread = new Thread(new RunableMicThread());
		m_objMicThread.start(); 
		
		m_objTickThread = new Thread(new RunableTickThread());
		m_objTickThread.start();   
    }
    
    class RunableMicThread implements Runnable 
	{
    	public void run() 
		{
    		int nMinBuffSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    		AudioRecord objRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, nMinBuffSize);    
        	objRecorder.startRecording();
        	        	        	        	        	        	
        	m_bIsRecording = true;
        	byte[] aMicBuff = new byte[nMinBuffSize];
        	
    		while(m_bIsRecording)
            {
    			int nMicReadSize = objRecorder.read(aMicBuff, 0, nMinBuffSize);
    			
    			if(m_bIsRecording == false)
    			{
    				break;
    			}
    			if(nMicReadSize == 0)
    			{
    				continue;
    			}
    			
    			PostMicData(aMicBuff, nMicReadSize);
            }
    		
    		objRecorder.stop();
    		objRecorder.release();
		}
	}; 
	
	
	class RunableTickThread implements Runnable 
	{
    	public void run() 
		{
    		m_bIsRecording = true;
        	        	
    		while(m_bIsRecording)
            {
    			Sleep(20);
//				Sleep(2);//11271140からコメントアウト
//				Sleep(4);
    			
    			if(m_bIsRecording == false)
    			{
    				break;
    			}
    			
    			PostTimerTick();
    		}
		}
	}; 
	
    public void CloseMic()
    {
    	m_bIsRecording = false;
        Sleep(200);
//		Sleep(20);//11271140からコメントアウト
//		Sleep(40);
    }
    
    class RunableMicData implements Runnable 
 	{
 	    byte[] m_sMicData = null;
 	    int m_nMicDataSize = 0;
 	     	    
 	   RunableMicData(byte[] aData, int nDataSize) 
 	   { 
 	    	m_sMicData = new byte[nDataSize];
 	    	System.arraycopy(aData, 0, m_sMicData, 0, nDataSize);
 	    	
 	    	m_nMicDataSize = nDataSize;
 	    }
 	    
 	    public void run() 
 	    {
 	    	if(m_bMuteMic)
 			{
 				byte[] aDataSilence = new byte[m_nMicDataSize];
 				m_objCirculareBUFF.WriteToBUFF(aDataSilence, m_nMicDataSize);
 			}
 			else
 			{
 				m_objCirculareBUFF.WriteToBUFF(m_sMicData, m_nMicDataSize);
 			}
 	    }
 	}
   
    private void PostMicData(byte[] aData, int nDataSize)
    {
    	RunableMicData rRun = new RunableMicData(aData, nDataSize);
		m_objHandler.post(rRun);
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
   
    private void OnTimerTick()
    {
    	if(m_bIsRecording == false)
    		return;
    	
    	if(!m_objCirculareBUFF.IsAvailable(UNIT_PCM_SIZE))
    		return;
			
    	byte[] aDatCopied = new byte[UNIT_PCM_SIZE];
			
    	m_objCirculareBUFF.ReadToBUFF(aDatCopied, UNIT_PCM_SIZE);
    	m_objVaxSIPUserAgent.OnMicData(aDatCopied, UNIT_PCM_SIZE);
    }
    
    class RunableTickData implements Runnable 
 	{
 	    public void run() 
 	    {
 	    	OnTimerTick();
 	    }
 	}
    
    private void PostTimerTick()
    {
    	RunableTickData rRun = new RunableTickData();
		m_objHandler.post(rRun);
    }
    
    public void Mute(Boolean bEnable)
    {
    	m_bMuteMic = bEnable;
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