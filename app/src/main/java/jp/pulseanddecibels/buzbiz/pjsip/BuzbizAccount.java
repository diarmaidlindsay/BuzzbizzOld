package jp.pulseanddecibels.buzbiz.pjsip;

import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.OnIncomingCallParam;

import jp.pulseanddecibels.buzbiz.MainService;

/**
 * Created by Diarmaid Lindsay on 2016/01/25.
 * Copyright Pulse and Decibels 2016
 *
 * PJSIP Account sub-class
 * Redirect incoming calls to MainService method.
 */
public class BuzbizAccount extends Account {
    private String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        super.onIncomingCall(prm);
        Log.e(LOG_TAG, "onIncomingCall");
        BuzBizCall call = new BuzBizCall(this, prm.getCallId());
        //force delete to ensure we don't get PJSIP non-registered thread crash
        prm.delete();
        //send Call object to MainService
        MainService.me.onIncomingCall(call);
    }

    /*
    @Override
    public void onRegStarted(OnRegStartedParam prm) {
        super.onRegStarted(prm);
        Log.d(LOG_TAG, "onRegStarted : " + prm.getRenew());
        prm.delete();
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);
        Log.d(LOG_TAG, "onRegState : " + prm.getCode().toString());
        Log.d(LOG_TAG, "onRegState : " + prm.getReason());
        prm.delete();
    }
    */
}
