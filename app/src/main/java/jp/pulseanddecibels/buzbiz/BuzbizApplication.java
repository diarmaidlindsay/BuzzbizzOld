package jp.pulseanddecibels.buzbiz;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import java.util.concurrent.ArrayBlockingQueue;

import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz.util.ActivityEvent;
import jp.pulseanddecibels.buzbiz.util.ActivityEventKind;
import rx.Observable;
import rx.Subscriber;

/**
 * BUZBIZ用アプリケーションクラス
 *
 */
public class BuzbizApplication extends Application {
    /**
     * For UI Testing
     *
     * https://github.com/FutureProcessing/AndroidEspressoIdlingResourcePlayground
     */
    private static Observable<ActivityEvent> _activityEventStream;

    public static Observable<ActivityEvent> activityEventStream() {
        return _activityEventStream;
    }

    // ----------- ライフサイクル -----------
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        startVolley();
        //For UI Testing
        ActivityEventProducer activityEventProducer = new ActivityEventProducer();
        _activityEventStream = Observable.create(activityEventProducer);
        registerActivityLifecycleCallbacks(activityEventProducer);
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

    /**
     * Broadcast the lifecycle of all Buzbiz Activities so we can listen for those events in UI Testing
     */
    private static class ActivityEventProducer implements ActivityLifecycleCallbacks, Observable.OnSubscribe<ActivityEvent> {

        private ArrayBlockingQueue<ActivityEvent> activityEvents = new ArrayBlockingQueue<>(256, false);
        private boolean anyOneSubscribed;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if(!anyOneSubscribed) {
                return;
            }
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.CREATED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.DESTROYED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void call(Subscriber<? super ActivityEvent> subscriber) {
            anyOneSubscribed = true;
            try {
                while(!subscriber.isUnsubscribed()) {
                    ActivityEvent activityEvent = activityEvents.take();
                    subscriber.onNext(activityEvent);
                }
            } catch(Exception e) {
                subscriber.onError(e);
            } finally {
                anyOneSubscribed = false;
                activityEvents.clear();
            }
        }
    }
}