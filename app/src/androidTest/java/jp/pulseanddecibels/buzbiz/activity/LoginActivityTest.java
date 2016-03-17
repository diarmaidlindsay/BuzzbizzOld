package jp.pulseanddecibels.buzbiz.activity;

import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.pulseanddecibels.buzbiz.BuzbizApplication;
import jp.pulseanddecibels.buzbiz.LoginActivity;
import jp.pulseanddecibels.buzbiz.MainActivity;
import jp.pulseanddecibels.buzbiz.R;
import jp.pulseanddecibels.buzbiz.util.ActivityEvent;
import jp.pulseanddecibels.buzbiz.util.ActivityEventKind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Diarmaid Lindsay on 2016/03/10.
 * Copyright Pulse and Decibels 2016
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> mActivityRule = new IntentsTestRule<>(LoginActivity.class);
    private MainActivityCreatedIdlingResource mainActivityCreatedIdlingResource;

    @Before
    public void setUp() throws Exception {
        mainActivityCreatedIdlingResource = new MainActivityCreatedIdlingResource();

        BuzbizApplication.activityEventStream().
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainActivityCreatedIdlingResource);
    }

    @Test
    public void testLoginScreen() {
        //verify login screen text shown
        onView(withText("ログイン設定")).check(matches(isDisplayed()));
        //enter user
        onView(withId(R.id.et_username)).perform(clearText(), typeText("demouser5"), closeSoftKeyboard());
        //enter password
        onView(withId(R.id.et_password)).perform(clearText(), typeText("demouser5"), closeSoftKeyboard());
        //enter local server
        onView(withId(R.id.et_local_server)).perform(clearText(), typeText("192.168.1.230"), closeSoftKeyboard());
        //click on ssid field
        onView(withId(R.id.et_ssid)).perform(clearText(), click());
        //click はい on dialog to auto-populate ssid
        onView(withText("はい")).perform(click());
        //verify that the ssid field was populated
        onView(withId(R.id.et_ssid)).check(matches(not(withText("")))).perform(closeSoftKeyboard());
        //enter remote server
        onView(withId(R.id.et_remote_server)).perform(clearText(), typeText("system-onpre.pulseanddecibels.jp"), closeSoftKeyboard());
        //click login button
        onView(withId(R.id.btn_login)).perform(click());
        // Wait until SecondActivity is created
        registerIdlingResources(mainActivityCreatedIdlingResource);

        //wait until Buzbiz "cover" picture is displayed
        onView(ViewMatchers.withId(R.id.cover))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        unregisterIdlingResources(mainActivityCreatedIdlingResource);
        mainActivityCreatedIdlingResource.unsubscribe();
    }

    private static class MainActivityCreatedIdlingResource extends Subscriber<ActivityEvent> implements IdlingResource {
        private volatile ResourceCallback resourceCallback;
        private volatile boolean mainActivityCreated;

        @Override
        public String getName() {
            return "MainActivity Created";
        }

        @Override
        public boolean isIdleNow() {
            return mainActivityCreated;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(ActivityEvent activityEvent) {
            if(mainActivityCreated(activityEvent)) {
                mainActivityCreated = true;
                resourceCallback.onTransitionToIdle();
            }
        }

        private boolean mainActivityCreated(ActivityEvent activityEvent) {
            return activityEvent.getActivityClass().equals(MainActivity.class) && activityEvent.getEventKind() == ActivityEventKind.CREATED;
        }
    }
}
