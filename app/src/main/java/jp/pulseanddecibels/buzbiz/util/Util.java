package jp.pulseanddecibels.buzbiz.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


import org.apache.http.conn.util.InetAddressUtils;


import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;





/**
 *
 * 汎用部品
 *
 *
 */
public class Util {

	/** 空文字列 */
	public static final String	STRING_EMPTY	= "";
	/** nullという文字列 */
	public static final String	NULL_KOMOJI		= "null";
	/** intの空文字 */
	public static final int		INT_EMPTY		= -9999;





	/**
	 * 指定時間分(ミリ秒)待機する
	 * @param miriSec	待機時間
	 */
	public static void waitMiriSec(int miriSec) {
		try {
			Thread.sleep(miriSec);
		} catch (InterruptedException e) {
			/** 待機できなくても無視 */
		}
	}





    /**
     * EditTextより入力値を取得する
     *
     * @param editText 対象のEditText
     * @return 入力値
     */
    public static String getText(EditText editText) {
        try {
            return editText.getText().toString().trim();
        } catch (Exception ex) {
            return STRING_EMPTY;
        }
    }





//	/**
//	 * スタックトレースから呼び出し元の基本情報を取得。
//	 *
//	 * @return <<クラス名  #メソッド名:行数>>
//	 */
//   public static String getStackTraceInfo() {
//       // 現在のスタックトレースを取得。
//       // 0:VM 1:スレッド 2:getStackTraceInfo() 3:outputLog() 4:logDebug()等 5:呼び出し元
//       StackTraceElement element = Thread.currentThread().getStackTrace()[5];
//
//       String fullName = element.getClassName();
//       String className = fullName.substring(fullName.lastIndexOf(".") + 1);
//       String methodName = element.getMethodName();
//       int lineNumber = element.getLineNumber();
//
//       return "<<" + className + "   #" + methodName + ":" + lineNumber + ">> ";
//   }





	/**
	 * JSONから解析した文字列のチェック
	 * @param str	チェックする文字列
	 * @return		空でない場合はそのまま返す、空の場合は空文字で返す
	 */
	public static String checkJsonParceString(String str){
		if(TextUtils.isEmpty(str) || str.equals(NULL_KOMOJI)){
			return STRING_EMPTY;
		}else {
			return str;
		}
	}





	/**
	 * キーボードを隠す
	 * @param activity
	 */
	public static void hideKeypad(Activity activity){
		try {
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			IBinder v = activity.getCurrentFocus().getWindowToken();
			if(v == null){
				return;
			}
	        imm.hideSoftInputFromWindow(v, InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception ex) { /** 隠せなくても無視 */ }
	}



//	/**
//	 * 自分のVPN用IPを取得する
//	 * @return	IPまたはVPNに接続していない場合は空文字を返す
//	 */
//	public static String getMyIPv4ForVPN(){
////		Log.e(LOG_TAG,"  Util.getMyIPv4ForVPN  ");
//
//		return getMyIPv4("ppp");
//	}
//
//
//
//
//
//	/**
//	 * 自分のWifi用IPを取得する
//	 * @return	IPまたはVPNに接続していない場合は空文字を返す
//	 */
//	public static String getMyIPv4ForWifi(){
////		Log.e(LOG_TAG,"  Util.getMyIPv4ForWifi  ");
//
//		return getMyIPv4("wlan");
//	}





	/**
	 * タイプを指定し、自分のIPを取得する
	 * @param serchInterfaceType	指定のインテーフェースタイプ
	 * @return	IP	（指定のインテーフェースにIPがふられていない場合は空文字を返す）
	 */
	private static String getMyIPv4(String serchInterfaceType){
//		Log.e(LOG_TAG,"  Util.getMyIPv4  ");

        try {
        	// システムの全ネットワークインタフェースを取得
        	Enumeration<NetworkInterface> allNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            // 全ネットワークインタフェースを1つづつ走査
            while( allNetworkInterfaces.hasMoreElements() ) {
            	// ネットワークインタフェースを読み込む
                NetworkInterface networkInterface = allNetworkInterfaces.nextElement();

                // 引数で指定されたインターフェイスであれば、
                if(networkInterface.getName().contains(serchInterfaceType)){
                    // ネットワークインタフェースに設定された全IPアドレスを取得
                    Enumeration<InetAddress> allIpAddress = networkInterface.getInetAddresses();

                    // ネットワークインタフェースに設定された全IPアドレスを1つづつ走査
                    while( allIpAddress.hasMoreElements() ) {
                    	// IPアドレスを読み込む
                        InetAddress ip = allIpAddress.nextElement();

                        // 文字列としてIPを取得
                        String ipString = ip.getHostAddress();

                        // IPがv4であれば返す
                        if (InetAddressUtils.isIPv4Address(ipString)) {
                            return ipString;
                        }
                    }
                    return STRING_EMPTY;
                }
            }
        } catch (Exception e) {}

        return STRING_EMPTY;
	}





	public static String splitGet(String str, String delimiter, int num) {
		if(TextUtils.isEmpty(str)){
			return Util.STRING_EMPTY;
		}

		String[] arr = str.split(delimiter);

		if ((num + 1) > arr.length) {
			return Util.STRING_EMPTY;
		}

		return arr[num];
	}
}