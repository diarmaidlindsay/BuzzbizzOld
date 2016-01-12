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





    // ------------- コーデック関連 -------------
    private static final int ON  = 100;
    private static final int OFF = 200;

    /**
     * G711Uコーデック
     */
    private static final String TAG_G711U = "TAG_G711U";

    /**
     * Opusコーデック
     */
    private static final String TAG_OPUS = "TAG_OPUS";

    /**
     * iLBCコーデック
     */
    private static final String TAG_ILBC = "TAG_ILBC";

    /**
     * GSMコーデック
     */
    private static final String TAG_GSM = "TAG_GSM";

    /**
     * スピーカーソフトブースト
     */
    private static final String TAG_SSB = "TAG_SSB";

    /**
     * スピーカーAutoGain
     */
    private static final String TAG_SAG = "TAG_SAG";

    /**
     * マイクソフトブースト
     */
    private static final String TAG_MSB = "TAG_MSB";

    /**
     * マイクAutoGain
     */
    private static final String TAG_MAG = "TAG_MAG";





    /**
     * G711Uの設定を保存
     *
     * @param context コンテキスト
     * @param isOn    使用するかどうか
     */
    public void saveG711U(Context context, boolean isOn) {
        int value = getValue(isOn);
        File.saveData(context, TAG_G711U, value);
    }





    /**
     * Opusの設定を保存
     *
     * @param context コンテキスト
     * @param isOn    使用するかどうか
     */
    public void saveOpus(Context context, boolean isOn) {
        int value = getValue(isOn);
        File.saveData(context, TAG_OPUS, value);
    }





    /**
     * iLBCの設定を保存
     *
     * @param context コンテキスト
     * @param isOn    使用するかどうか
     */
    public void saveIlbc(Context context, boolean isOn) {
        int value = getValue(isOn);
        File.saveData(context, TAG_ILBC, value);
    }





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
     * スピーカーソフトブーストの設定を保存
     *
     * @param context コンテキスト
     * @param isOn    使用するかどうか
     */
    public void saveSpkSoftBoost(Context context, boolean isOn) {
        int value = getValue(isOn);
        File.saveData(context, TAG_SSB, value);
    }





    /**
     * スピーカーAutoGainの設定を保存
     *
     * @param context コンテキスト
     * @param value   設定する値
     */
    public void saveSpkAutoGain(Context context, int value) {
        File.saveData(context, TAG_SAG, value);
    }





    /**
     * マイクソフトブーストの設定を保存
     *
     * @param context コンテキスト
     * @param isOn    使用するかどうか
     */
    public void saveMicSoftBoost(Context context, boolean isOn) {
        int value = getValue(isOn);
        File.saveData(context, TAG_MSB, value);
    }





    /**
     * マイクAutoGainの設定を保存
     *
     * @param context コンテキスト
     * @param value   設定する値
     */
    public void saveMicAutoGain(Context context, int value) {
        File.saveData(context, TAG_MAG, value);
    }





    /**
     * G711Uの設定をロード
     *
     * @param context コンテキスト
     */
    public boolean loadG711U(Context context) {
        int value = File.getInt(context, TAG_G711U);
        return checkOnOff_DefaultOff(value);
    }





    /**
     * Opusの設定をロード
     *
     * @param context コンテキスト
     */
    public boolean loadOpus(Context context) {
        int value = File.getInt(context, TAG_OPUS);
        return checkOnOff_DefaultOff(value);
    }





    /**
     * iLBCの設定をロード
     *
     * @param context コンテキスト
     */
    public boolean loadIlbc(Context context) {
        int value = File.getInt(context, TAG_ILBC);
        return checkOnOff_DefaultOff(value);
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
     * スピーカーソフトブーストの設定をロード
     *
     * @param context コンテキスト
     */
    public boolean loadSpkSoftBoost(Context context) {
        int value = File.getInt(context, TAG_SSB);
        return checkOnOff_DefaultOn(value);
    }





    /**
     * スピーカーAutoGainの設定をロード
     *
     * @param context コンテキスト
     */
    public int loadSpkAutoGain(Context context) {
        int value = File.getInt(context, TAG_SAG);
        return formatAutoGainvalue(value);
    }





    /**
     * マイクソフトブーストの設定をロード
     *
     * @param context コンテキスト
     */
    public boolean loadMicSoftBoost(Context context) {
        int value = File.getInt(context, TAG_MSB);
        return checkOnOff_DefaultOff(value);
    }





    /**
     * マイクAutoGainの設定をロード
     *
     * @param context コンテキスト
     */
    public int loadMicAutoGain(Context context) {
        int value = File.getInt(context, TAG_MAG);
        return formatAutoGainvalue(value);
    }





    private int getValue(boolean isOn) {
        if (isOn) {
            return ON;
        } else {
            return OFF;
        }
    }





    private boolean checkOnOff_DefaultOff(int value) {
        if (value == ON) {
            return true;
        } else {
            return false;
        }
    }





    private boolean checkOnOff_DefaultOn(int value) {
        if (value == ON) {
            return true;
        } else if (value <= 0) {
            return true;
        } else {
            return false;
        }
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