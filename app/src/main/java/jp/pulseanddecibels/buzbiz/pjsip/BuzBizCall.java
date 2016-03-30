package jp.pulseanddecibels.buzbiz.pjsip;

import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnCallTransferStatusParam;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua_call_media_status;

import jp.pulseanddecibels.buzbiz.MainActivity;
import jp.pulseanddecibels.buzbiz.MainService;
import jp.pulseanddecibels.buzbiz.R;
import jp.pulseanddecibels.buzbiz.models.SoundPlayer;

/**
 * Created by Diarmaid Lindsay on 2016/01/25.
 * Copyright Pulse and Decibels 2016
 *
 * PJSIP Call subclass
 * This is where we handle most of the PJSIP events
 * PJSIPのイベントがほとんどここでハンドルされた
 */
public class BuzBizCall extends Call {
    protected BuzBizCall(long cPtr, boolean cMemoryOwn) {
        super(cPtr, cMemoryOwn);
    }

    public BuzBizCall(Account acc, int call_id) {
        super(acc, call_id);
    }

    public BuzBizCall(Account acc) {
        super(acc);
    }

    private String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void makeCall(String dst_uri, CallOpParam prm) throws Exception {
        super.makeCall(dst_uri, prm);
        //force delete to ensure we don't get PJSIP non-registered thread crash
        prm.delete();
        SoundPlayer.INSTANCE.startHassin(MainService.me.getApplicationContext());
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        super.onCallState(prm);
        //force delete to ensure we don't get PJSIP non-registered thread crash
        prm.delete();
        try {
            CallInfo ci = getInfo();
//            Log.e(LOG_TAG, "StatusCode : " + ci.getLastStatusCode());
//            Log.e(LOG_TAG, "onCallState : "+ci.getState().toString());
            //call connected event
            if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                MainService.setEventStartCall();
            }

            pjsip_status_code lastStatusCode;

            try {
                lastStatusCode = ci.getLastStatusCode();
                //wrong number/number not found event
                if(lastStatusCode == pjsip_status_code.PJSIP_SC_NOT_FOUND || lastStatusCode == pjsip_status_code.PJSIP_SC_ADDRESS_INCOMPLETE) {
                    MainActivity.displayMessage("お掛けになった電話番号は、存在しません");
                }
                //number busy event
                else if(lastStatusCode == pjsip_status_code.PJSIP_SC_BUSY_HERE && MainService.LIB_OP.isCurrentCall(this)) {
                    MainActivity.displayMessage("ご利用中です。お掛け直し下さい");
                }
            } catch (IllegalArgumentException e) {
                //do nothing
            }

            //call disconnected event
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED &&
                    //ongoing call is disconnected by either party or incoming call is disconnected by caller
                    (MainService.LIB_OP.isCurrentCall(this) || MainService.LIB_OP.isCurrentCall(null))
                    ) {
                MainService.setEventEndCall();
                MainService.OnIncomingCallRingingStop();
            }

            //force delete to ensure we don't get PJSIP non-registered thread crash
            ci.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Copied from sample code :
     * https://svn.pjsip.org/repos/pjproject/trunk/pjsip-apps/src/swig/java/android/src/org/pjsip/pjsua2/app/MyApp.java
     *
     * Event triggered whe n
     */
    @Override
    public void onCallMediaState(OnCallMediaStateParam prm)
    {
        //force delete to ensure we don't get PJSIP non-registered thread crash
        prm.delete();
        Log.e(LOG_TAG, "onCallMediaState");
        CallInfo ci;
        try {
            ci = getInfo();
        } catch (Exception e) {
            return;
        }

        CallMediaInfoVector cmiv = ci.getMedia();

        for (int i = 0; i < cmiv.size(); i++) {
            CallMediaInfo cmi = cmiv.get(i);
            //if audio call becomes active
            if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                    (cmi.getStatus() ==
                            pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                            cmi.getStatus() ==
                                    pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
            {
                // unfortunately, on Java too, the returned Media cannot be
                // downcasted to AudioMedia
                Media m = getMedia(i);
                AudioMedia am = AudioMedia.typecastFromMedia(m);
                m.delete();
                // connect ports
                try {
                    //start audio device
                    AudDevManager audDevManager = Endpoint.instance().audDevManager();
                    //on some devices (Sony Xperia) by default the wrong microphone was selected.
                    //"1" seems to work on all devices tested so far.
                    audDevManager.setCaptureDev(1);
                    audDevManager.getCaptureDevMedia().
                            startTransmit(am);
                    am.startTransmit(audDevManager.
                            getPlaybackDevMedia());
                } catch (Exception e) {
                    continue;
                }
            }
        }
        //force delete to ensure we don't get PJSIP non-registered thread crash
        cmiv.delete();
        ci.delete();
    }

    public void muteMic(boolean flag) {
        try {
            Log.e(LOG_TAG, "muteMic " + flag);
            AudDevManager audDevManager = Endpoint.instance().audDevManager();
            audDevManager.getCaptureDevMedia().adjustTxLevel(flag ? 0f : 1.0f);
            //audDevManager.setInputVolume(flag ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void muteAudio(boolean flag) {
        try {
            Log.e(LOG_TAG, "muteAudio "+flag);
            AudDevManager audDevManager = Endpoint.instance().audDevManager();
            audDevManager.setOutputVolume(flag ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCallTransferStatus(OnCallTransferStatusParam prm) {
        super.onCallTransferStatus(prm);
        if(prm.getStatusCode().equals(pjsip_status_code.PJSIP_SC_OK)) {
            //hang up when the call is put on hold (when its transferred)
            MainService.LIB_OP.endCall();
        }
        if(prm.getStatusCode().equals(pjsip_status_code.PJSIP_SC_SERVICE_UNAVAILABLE)) {
            //call is already being held by other party
            MainActivity.displayMessage(MainService.me.getString(R.string.hold_error));
        }
        Log.d(LOG_TAG, "onCallTransferStatus " + prm.getStatusCode().toString());
    }
}
