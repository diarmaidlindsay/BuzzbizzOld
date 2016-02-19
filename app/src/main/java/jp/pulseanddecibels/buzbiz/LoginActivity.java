package jp.pulseanddecibels.buzbiz;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import jp.pulseanddecibels.buzbiz.models.WifiController;
import jp.pulseanddecibels.buzbiz.util.Util;


/**
 * ログイン画面
 *
 */
public class LoginActivity
        extends Activity
        implements LoginManagerInterface {

    static LoginActivity me;

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

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.et_ssid)
    EditText ssidEditText;

    @InjectView(jp.pulseanddecibels.buzbiz.R.id.et_remote_server)
    EditText remoteServerEditText;

//    @InjectView(jp.pulseanddecibels.buzbiz.R.id.tbtn_tab_position_normal)
//    ToggleButton tabPositionNormalToggleButton;
//
//    @InjectView(jp.pulseanddecibels.buzbiz.R.id.tbtn_tab_position_reversal)
//    ToggleButton tabPositionReversalToggleButton;

//    @InjectView(jp.pulseanddecibels.buzbiz.R.id.tbtn_tab_operation_tap)
//    ToggleButton tabOperationTapToggleButton;
//
//    @InjectView(jp.pulseanddecibels.buzbiz.R.id.tbtn_tab_operation_frick)
//    ToggleButton tabOperationFrickToggleButton;





    // --------------- ライフサイクル ---------------
    /**
     * onCreateメソッド
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        me = this;

        // 表示画面をログイン画面に変更
//        MainService.CurentScreenState = MainService.LOGIN;
        MainService.CurentScreenState = MainService.ScreenStates.LOGIN;

        requestWindowFeature(Window.FEATURE_NO_TITLE);    // タイトルは非表示
        setContentView(jp.pulseanddecibels.buzbiz.R.layout.login_screen);            // ビューを設定
        Button settingsButton = (Button) findViewById(R.id.button_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSSSS(v);
            }
        });
        ButterKnife.inject(this);

        // 各UI部品を初期化
        initAllComponent();

        // 保存されたユーザ情報をUIに反映させる
        loadSavedData();
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
        ssidEditText        .setFocusable(true);
        remoteServerEditText.setFocusable(true);
        usernameEditText    .setFocusableInTouchMode(true);
        passwordEditText    .setFocusableInTouchMode(true);
        locaServerEditText  .setFocusableInTouchMode(true);
        ssidEditText        .setFocusableInTouchMode(true);
        remoteServerEditText.setFocusableInTouchMode(true);

        // エディットテキストフォーカス次の処理を追加
        ssidEditText.setOnFocusChangeListener(ssidEditTextFocusChangeListener);
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
        final String ssid         = Setting.loadSsid(cntxt);
        final String remoteServer = Setting.loadRemoteServerDomain(cntxt);
        usernameEditText    .setText(userName);
        passwordEditText    .setText(password);
        locaServerEditText  .setText(localServer);
        ssidEditText        .setText(ssid);
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
        final String ssid        = Util.getText(ssidEditText);
        setting.saveLocalServerInfo(getApplicationContext(), localServer, ssid);
        Log.d("LoginActivity", "login2");
        final String remoteServer = Util.getText(remoteServerEditText);
        setting.saveRemoteServerInfo(getApplicationContext(), remoteServer);

        // ログインマネージャーを生成
        LoginManager loginManager = new LoginManager();
        Log.d("LoginActivity", "login3");
        // ログインに必要な情報が無い場合は、メッセージを表示し終了
        boolean dataOk = setting.isExistSetting(context);
        if(!dataOk){
            String inputNgMessage = loginManager.getInputNgMessage(context, userName, password, localServer, ssid, remoteServer);
            showMessage(inputNgMessage);
            return;
        }
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





    /*
     * TODO test
     */
    public void clickSSSS(View view) {
        startActivity(new Intent(this, CodecSettingActivity.class));
    }





    /**
     * タブ設定用トグルボタン選択時の操作
     */
//    private final OnCheckedChangeListener tabSettingCheckedChanged = new OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
////			Log.e(Util.LOG_TAG,"  ログイン画面.tabSettingCheckedChanged  ");
//
//            Context cntxt = getApplicationContext();
//
//
//            if (((ToggleButton) compoundbutton).equals(tabPositionNormalToggleButton)) {
//                tabPositionReversalToggleButton.setChecked(!flag);
//
//                if (flag) {
//                    File.saveData(cntxt, File.TAB_POSITION, "Normal");
//                } else {
//                    File.saveData(cntxt, File.TAB_POSITION, "Reversal");
//                }
//
//            } else if (((ToggleButton) compoundbutton).equals(tabPositionReversalToggleButton)) {
//                tabPositionNormalToggleButton.setChecked(!flag);
//
//                if (flag) {
//                    File.saveData(cntxt, File.TAB_POSITION, "Reversal");
//                } else {
//                    File.saveData(cntxt, File.TAB_POSITION, "Normal");
//                }
//
//
//            } else if (((ToggleButton) compoundbutton).equals(tabOperationTapToggleButton)) {
//                tabOperationFrickToggleButton.setChecked(!flag);
//
//                if (flag) {
//                    File.saveData(cntxt, File.TAB_OPERATION, "Tap");
//                } else {
//                    File.saveData(cntxt, File.TAB_OPERATION, "Frick");
//                }
//
//            } else if (((ToggleButton) compoundbutton).equals(tabOperationFrickToggleButton)) {
//                tabOperationTapToggleButton.setChecked(!flag);
//
//                if (flag) {
//                    File.saveData(cntxt, File.TAB_OPERATION, "Frick");
//                } else {
//                    File.saveData(cntxt, File.TAB_OPERATION, "Tap");
//                }
//            }
//        }
//    };





    /**
     * SSID入力欄のフォーカス変更時の処理
     */
    private final View.OnFocusChangeListener ssidEditTextFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(final View view, boolean hasFocus) {
            // 処理はフォーカスが当てられた場合のみ実行
            if (!hasFocus) {
                return;
            }

            // 既に入力値がある場合は何もしない
            final String input = Util.getText(ssidEditText);
            if (!TextUtils.isEmpty(input)) {
                return;
            }

            // 現在、Wi-Fiが有効でない場合は何もしない
            WifiController wc = new WifiController();
            final String ssid = wc.getConnectionSsid(getApplicationContext());
            if (TextUtils.isEmpty(ssid)) {
                return;
            }

            // 現在のssidを設定するかを確認
            DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((EditText) view).setText(ssid);
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("SSID")
                    .setMessage("現在、接続している無線LANのSSIDを設定しますか?")
                    .setPositiveButton("はい", yes)
                    .setNeutralButton("いいえ", null)
                    .show();
        }
    };





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