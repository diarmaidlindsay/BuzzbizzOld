//package com.vax.dev.lib;
//
//
//
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//
//
//
//
//
//
//public class MediaMic {
//
//    static final int RECORDER_SAMPLERATE = 16000;
//
//    static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
//
//    static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
//
//    static final int UNIT_PCM_SIZE = 640;
//
//    boolean m_bMuteMic = false;
//
//    final VaxSIPUserAgent m_objVaxSIPUserAgent;
//
//    boolean m_bIsRecording = false;
//
//    Thread m_objMicThread = null;
//
//    Thread m_objTickThread = null;
//
//    CircularBUFF m_objCirculareBUFF = null;
//
//
//
//
//
//    public MediaMic(VaxSIPUserAgent objVaxSIPUserAgent) {
//        m_objVaxSIPUserAgent = objVaxSIPUserAgent;
//    }
//
//
//
//
//
//    public void OpenMic() {
//        System.gc();
//
//        m_objCirculareBUFF = new CircularBUFF();
//        m_objCirculareBUFF.SetSizeBUFF(UNIT_PCM_SIZE * 20);
//
//        m_objMicThread = new Thread(new RunableMicThread());
//        m_objMicThread.start();
//
//        m_objTickThread = new Thread(new RunableTickThread());
//        m_objTickThread.start();
//    }
//
//
//
//
//
//    class RunableMicThread implements Runnable {
//
//        public void run() {
//            int nMinBuffSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING) * 2;
//            AudioRecord objRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, nMinBuffSize);
//
//            try {
//                objRecorder.startRecording();
//            } catch (Exception ex) {
//                android.util.Log.e("BUZBIZ", ex.toString());
//                try {
//                    objRecorder.release();
//                    objRecorder = null;
//                } catch (Exception ex2) { }
//                return;
//            }
//
//            m_bIsRecording = true;
//            byte[] aMicBuff = new byte[nMinBuffSize];
//
//            while (m_bIsRecording) {
//                int nMicReadSize = objRecorder.read(aMicBuff, 0, nMinBuffSize);
//
//                if (m_bIsRecording == false) {
//                    break;
//                }
//                if (nMicReadSize == 0) {
//                    continue;
//                }
//
//                if (m_bMuteMic) {
//                    byte[] aDataSilence = new byte[nMinBuffSize];
//                    m_objCirculareBUFF.WriteToBUFF(aDataSilence, nMinBuffSize);
//                } else {
//                    m_objCirculareBUFF.WriteToBUFF(aMicBuff, nMinBuffSize);
//                }
//            }
//
//            objRecorder.stop();
//            objRecorder.release();
//            objRecorder = null;
//        }
//    }
//
//
//
//
//
//    class RunableTickThread implements Runnable {
//
//        public void run() {
//            m_bIsRecording = true;
//
//            while (m_bIsRecording) {
//                Sleep(10);
//                OnTimerTick();
//            }
//        }
//    }
//
//
//
//
//
//    public void CloseMic() {
//        m_bIsRecording = false;
//        Sleep(200);
//    }
//
//
//    ///////////////////////////////////////////////////////////////
//    ///////////////////////////////////////////////////////////////
//
//
//
//
//
//    private void OnTimerTick() {
//        if (m_bIsRecording == false)
//            return;
//
//        if (!m_objCirculareBUFF.IsAvailable(UNIT_PCM_SIZE))
//            return;
//
//        byte[] aDatCopied = new byte[UNIT_PCM_SIZE];
//
//        m_objCirculareBUFF.ReadToBUFF(aDatCopied, UNIT_PCM_SIZE);
//        m_objVaxSIPUserAgent.OnMicData(aDatCopied, UNIT_PCM_SIZE);
//    }
//
//
//
//
//
//    public void Mute(Boolean bEnable) {
//        m_bMuteMic = bEnable;
//    }
//
//
//
//
//
//    private void Sleep(int nMilliSec) {
//        try {
//            Thread.sleep(nMilliSec);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}

// ↑ はCircularBUFFを使ったコード
//
// 通話中に落ちてしまうためコメントアウト























package com.vax.dev.lib;



import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.lang.Boolean;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Runnable;
import java.lang.Thread;





public class MediaMic {

    static final int RECORDER_SAMPLERATE = 16000;

    static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    static final int MIN_BUFF_SIZE = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

    boolean m_bMuteMic = false;

    final VaxSIPUserAgent m_objVaxSIPUserAgent;

    //AudioRecord m_objRecorder = null;
    boolean m_bIsRecording = false;

    Thread m_objMicThread = null;
    // AudioTrack audioTrack = null;





    public MediaMic(VaxSIPUserAgent objVaxSIPUserAgent) {
        m_objVaxSIPUserAgent = objVaxSIPUserAgent;
    }





    public void OpenMic() {
        m_objMicThread = new Thread(new RunableMicThread());
        m_objMicThread.start();
    }





    class RunableMicThread implements Runnable {

        public void run() {
            AudioRecord objRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, MIN_BUFF_SIZE);
            try {
                objRecorder.startRecording();
            } catch (Exception ex) {
                android.util.Log.e("BUZBIZ", ex.toString());
                try {
                    objRecorder.release();
                    objRecorder = null;
                } catch (Exception ex2) {
                }
                return;
            }

            m_bIsRecording = true;
            byte[] aMicBuff = new byte[MIN_BUFF_SIZE];

            while (m_bIsRecording) {
                int nMicReadSize = objRecorder.read(aMicBuff, 0, MIN_BUFF_SIZE);

                if (m_bIsRecording == false) {
                    break;
                }
                if (nMicReadSize == 0) {
                    continue;
                }

                //------------------------------------------------
//                PostMicData(aMicBuff, nMicReadSize);	//	サンプルではこっちが実行され、下記がコメントアウトされていたが、ここを通ると遅延が発生する


                m_objVaxSIPUserAgent.OnMicData(aMicBuff, nMicReadSize);
                //------------------------------------------------
            }

            Sleep(200);

            objRecorder.stop();
            objRecorder.release();
            objRecorder = null;
        }
    }





    ;





    public void CloseMic() {
        m_bIsRecording = false;
        Sleep(200);
    }





//    class RunableMicData implements Runnable
//	{
//	    byte[] m_sMicData = null;
//	    int m_nMicDataSize = 0;
//
//	   RunableMicData(byte[] aData, int nDataSize)
//	   {
//	    	m_sMicData = new byte[nDataSize];
//	    	System.arraycopy(aData, 0, m_sMicData, 0, nDataSize);
//
//	    	m_nMicDataSize = nDataSize;
//	    }
//
//	    public void run()
//	    {
//	    	m_objVaxSIPUserAgent.OnMicData(m_sMicData, m_nMicDataSize);
//	    }
//	}
//
//    private void PostMicData(byte[] aData, int nDataSize)
//    {
//    	RunableMicData rRun = new RunableMicData(aData, nDataSize);
//		m_objHandler.post(rRun);
//    }





    public void Mute(Boolean bEnable) {
        m_bMuteMic = bEnable;
    }





    private void Sleep(int nMilliSec) {
        try {
            Thread.sleep(nMilliSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
