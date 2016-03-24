package jp.pulseanddecibels.buzbiz.models;



import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import jp.pulseanddecibels.buzbiz.MainService;
import jp.pulseanddecibels.buzbiz.data.AsteriskAccount;
import jp.pulseanddecibels.buzbiz.util.Logger;
import jp.pulseanddecibels.buzbiz.util.Util;





/**
 */
public class LoginManager {



    // ----------- API -----------
    /**
     * 入力不足項目に関するメッセージを取得する
     *
     * @return 入力不足項目に関するメッセージ
     */
    public String getInputNgMessage(Context context,
                                    String userName,
                                    String password,
                                    String localServer,
                                    String ssid,
                                    String remoteServer) {
        // 各入力が空であるか確認
        boolean notEmptyUserName     = !isEmpty(userName);
        boolean notEmptyPassword     = !isEmpty(password);
        boolean notEmptyLocalServer  = !isEmpty(localServer);
        boolean notEmptySsid         = !isEmpty(ssid);
        boolean notEmptyRemoteServer = !isEmpty(remoteServer);

        // SSIDが現在接続している無線LANのSSIDであるか確認する
        boolean ssidOk;
        ssidOk = notEmptySsid && ssid.equals(new WifiController().getConnectionSsid(context));

        // 各設定の入力がされているか
        boolean accountOk       = notEmptyUserName && notEmptyPassword;
        boolean localSettingOk  = notEmptyLocalServer && notEmptySsid;
        boolean remoteSettingOk = notEmptyRemoteServer;

        // ローカル or リモートのどちらかの設定があるか (ローカルはSSIDが正しいかも確認)
        boolean serverOk = (localSettingOk && ssidOk) || remoteSettingOk;


        String message = Util.STRING_EMPTY;
        if (accountOk && serverOk) {
            return "設定の保存に失敗しました";
        }
        if (!accountOk) {
            if (!notEmptyUserName) {
                message += "『ユーザ名』";
            }
            if (!notEmptyPassword) {
                message += "『パスワード』";
            }
            message += "は必ず入力してください\n";
        }
        if (!serverOk) {
            if (localSettingOk && !ssidOk && !remoteSettingOk) {
                message += "『無線LAN SSID』が現在の情報と一致しません";
                return message;
            }
            if (!localSettingOk && !remoteSettingOk) {
                if (!notEmptyLocalServer) {
                    message += "『ローカルサーバ』";
                }
                if (!notEmptySsid) {
                    message += "『無線LAN SSID』";
                }
                message += "か『リモートサーバ』のどちらかは入力してください";
                return message;
            }
        }

        return message;
    }





    /**
     * ログイン
     *
     * @param lmi     連携するActivityのインターフェース
     * @param handler UIスレッドのHandler
     */
    public void login(final LoginManagerInterface lmi,
                      final Handler handler) {
        getAsteriskAccount(lmi, handler);
    }





    /**
     * ログアウト
     *
     * @param lmi 連携するActivityのインターフェース
     */
    public void logout(final LoginManagerInterface lmi) {
        VolleyOperator.forceLogout(lmi.getContext());
    }





    // ----------- 内部処理 -----------
    private boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }





    /**
     * サーバよりAsteriskのアカウントを取得する
     */
    private void getAsteriskAccount(final LoginManagerInterface lmi,
                                    final Handler handler) {
        // 成功時
        Response.Listener ok = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                // 取得情報より、Asteriskアカウントを解析する
                AsteriskAccount asteriskAccount;
                try {
                    String json = response.toString();
                    asteriskAccount = new JsonParser().parseAsteriskAccount(json);
                } catch (Exception ex) {
                    lmi.showMessage("ログインに失敗しました\n" + ex.getMessage());
                    return;
                }

                // 取得に成功した場合は、保存
                asteriskAccount.save(lmi.getContext());

                // ライセンスキーを取得
                getLicenseKey(lmi, handler);
            }
        };

        // 失敗時
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lmi.showMessage("サーバからアカウントを取得できませんでした\n" + error.getMessage());
            }
        };

        // 通信開始
        VolleyOperator.getAsteriskAccount(lmi.getContext(), ok, err);
    }





    /**
     * サーバよりライセンスキーを取得する
     */
    private void getLicenseKey(final LoginManagerInterface lmi,
                               final Handler handler) {
        // 成功時
        Response.Listener ok = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                // 取得したライセンスキーを登録
                try {
                    String json = response.toString();
                    String licenceKey = new JsonParser().parseLicenceKey(json).trim();
                    MainService.LIB_OP.setKey(licenceKey);
                    new Setting().saveLicenceKey(lmi.getContext(), licenceKey);
                } catch (Exception ex) {
                    lmi.showMessage("ライセンスキー登録に失敗しました。\n" + ex.getMessage());
                    return;
                }

                // 少し遅らせAsteriskへログイン
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        loginAsterisk(lmi, handler);
                    }
                };
                handler.postDelayed(run, 1500);
            }
        };

        // 失敗時
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lmi.showMessage("サーバからライセンスキーを取得できませんでした\n" + error.getMessage());
            }
        };

        // 通信開始
        VolleyOperator.getLicenceKey(lmi.getContext(), ok, err);
    }





    /**
     * Asteriskへログインする
     */
    private void loginAsterisk(final LoginManagerInterface lmi,
                               final Handler handler) {
        Setting setting = new Setting();
        AsteriskAccount asteriskAccount = setting.loadAsteriskAccount(lmi.getContext());
        String user   = asteriskAccount.sipId;
        String pass   = asteriskAccount.sipPass;
        String server = setting.loadCurrentUseServerDomain(lmi.getContext());

        // ログイン
        boolean success = MainService.LIB_OP.login(user, pass, server);
        if (!success) {
            lmi.showMessage("ログインに失敗しました");
            return;
        }

        registerGcm(lmi, handler);
    }





    /**
     * PUSH通知用登録処理
     */
    private void registerGcm(final LoginManagerInterface lmi,
                             final Handler handler) {
        final String msg = "Push通知の登録に失敗しました。\nBUZBIZ終了後も着信情報を受け取るには、ログインし直してください。";

        // 成功時
        final Response.Listener ok = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                // GCMへの登録結果を解析
                boolean registerGcmOk;

                Logger.e(response);

                try {
                    String json = response.toString();
                    registerGcmOk = new JsonParser().parseRegisteGcmOk(json);
                } catch (Exception ex) {
                    registerGcmOk = false;
                }

                // 失敗した場合はメッセージを表示
                if (!registerGcmOk) {
                    //TODO 毎回ここを通るので要検証20150630
                    lmi.showMessage(msg);
                }
            }
        };

        // 失敗時
        final Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lmi.showMessage(msg);
            }
        };

        // 通信開始
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VolleyOperator.registerGcm(lmi.getContext(), ok, err);
                } catch (Exception ex) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            lmi.showMessage(msg);
                        }
                    });
                }
            }
        }).start();

    }
}