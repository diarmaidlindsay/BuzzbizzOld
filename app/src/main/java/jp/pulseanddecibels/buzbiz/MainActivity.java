package jp.pulseanddecibels.buzbiz;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz.models.LoginManager;
import jp.pulseanddecibels.buzbiz.models.LoginManagerInterface;
import jp.pulseanddecibels.buzbiz.models.Setting;
import jp.pulseanddecibels.buzbiz.models.ContentProvideHelper;
import jp.pulseanddecibels.buzbiz.util.Logger;





/**
 * BuzbizのメインActivity
 *
 */
public class MainActivity extends Activity {

    /** 自アクティビティの識別ID */
     public static final int MY_ID = 61314010;

    /** サービスからのUI操作用 */
    public static MainActivity me = null;

    /** UI操作用ハンドラー */
    private static final Handler MAIN_HANDLER = new Handler();

    private final String LOG_TAG = this.getClass().getSimpleName();

    /** レイアウトルート */
    @InjectView(jp.pulseanddecibels.buzbiz.R.id.parent_layout)
    FrameLayout parentLayout;

    /** 第一画面を取得 */
    @InjectView(jp.pulseanddecibels.buzbiz.R.id.first_screen)
    MyFrameLayout firstScreen;

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.vs_waku)
    ViewSwitcher vsWaku;





    // -------------- ライフサイクル --------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Log.d(LOG_TAG, "onCreate 1");
        Fabric.with(this, new Crashlytics());
        Log.d(LOG_TAG, "onCreate 2");
        me = this;

        setEnableAdjustVolume();
        Log.d(LOG_TAG, "onCreate 3");
        startMainService();
        Log.d(LOG_TAG, "onCreate 4");

        ////////////////////////////////////////////////////////////////
        /////////////////////////// UIの設定 ///////////////////////////
        ////////////////////////////////////////////////////////////////

        // タイトルは非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        // タブの設定を取得
        MyFrameLayout.getTabSetting(getApplicationContext());
        Log.d(LOG_TAG, "onCreate 5");
        // ビューを設定
        setContentView(jp.pulseanddecibels.buzbiz.R.layout.main);
        ButterKnife.inject(this);
        Log.d(LOG_TAG, "onCreate 6");
        // タブの初期化
        MyFrameLayout.initTabSetting(this);


        // 通話状態であれば、枠を変更する
//        if (MainService.currentKeypadScreen != MainService.NOMAL) {
        Log.d(LOG_TAG, "onCreate 7");
        if (MainService.currentKeypadScreen != MainService.KeyPadStates.NOMAL) {
            setNormalWaku();
        }
        Log.d(LOG_TAG, "onCreate 8");
        login();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "onStop");
        //this was causing a crash when turning the screen off using wakelock!!
        System.gc();

        super.onStop();
    }





    /**
     * 電話帳から戻った際の処理
     */
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);
        Log.d(LOG_TAG, "onActivityResult");

        if (reqCode != MY_ID || resultCode != Activity.RESULT_OK) {
            return;
        }

        // 指定された電話帳アイテムの電話番号を取得
        Uri targetUri = intent.getData();
        ContentProvideHelper cph = new ContentProvideHelper();
        final String[] telNums = cph.getTelNumberFromUri(this, targetUri);

        // 選択者が電話番号を持っていない場合
        if (telNums.length <= 0) {
            Toast.makeText(getApplicationContext(), "指定されたアイテムには電話番号が登録されておりません", Toast.LENGTH_LONG).show();
            return;
        }

        // 電話番号が１つの場合
        if (telNums.length == 1) {
            firstScreen.keypadScreen.setInputNumber(telNums[0]);
            return;
        }

        // 複数の電話番号が設定されている場合は選択してもらう
        DialogInterface.OnClickListener pickupItem = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                firstScreen.keypadScreen.setInputNumber(telNums[item]);
            }
        };
        new AlertDialog.Builder(this)
                .setTitle("発信する電話番号を選択してください")
                .setItems(telNums, pickupItem)
                .show();
    }





    /**
     * メニューを非表示に設定
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }





    // -------------- イベントハンドラ --------------
    /**
     * 『戻る』ボタンを無効に設定
     */
    @Override
    public void onBackPressed() { }





    // -------------- 内部処理 --------------
    /**
     * 音量調節を可能にする
     */
    private void setEnableAdjustVolume() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }





    /**
     * メインサービスを開始する
     */
    private void startMainService() {
        Intent sipServiceIntent = new Intent(getApplicationContext(), MainService.class);
        sipServiceIntent.putExtra("message", "resident");
        startService(sipServiceIntent);
    }





    /**
     * ログイン処理
     */
    private void login() {
        if (MainService.LIB_OP.isLogined()) {
            return;
        }

        // 設定が存在しない場合はなにもしない
        final Setting setting = new Setting();
        boolean settingOk = setting.isExistSetting(getApplicationContext());
        if (!settingOk) {
            return;
        }

        // ログイン開始
        LoginManager loginManager = new LoginManager();
        LoginManagerInterface lmi = new LoginManagerInterface() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public Context getContext() {
                return getApplicationContext();
            }
        };
        loginManager.login(lmi, MAIN_HANDLER);
    }





    // -------------- API --------------
    /**
     * 通常用の枠に設定する
     */
    public void setNormalWaku() {
        View nextView = vsWaku.getNextView();
        if (nextView.getId() == jp.pulseanddecibels.buzbiz.R.id.waku1) {
            vsWaku.showNext();
        }
    }





    /**
     * 本画面を終了させる
     */
    public static void end() {
        if (me != null && me.isFinishing() == false) {
            me.finish();
            me = null;
        }
    }





    /**
     * 本アプリのUI操作用ハンドラーを取得
     *
     * @return ハンドラー
     */
    public static Handler getHandler() {
        return MAIN_HANDLER;
    }





    public static void displayMessage(final String msg) {
        displayMessage(me, msg);
    }

    /**
     * メインスレッド以外からメッセージを表示させる
     *
     * @param msg     表示させるメッセージ
     */
    public static void displayMessage(final Context context, final String msg) {
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Logger.e(e);
                }
            }
        });
    }
}