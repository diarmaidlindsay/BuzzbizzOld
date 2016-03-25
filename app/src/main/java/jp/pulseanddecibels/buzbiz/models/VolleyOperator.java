package jp.pulseanddecibels.buzbiz.models;


import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.pulseanddecibels.buzbiz.BuzbizApplication;
import jp.pulseanddecibels.buzbiz.data.AsteriskAccount;
import jp.pulseanddecibels.buzbiz.data.TelNumber;
import jp.pulseanddecibels.buzbiz.util.Logger;


/**
 * DB接続クラス
 *
 */
public class VolleyOperator {

    public static final String LOG_TAG = VolleyOperator.class.getSimpleName();

    /**
     * Asteriskアカウントを取得する
     *
     * @param context コンテキスト
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     */
    public static synchronized void getAsteriskAccount(final Context context,
                                                       Response.Listener ok,
                                                       Response.ErrorListener err) {
        Log.d(LOG_TAG, "getAsteriskAccount");
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/login.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/login.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                postParams.put("user_id", setting.loadUserName(context));
                postParams.put("user_pass", setting.loadPassword(context));
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * ライセンスキーを取得する
     *
     * @param context コンテキスト
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     */
    public static synchronized void getLicenceKey(final Context context,
                                                  Response.Listener ok,
                                                  Response.ErrorListener err) {
        Log.d(LOG_TAG, "getLicenceKey");
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/get_android_license.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/get_android_license.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                postParams.put("user_id", setting.loadUserName(context));
                postParams.put("user_pass", setting.loadPassword(context));
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * GCMへのレジスト処理
     *
     * @param context コンテキスト
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     */
    public static synchronized void registerGcm(final Context context,
                                                Response.Listener ok,
                                                Response.ErrorListener err) throws IOException {
        Log.d(LOG_TAG, "registerGcm");
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        final String token = gcm.register("337574662965");

        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/add_endpoint.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/add_endpoint.php", sipServer);
        }

        // リクエストを作成
        // ---------------------------------------------
        // ★platformの値
        // 0 ipad
        // 1 iphone
        // 2 android
        // ---------------------------------------------
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                postParams.put("user_id",   setting.loadUserName(context));
                postParams.put("user_pass", setting.loadPassword(context));
                postParams.put("platform", "2");
                Logger.e(token);
                postParams.put("token", token);
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * 強制ログアウト
     *
     * @param context コンテキスト
     */
    public static synchronized void forceLogout(final Context context) {
        Log.d(LOG_TAG, "forceLogout");
        // 成功時
        Response.Listener ok = new Response.Listener() {
            @Override
            public void onResponse(Object response) { }
        };

        // 失敗時
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        };

        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/logout.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/logout.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                AsteriskAccount asteriskAccount = setting.loadAsteriskAccount(context);
                postParams.put("user_id", setting.loadUserName(context));
                postParams.put("user_pass", setting.loadPassword(context));
                postParams.put("sip_id", asteriskAccount.sipId);
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * 外線帳を取得する
     *
     * @param context コンテキスト
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     */
    public static synchronized void downloadExternalTable(final Context context,
                                                          Response.Listener ok,
                                                          Response.ErrorListener err) {
        Log.d(LOG_TAG, "downloadExternalTable");
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/outline.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/outline.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                postParams.put("user_id", setting.loadUserName(context));
                postParams.put("user_pass", setting.loadPassword(context));
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * 内線帳を取得する
     *
     * @param context コンテキスト
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     */
    public static synchronized void downloadInternalTable(final Context context,
                                                          Response.Listener ok,
                                                          Response.ErrorListener err) {
        Log.d(LOG_TAG, "downloadInternalTable");
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/inline.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/inline.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                postParams.put("user_id", setting.loadUserName(context));
                postParams.put("user_pass", setting.loadPassword(context));
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * 履歴を取得する
     *
     * @param context コンテキスト
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     */
    public static synchronized void downloadHistoryList(final Context context,
                                                        Response.Listener ok,
                                                        Response.ErrorListener err) {
        Log.d(LOG_TAG, "downloadHistoryList");
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/history.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/history.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                AsteriskAccount asteriskAccount = setting.loadAsteriskAccount(context);
                postParams.put("user_id",       setting.loadUserName(context));
                postParams.put("user_pass",     setting.loadPassword(context));
                postParams.put("sip_id",        asteriskAccount.sipId);
                postParams.put("sip_group_id",  asteriskAccount.sipGroupId);
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * 保留リストを取得する
     *
     * @param context コンテキスト
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     */
    public static synchronized void downloadHoldList(final Context context,
                                                     Response.Listener ok,
                                                     Response.ErrorListener err) {
        Log.d(LOG_TAG, "downloadHoldList");
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/hold.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/hold.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                postParams.put("user_id", setting.loadUserName(context));
                postParams.put("user_pass", setting.loadPassword(context));
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * 指定の電話番号から名前を取得する
     */
    public static synchronized void resolverName(final Context context,
                                                 final TelNumber telNum,
                                                 Response.Listener ok,
                                                 Response.ErrorListener err) {
        Log.d(LOG_TAG, "resolverName");
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/get_incoming_info.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/get_incoming_info.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                AsteriskAccount asteriskAccount = setting.loadAsteriskAccount(context);
                postParams.put("user_id",       setting.loadUserName(context));
                postParams.put("user_pass",     setting.loadPassword(context));
                postParams.put("sip_id",        asteriskAccount.sipId); // オンプレ用
                postParams.put("searchNum",     telNum.getBaseString());
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * ログイン状態を確認する
     */
    public static synchronized void checkLoginStatus(final Context context,
                                                     Response.Listener ok,
                                                     Response.ErrorListener err) {
        // 設定により接続先URLを設定する
        String url;
        final Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            String sipServer = setting.loadLocalServerDomain(context);
            url = String.format("http://%s:50080/v1_0/address/check_status.php", sipServer);
        } else {
            String sipServer = setting.loadRemoteServerDomain(context);
            url = String.format("https://%s:50443/v1_0/address/check_status.php", sipServer);
        }

        // リクエストを作成
        StringRequest sr = new StringRequest(Request.Method.POST, url, ok, err) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();
                AsteriskAccount asteriskAccount = setting.loadAsteriskAccount(context);
                postParams.put("sip_id", asteriskAccount.sipId);
                return postParams;
            }
        };
        sr = setRetryPolicy(sr);

        // キューにリクエストを追加
        RequestQueue requestQueue = BuzbizApplication.getVolleyRequestQueue(context);
        requestQueue.add(sr);
    }





    /**
     * Basic認証用のStringRequestを作成する
     *
     * ※BUZBIZ miniではログインアカウントをBasic認証に流用する
     *
     * @param context コンテキスト
     * @param url     URL
     * @param ok      通信成功時の処理
     * @param err     通信失敗時の処理
     * @return Basic認証用のStringRequest
     */
//    private static synchronized StringRequest createStringRequestForBasicAuth(
//            final Context context,
//            String url,
//            Response.Listener ok,
//            Response.ErrorListener err) {
//        return new StringRequest(Request.Method.GET, url, ok, err) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = super.getHeaders();
//
//                // HTTPのヘッダに認証情報を追加する
//                Setting setting = new Setting();
//                String user = setting.loadSipTelNumber(context);
//                String pass = setting.loadSipPassword(context);
//                String userpassword = user + ":" + pass;
//                final String encodedUserpassword = new String(Base64.encode(userpassword.getBytes(), Base64.DEFAULT));
//
//                Map<String, String> newHeaders = new HashMap<>();
//                newHeaders.putAll(headers);
//                newHeaders.put("Authorization", "Basic " + encodedUserpassword);
//                return newHeaders;
//            }
//        };
//    }





    /**
     * Requestのポリシーを変更する
     *
     * @param request 対象のRequest
     * @return ポリシーを変更済みのRequest
     */
    private static synchronized StringRequest setRetryPolicy(StringRequest request) {
        /* タイムアウトを10秒に変更 */
        int timeoutMilliSec = 10 * 1000;

        DefaultRetryPolicy policy = new DefaultRetryPolicy(
                timeoutMilliSec,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );

        request.setRetryPolicy(policy);
        return request;
    }
}