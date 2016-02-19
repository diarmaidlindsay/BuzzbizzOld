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
        prm.delete();
        MainService.me.onIncomingCall(call);
    }
}
