package jp.pulseanddecibels.buzbiz;



import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;





/**
 * BUZBIZ用アプリケーションクラス
 *
 */
public class BuzbizApplication extends Application {

    // ----------- ライフサイクル -----------
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        startVolley();
    }


    @Override
    public void onTerminate() {
        stopVolley();

        super.onTerminate();
    }


    // ----------- Volley -----------
    private RequestQueue requestQueue;

    private void startVolley() {
        stopVolley();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }


    private void stopVolley() {
        if (requestQueue != null) {
            requestQueue.stop();
            requestQueue = null;
        }
    }


    /**
     * VolleyのRequestQueueを取得
     *
     * @param context コンテキスト
     * @return VolleyのRequestQueue
     */
    public static RequestQueue getVolleyRequestQueue(Context context) {
        BuzbizApplication me = (BuzbizApplication) context.getApplicationContext();

        if (me.requestQueue == null) {
            me.requestQueue = Volley.newRequestQueue(me);
        }

        return me.requestQueue;
    }
}