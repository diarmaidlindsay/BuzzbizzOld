package jp.pulseanddecibels.buzbiz;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz.data.LoginStatus;
import jp.pulseanddecibels.buzbiz.models.File;
import jp.pulseanddecibels.buzbiz.models.LoginChecker;
import jp.pulseanddecibels.buzbiz.models.LoginManager;
import jp.pulseanddecibels.buzbiz.models.LoginManagerInterface;
import jp.pulseanddecibels.buzbiz.models.Setting;
import jp.pulseanddecibels.buzbiz.util.Util;


/**
 * ログイン画面
 *
 */
public class LoginActivity
        extends Activity
        implements LoginManagerInterface {

    public static LoginActivity me;

    /** UI操作用ハンドラー */
    private static final Handler HANDLER = new Handler();





    // --------------- 各View ---------------
    @InjectView(jp.pulseanddecibels.buzbiz.R.id.btn_login)
    Button loginButton;

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.btn_end_application)
    Button endApplicationButton;

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.et_username)
    EditText usernameEditText;

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.et_password)
    EditText passwordEditText;

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.et_local_server)
    EditText locaServerEditText;

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.et_remote_server)
    EditText remoteServerEditText;

    @InjectView(R.id.tb_gsm)
    ToggleButton codec1Button;

    @InjectView(R.id.tb_ulaw)
    ToggleButton codec2Button;


    // --------------- ライフサイクル ---------------
    /**
     * onCreateメソッド
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);    // タイトルは非表示

        setContentView(jp.pulseanddecibels.buzbiz.R.layout.login_screen);            // ビューを設定
        Fabric.with(this, new Crashlytics());
        ButterKnife.inject(this);
        me = this;

        // 表示画面をログイン画面に変更
//        MainService.CurentScreenState = MainService.LOGIN;
        MainService.CurentScreenState = MainService.ScreenStates.LOGIN;



        codec1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //At least one codec should be selected
                    if (!codec2Button.isChecked()) {
                        codec1Button.setEnabled(false);
                    }
                }
                codec2Button.setEnabled(isChecked);
            }
        });

        codec2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //At least one codec should be selected
                    if (!codec1Button.isChecked()) {
                        codec2Button.setEnabled(false);
                    }
                } else {

                }
                codec1Button.setEnabled(isChecked);
            }
        });

        // 各UI部品を初期化
        initAllComponent();

        // 保存されたユーザ情報をUIに反映させる
        loadSavedData();

        //loadCodecSetting();
    }


    /**
     * onResumeメソッド
     */
    @Override
    protected void onResume() {
        super.onResume();

        // メインActivityが残っている場合は終了させる
        MainActivity.end();
        //System.gc();
    }





    /**
     * onDestroyメソッド
     */
    @Override
    public void onDestroy() {
        me = null;

        super.onDestroy();
    }





    // --------------- 内部メソッド ---------------
    /**
     * 各UI部品を初期化
     */
    void initAllComponent() {
        // 各トグルボタンを設定
//        tabPositionNormalToggleButton   .setOnCheckedChangeListener(tabSettingCheckedChanged);
//        tabPositionReversalToggleButton .setOnCheckedChangeListener(tabSettingCheckedChanged);
//        tabOperationTapToggleButton     .setOnCheckedChangeListener(tabSettingCheckedChanged);
//        tabOperationFrickToggleButton   .setOnCheckedChangeListener(tabSettingCheckedChanged);

        // 各エディットテキストをフォーカス可能に設定
        usernameEditText    .setFocusable(true);
        passwordEditText    .setFocusable(true);
        locaServerEditText  .setFocusable(true);
        remoteServerEditText.setFocusable(true);
        usernameEditText    .setFocusableInTouchMode(true);
        passwordEditText    .setFocusableInTouchMode(true);
        locaServerEditText  .setFocusableInTouchMode(true);
        remoteServerEditText.setFocusableInTouchMode(true);
    }


    private void saveCodecSetting() {
        Setting setting = new Setting();
        Context context = getApplicationContext();
        setting.saveGSM(context, codec1Button.isChecked());
        setting.saveULAW(context, codec2Button.isChecked());
    }

    private void loadCodecSetting() {
        Setting setting = new Setting();
        Context context = getApplicationContext();
        codec1Button.setChecked(setting.loadGSM(context));
        codec2Button.setChecked(setting.loadULAW(context));

        //At least one codec should be selected
        if (codec1Button.isChecked() && !codec2Button.isChecked()) {
            codec1Button.setEnabled(false);
        } else if (codec2Button.isChecked() && !codec1Button.isChecked()) {
            codec2Button.setEnabled(false);
        }
    }


    /**
     * 保存されたユーザ情報をUIに反映させる
     */
    private void loadSavedData() {
        Context cntxt = getApplicationContext();

        // 値を保存されている場合は、各テキストボックスに取得した値をセット
        Setting Setting = new Setting();
        final String userName     = Setting.loadUserName(cntxt);
        final String password     = Setting.loadPassword(cntxt);
        final String localServer  = Setting.loadLocalServerDomain(cntxt);
        final String remoteServer = Setting.loadRemoteServerDomain(cntxt);
        usernameEditText    .setText(userName);
        passwordEditText    .setText(password);
        locaServerEditText  .setText(localServer);
        remoteServerEditText.setText(remoteServer);


        // 値を取得できた場合は、各スピナーに取得した値をセット
        String tmp = File.getValue(cntxt, File.TAB_POSITION);
//        if (tmp != null && tmp.equals("Reversal")) {
//            tabPositionNormalToggleButton.setChecked(false);
//            tabPositionReversalToggleButton.setChecked(true);
//        } else {
//            tabPositionNormalToggleButton.setChecked(true);
//            tabPositionReversalToggleButton.setChecked(false);
//        }
//
//        tmp = File.getValue(cntxt, File.TAB_OPERATION);
//        if (tmp != null && tmp.equals("Frick")) {
//            tabOperationTapToggleButton.setChecked(false);
//            tabOperationFrickToggleButton.setChecked(true);
//        } else {
//            tabOperationTapToggleButton.setChecked(true);
//            tabOperationFrickToggleButton.setChecked(false);
//        }
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
     * ログイン処理
     */
    private synchronized void login() {
        final Context context = getApplicationContext();
        final Setting setting = new Setting();

        // 現在の入力情報を保存
        final String userName = Util.getText(usernameEditText);
        final String password = Util.getText(passwordEditText);
        setting.saveAccount(context, userName, password);
        Log.d("LoginActivity", "login1");
        final String localServer = Util.getText(locaServerEditText);
        setting.saveLocalServerInfo(getApplicationContext(), localServer);
        Log.d("LoginActivity", "login2");
        final String remoteServer = Util.getText(remoteServerEditText);
        setting.saveRemoteServerInfo(getApplicationContext(), remoteServer);
        //saveCodecSetting();

        // ログインマネージャーを生成
        LoginManager loginManager = new LoginManager();
        Log.d("LoginActivity", "login3");
        // ログインに必要な情報が無い場合は、メッセージを表示し終了

        Log.d("LoginActivity", "login4");
        // ログイン開始
        loginManager.login(this, HANDLER);
    }





    /**
     * ログアウト
     */
    private synchronized void logoff() {
//		Log.e(Util.LOG_TAG,"  ログイン画面.logout  ");
        Log.d("LoginActivity", "logoff1");

        // ログアウト状態を保存
        new LoginStatus(LoginStatus.OFFLINE).save(getApplicationContext());

        // SIPプロキシサーバから登録解除
        MainService.LIB_OP.logout();
        Log.d("LoginActivity", "logoff2");
        Intent sipService = new Intent(getApplicationContext(), MainService.class);
        sipService.putExtra("message", "re-resident");
        startService(sipService);
        Log.d("LoginActivity", "logoff3");
        // サーバ死活監視終了
        LoginChecker.stop();

        // ※ Vaxではログアウトできないことも有るため強制的にログアウト
        LoginManager loginManager = new LoginManager();
        loginManager.logout(this);
        Log.d("LoginActivity", "logoff4");
        // 少し待機
        Util.waitMiriSec(700);
    }





    /**
     * ボタンをロック
     */
    private void lockButton() {
        loginButton         .setEnabled(false);
        endApplicationButton.setEnabled(false);
    }





    /**
     * ボタンをロック解除
     */
    private void unlockButton() {
        loginButton         .setEnabled(true);
        endApplicationButton.setEnabled(true);
    }





    /**
     * ログインボタン押下時の処理
     */
    public void clickLoginButton(View view) {
        lockButton();

        logoff();
        login();

        unlockButton();
    }





    /**
     * アプリ終了ボタン押下時の処理
     */
    public void clickEndApplicationButton(View view) {
        lockButton();

        logoff();

        // サービスを終了させる
        Intent sipService = new Intent(getApplicationContext(), MainService.class);
        stopService(sipService);

        // 本ログイン画面を終了
        finish();
        android.os.Process.killProcess(Process.myPid());

        unlockButton();
    }





    /**
     * VPN設定ボタン押下時の処理
     */
    public void clickVpnSettingButton(View view) {
        try {
            startActivity(new Intent("android.net.vpn.SETTINGS"));
        } catch (Exception e) {
            showMessage("移動に失敗しました");
        }
    }





    /**
     * wifi設定ボタン押下時の処理
     */
    public void clickWifiSettingButton(View view) {
        try {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } catch (Exception e) {
            showMessage("移動に失敗しました");
        }
    }



    /**
     * 『戻る』ボタンを押下時の処理
     */
    @Override
    public void onBackPressed() {
        // メイン画面のアクティビティ起動
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        // ログイン画面を終了
        finish();
    }





    // -------- API --------
    @Override
    public void showMessage(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }





    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}