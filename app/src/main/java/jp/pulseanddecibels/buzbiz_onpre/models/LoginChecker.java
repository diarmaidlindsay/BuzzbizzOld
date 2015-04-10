package jp.pulseanddecibels.buzbiz_onpre.models;





import java.util.Timer;
import java.util.TimerTask;

import jp.pulseanddecibels.buzbiz_onpre.MainService;
import jp.pulseanddecibels.buzbiz_onpre.R;
import jp.pulseanddecibels.buzbiz_onpre.data.LoginStatus;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;





/**
 * ログイン確認用クラス
 *
 * @author 普天間
 */
public class LoginChecker {

    /** チェックの定期実行用タイマー */
    private static Timer timer = null;

    /** 失敗回数のカウント用 */
    private static int ngCount = 0;





    /**
     * サーバ死活監視タイマースタート
     */
    public synchronized static void start(final Context context) {
        // ２重起動させないために、１度ストップさせる
        stop();

        // 初期化
        timer = new Timer();
        ngCount = 0;

        // タイマーのスケジュールを設定
        timer.schedule(new TimerTask() {
            // 失敗時
            final Response.ErrorListener err = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ngCount++;

                    // pingが2回失敗した場合は、ログインステータスをNWダウンとする
                    if (ngCount == 2) {
                        saveCheckNg(context);
                    }
                }
            };

            // 成功時
            final Response.Listener ok = new Response.Listener() {
                @Override
                public void onResponse(final Object response) {
                    boolean loginOk = (response != null) && (response.toString().contains("1") );

                    if (loginOk) {
                        if (ngCount != 0) {
                            saveCheckOk(context);
                        }
                    } else {
                        ngCount++;

                        try {
                            MainService.LIB_OP.reLogin();
                        } catch (Exception e) {
                            saveCheckNg(context);
                        }
                    }
                }
            };

            @Override
            public void run() {
                VolleyOperator.checkLoginStatus(context, ok, err);
            }
        }, 30000, 30000);
    }





    /**
     * サーバ死活監視タイマーストップ
     */
    public synchronized static void stop() {
        if (timer == null) {
            return;
        }

        timer.cancel();
        timer.purge();
        timer = null;
    }





    /**
     * チェックOKの状態を保存
     *
     * @param context コンテキスト
     */
    private static void saveCheckOk(Context context) {
        // ステータスバーのアイコンを変更
        MainService.me.stopStayService();
        MainService.me.startStayService(R.drawable.login_status_icon);

        // ログイン状態を保存
        new LoginStatus(LoginStatus.ONLINE).save(context);

        ngCount = 0;
    }





    /**
     * チェックNGの状態を保存
     *
     * @param context コンテキスト
     */
    private static void saveCheckNg(Context context) {
        // ステータスバーのアイコンを変更
        MainService.me.stopStayService();
        MainService.me.startStayService(R.drawable.logout_status_icon);

        // ログイン状態を保存
        new LoginStatus(LoginStatus.PING_NG).save(context);
    }
}