package jp.pulseanddecibels.buzbiz_onpre.models;



import android.content.Context;
import android.text.TextUtils;

import jp.pulseanddecibels.buzbiz_onpre.data.AsteriskAccount;
import jp.pulseanddecibels.buzbiz_onpre.util.Util;





/**
 * 設定
 *
 * @author 普天間
 */
public class Setting {

    /**
     * 内線番号
     */
    private static final String TAG_USER_NAME = "TAG_USER_NAME";

    /**
     * パスワード
     */
    private static final String TAG_PASSWORD = "TAG_PASSWORD";

    /**
     * ローカルでのサーバのドメイン
     */
    private static final String TAG_LOCAL_SERVER_DOMAIN = "TAG_LOCAL_SERVER_DOMAIN";

    /**
     * 無線LANのSSID
     */
    private static final String TAG_SSID = "TAG_SSID";

    /**
     * リモートでのサーバのドメイン
     */
    private static final String TAG_REMOTE_SERVER_DOMAIN = "TAG_REMOTE_SERVER_DOMAIN";

    /**
     * ライセンスキー
     */
    private static final String TAG_LICENCE_KEY = "LICENCE_KEY";

    /**
     * AsteriskのID
     */
    private static final String TAG_ASTERISK_ID = "TAG_ASTERISK_ID";

    /**
     * Asteriskのパスワード
     */
    private static final String TAG_ASTERISK_PASS = "TAG_ASTERISK_PASS";

    /**
     * AsteriskのグループID
     */
    private static final String TAG_ASTERISK_GROUP_ID = "TAG_ASTERISK_GROUP_ID";





    /**
     * アカウントを保存する
     *
     * @param context  コンテキスト
     * @param userName ユーザ名
     * @param password パスワード
     */
    public void saveAccount(Context context, String userName, String password) {
        File.saveData(context, TAG_USER_NAME, userName);
        File.saveData(context, TAG_PASSWORD, password);
    }





    /**
     * ローカルサーバの情報を保存する
     *
     * @param context コンテキスト
     * @param lDomain ローカルでのサーバのドメイン
     * @param ssid　無線LANのSSID
     */
    public void saveLocalServerInfo(Context context, String lDomain, String ssid) {
        File.saveData(context, TAG_LOCAL_SERVER_DOMAIN, lDomain);
        File.saveData(context, TAG_SSID,                ssid);
    }





    /**
     * ローカルサーバの情報を保存する
     *
     * @param context コンテキスト
     * @param rDomain リモートでのサーバのドメイン
     */
    public void saveRemoteServerInfo(Context context, String rDomain) {
        File.saveData(context, TAG_REMOTE_SERVER_DOMAIN, rDomain);
    }





    /**
     * Asteriskアカウントを保存する
     *
     * @param context         コンテキスト
     * @param asteriskAccount リモートでのサーバのドメイン
     */
    public void saveAsteriskAccount(Context context, AsteriskAccount asteriskAccount) {
        File.saveData(context, TAG_ASTERISK_ID,       asteriskAccount.sipId);
        File.saveData(context, TAG_ASTERISK_PASS,     asteriskAccount.sipPass);
        File.saveData(context, TAG_ASTERISK_GROUP_ID, asteriskAccount.sipGroupId);
    }





    /**
     * ライセンスキーを保存する
     *
     * @param context コンテキスト
     * @param key ライセンスキー
     */
    public void saveLicenceKey(Context context, String key) {
        File.saveData(context, TAG_LICENCE_KEY, key);
    }





    /**
     * 設定が存在するか確認する
     *
     * @return 存在すればtrue
     */
    public boolean isExistSetting(Context context) {
        // アカウントが設定されていない場合は失敗
        if (!isExistSavedAccount(context)) {
            return false;
        }

        // ローカルサーバの設定を確認
        boolean isLocalInfoOk = isExistSavedLocalServerInfo(context);

        // リモートサーバの設定を確認
        boolean isRemoteInfoOk = isExistSavedRemoteServerInfo(context);

        // ローカル,リモートのどちらかの設定があれば成功
        if (isLocalInfoOk || isRemoteInfoOk) {
            return true;
        } else {
            return false;
        }
    }





    /**
     * アカウント情報を保存しているか確認する
     *
     * @param context コンテキスト
     * @return 結果
     */
    private boolean isExistSavedAccount(Context context) {
        String user = File.getValue(context, TAG_USER_NAME);
        String pass = File.getValue(context, TAG_PASSWORD);
        return (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass));
    }





    /**
     * ローカルサーバの情報が保存されているか確認する
     *
     * @param context コンテキスト
     * @return 結果
     */
    public boolean isExistSavedLocalServerInfo(Context context) {
        String lDomain = File.getValue(context, TAG_LOCAL_SERVER_DOMAIN);
        String ssid    = File.getValue(context, TAG_SSID);
        // 保存されていなければ失敗
        if (TextUtils.isEmpty(lDomain) || TextUtils.isEmpty(ssid)) {
            return false;
        }

        // 保存されたSSIDと現在のSSIDが違う場合も、失敗とする
        boolean ssidOk = ssid.equals(new WifiController().getConnectionSsid(context));
        if (ssidOk) {
            return true;
        } else {
            return false;
        }
    }





    /**
     * リモートサーバの情報が保存されているか確認する
     *
     * @param context コンテキスト
     * @return 結果
     */
    public boolean isExistSavedRemoteServerInfo(Context context) {
        String rDomain = File.getValue(context, TAG_REMOTE_SERVER_DOMAIN);
        return (!TextUtils.isEmpty(rDomain));
    }





    /**
     * 保存ファイルより現在使用すべきサーバのドメインをロードする
     *
     * @param context コンテキスト
     * @return サーバのドメイン
     */
    public String loadCurrentUseServerDomain(Context context) {
        Setting setting = new Setting();
        if (setting.isExistSavedLocalServerInfo(context)) {
            return File.getValue(context, TAG_LOCAL_SERVER_DOMAIN);
        } else if (setting.isExistSavedRemoteServerInfo(context)) {
            return File.getValue(context, TAG_REMOTE_SERVER_DOMAIN);
        } else {
            return Util.STRING_EMPTY;
        }
    }





    /**
     * 保存ファイルよりユーザ名をロードする
     *
     * @param context コンテキスト
     * @return 内線番号
     */
    public String loadUserName(Context context) {
        return File.getValue(context, TAG_USER_NAME);
    }





    /**
     * 保存ファイルよりパスワードをロードする
     *
     * @param context コンテキスト
     * @return パスワード
     */
    public String loadPassword(Context context){
        return File.getValue(context, TAG_PASSWORD);
    }





    /**
     * 保存ファイルよりローカルでのサーバのドメインをロードする
     *
     * @param context コンテキスト
     * @return ローカルでのサーバのドメイン
     */
    public String loadLocalServerDomain(Context context){
        return File.getValue(context, TAG_LOCAL_SERVER_DOMAIN);
    }





    /**
     * 保存ファイルより無線LANのSSIDをロードする
     *
     * @param context コンテキスト
     * @return 無線LANのSSID
     */
    public String loadSsid(Context context){
        return File.getValue(context, TAG_SSID);
    }





    /**
     * 保存ファイルよりリモートでのサーバのドメインをロードする
     *
     * @param context コンテキスト
     * @return リモートでのサーバのドメイン
     */
    public String loadRemoteServerDomain(Context context){
        return File.getValue(context, TAG_REMOTE_SERVER_DOMAIN);
    }





    /**
     * ライセンスキーをロードする
     *
     * @param context コンテキスト
     * @return ライセンスキー
     */
    public String loadLicenceKey(Context context) {
        return File.getValue(context, TAG_LICENCE_KEY);
    }





    /**
     * Asteriskアカウントをロードする
     *
     * @param context コンテキスト
     * @return Asteriskアカウント
     */
    public AsteriskAccount loadAsteriskAccount(Context context) {
        AsteriskAccount asteriskAccount = new AsteriskAccount();
        asteriskAccount.sipId       = File.getValue(context, TAG_ASTERISK_ID);
        asteriskAccount.sipPass     = File.getValue(context, TAG_ASTERISK_PASS);
        asteriskAccount.sipGroupId  = File.getValue(context, TAG_ASTERISK_GROUP_ID);
        return asteriskAccount;
    }
}