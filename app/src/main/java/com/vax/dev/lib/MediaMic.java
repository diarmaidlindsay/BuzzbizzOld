package com.vax.dev.lib;



import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;





public class MediaMic {

    static final int RECORDER_SAMPLERATE = 16000;

    static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    static final int UNIT_PCM_SIZE = 640;

    boolean m_bMuteMic = false;

    final VaxSIPUserAgent m_objVaxSIPUserAgent;

    boolean m_bIsRecording = false;

    Thread m_objMicThread = null;

    Thread m_objTickThread = null;

    CircularBUFF m_objCirculareBUFF = null;





    public MediaMic(VaxSIPUserAgent objVaxSIPUserAgent) {
        m_objVaxSIPUserAgent = objVaxSIPUserAgent;
    }





    public void OpenMic() {
        System.gc();

        m_objCirculareBUFF = new CircularBUFF();
        m_objCirculareBUFF.SetSizeBUFF(UNIT_PCM_SIZE * 20);

        m_objMicThread = new Thread(new RunableMicThread());
        m_objMicThread.start();

        m_objTickThread = new Thread(new RunableTickThread());
        m_objTickThread.start();
    }





    class RunableMicThread implements Runnable {

        public void run() {
            int nMinBuffSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING) * 2;
            AudioRecord objRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, nMinBuffSize);
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
            byte[] aMicBuff = new byte[nMinBuffSize];

            while (m_bIsRecording) {
                int nMicReadSize = objRecorder.read(aMicBuff, 0, nMinBuffSize);

                if (m_bIsRecording == false) {
                    break;
                }
                if (nMicReadSize == 0) {
                    continue;
                }

                if (m_bMuteMic) {
                    byte[] aDataSilence = new byte[nMinBuffSize];
                    m_objCirculareBUFF.WriteToBUFF(aDataSilence, nMinBuffSize);
                } else {
                    m_objCirculareBUFF.WriteToBUFF(aMicBuff, nMinBuffSize);
                }
            }

            objRecorder.stop();
            objRecorder.release();
            objRecorder = null;
        }
    }





    class RunableTickThread implements Runnable {

        public void run() {
            m_bIsRecording = true;

            while (m_bIsRecording) {
                OnTimerTick();
            }
        }
    }





    public void CloseMic() {
        m_bIsRecording = false;
        Sleep(200);
    }


    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////





    private void OnTimerTick() {
        if (m_bIsRecording == false)
            return;

        if (!m_objCirculareBUFF.IsAvailable(UNIT_PCM_SIZE))
            return;

        byte[] aDatCopied = new byte[UNIT_PCM_SIZE];

        m_objCirculareBUFF.ReadToBUFF(aDatCopied, UNIT_PCM_SIZE);
        m_objVaxSIPUserAgent.OnMicData(aDatCopied, UNIT_PCM_SIZE);
    }





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