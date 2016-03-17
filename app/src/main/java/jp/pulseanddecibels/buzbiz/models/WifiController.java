package jp.pulseanddecibels.buzbiz.models;



import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import jp.pulseanddecibels.buzbiz.util.Util;


/**
 * Wi-Fiの操作用
 *
 */
public class WifiController {

    /**
     * 現在接続しているWi-FiのSSIDを取得する
     *
     * @param context コンテキスト
     * @return SSID
     */
    public String getConnectionSsid(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // Wi-Fiが使用可能でない場合は空文字を返す
        if (!wifiManager.isWifiEnabled()) {
            return Util.STRING_EMPTY;
        }

        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();

        // フォーマット
        if(TextUtils.isEmpty(ssid)){
            return Util.STRING_EMPTY;
        }
        if(ssid.startsWith("\"")){
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        if(ssid.endsWith("\"")){
            ssid = ssid.substring(0, ssid.length() - 1);
        }

        return ssid;
    }
}