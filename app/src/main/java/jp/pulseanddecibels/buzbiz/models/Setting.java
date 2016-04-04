package jp.pulseanddecibels.buzbiz.models;



import android.content.Context;
import android.text.TextUtils;

import jp.pulseanddecibels.buzbiz.data.AsteriskAccount;
import jp.pulseanddecibels.buzbiz.util.Util;





/**
 * 設定
 *
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


    private static final String TAG_ASTERISK_CONNECTION_TYPE = "TAG_ASTERISK_CONNECTION_TYPE";


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
     */
    public void saveLocalServerInfo(Context context, String lDomain) {
        File.saveData(context, TAG_LOCAL_SERVER_DOMAIN, lDomain);
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


    public ConnectionType getLastConnection(Context context) {
        String name = File.getValue(context, TAG_ASTERISK_CONNECTION_TYPE);
        return ConnectionType.getConnectionTypeMatching(name);
    }


    public void setLastConnection(Context context, ConnectionType type) {
        File.saveData(context, TAG_ASTERISK_CONNECTION_TYPE, type.toString());
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
        return isLocalInfoOk || isRemoteInfoOk;
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
        return !TextUtils.isEmpty(lDomain);
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
        if (getLastConnection(context) == ConnectionType.LOCAL) {
            return File.getValue(context, TAG_LOCAL_SERVER_DOMAIN);
        } else if (getLastConnection(context) == ConnectionType.REMOTE) {
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





    // ------------- コーデック関連 -------------
    private static final int ON  = 100;
    private static final int OFF = 200;


    /**
     * GSMコーデック
     */
    private static final String TAG_GSM = "TAG_GSM";

    /**
     * ULAWコーデック
     */
    private static final String TAG_ULAW = "TAG_ULAW";



    /**
     * GSMの設定を保存
     *
     * @param context コンテキスト
     * @param isOn    使用するかどうか
     */
    public void saveGSM(Context context, boolean isOn) {
        int value = getValue(isOn);
        File.saveData(context, TAG_GSM, value);
    }



    /**
     * ULAWの設定を保存
     *
     * @param context コンテキスト
     * @param isOn    使用するかどうか
     */
    public void saveULAW(Context context, boolean isOn) {
        int value = getValue(isOn);
        File.saveData(context, TAG_ULAW, value);
    }


    /**
     * GSMの設定をロード
     *
     * @param context コンテキスト
     */
    public boolean loadGSM(Context context) {
        int value = File.getInt(context, TAG_GSM);

        // GSMのみデフォルトはONとする
        return checkOnOff_DefaultOn(value);
    }


    /**
     * ULAWの設定をロード
     *
     * @param context コンテキスト
     */
    public boolean loadULAW(Context context) {
        int value = File.getInt(context, TAG_ULAW);

        // GSMのみデフォルトはONとする
        return checkOnOff_DefaultOn(value);
    }


    private int getValue(boolean isOn) {
        if (isOn) {
            return ON;
        } else {
            return OFF;
        }
    }





    private boolean checkOnOff_DefaultOff(int value) {
        return value == ON;
    }





    private boolean checkOnOff_DefaultOn(int value) {
        if (value == ON) {
            return true;
        } else return value <= 0;
    }





    private int formatAutoGainvalue(int value) {
        if (value < 0) {
            return 2;
        }
        if (value > 20) {
            return 20;
        }
        return value;
    }
}