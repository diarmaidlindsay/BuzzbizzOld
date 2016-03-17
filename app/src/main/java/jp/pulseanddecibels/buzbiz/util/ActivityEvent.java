package jp.pulseanddecibels.buzbiz.util;

import android.app.Activity;

/**
 * For UI Testing with Espresso Framework.
 * Report on Activity Lifecycle Events, especially useful when moving between activities.
 */
public class ActivityEvent {

    private Class<? extends Activity> activityClass;
    private ActivityEventKind eventKind;

    @Override
    public String toString() {
        return "ActivityEvent{" +
                "activityClass=" + activityClass +
                ", eventKind=" + eventKind +
                '}';
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class<? extends Activity> activityClass) {
        this.activityClass = activityClass;
    }

    public ActivityEventKind getEventKind() {
        return eventKind;
    }

    public void setEventKind(ActivityEventKind eventKind) {
        this.eventKind = eventKind;
    }
}
