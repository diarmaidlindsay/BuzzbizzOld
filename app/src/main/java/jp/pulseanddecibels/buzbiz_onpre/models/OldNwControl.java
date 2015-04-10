//package jp.pulseanddecibels.buzbiz_onpre.models;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.security.KeyStore;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//import jp.pulseanddecibels.buzbiz_onpre.ExternalTelListItem;
//import jp.pulseanddecibels.buzbiz_onpre.HistoryListItem;
//import jp.pulseanddecibels.buzbiz_onpre.HoldListItem;
//import jp.pulseanddecibels.buzbiz_onpre.InternalTelListItem;
//import jp.pulseanddecibels.buzbiz_onpre.MainService;
//import jp.pulseanddecibels.buzbiz_onpre.R;
//import jp.pulseanddecibels.buzbiz_onpre.data.TelNummber;
//import jp.pulseanddecibels.buzbiz_onpre.util.Logger;
//import jp.pulseanddecibels.buzbiz_onpre.util.Util;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.ResponseHandler;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.scheme.PlainSocketFactory;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.scheme.SchemeRegistry;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.conn.SingleClientConnManager;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//
///**
// *
// * ネットワーク操作用クラス
// *
// * @author 普天間
// *
// */
//public class OldNwControl {
//
//	/** 証明書を保存する為のバイト配列 */
//	public static final byte [] cert = new byte[2048];
//	/** 証明書のパスワード */
//	final static char[] certPasswd = "buzbiz".toCharArray();
//
//
//
//
//
//	/**
//	 * 外線帳用Json文字列を解析する
//	 * @param jsonStr	json文字列
//	 * @return 			電話帳アイテムの可変配列
//	 */
//	public static ArrayList<ExternalTelListItem> parceJsonForExternalTable(String jsonStr) {
////		Log.e(Util.LOG_TAG,"  NwControl.parceJsonForExternalTable  ");
//
//
//		// 結果を収める可変配列
//		ArrayList<ExternalTelListItem> resultArray = new ArrayList<ExternalTelListItem>();
//
//		// Jsonオブジェクトの生成
//		try {
//			// 今回は配列形式のJSONの為、JSONArrayで
//			JSONArray jsonArray = new JSONArray(jsonStr);
//
//			int size = jsonArray.length();
//			for (int i=0; i<size; i++) {
//
//				JSONObject singleJsonObj = jsonArray.getJSONObject(i);	// ArrayList.get(i) とほぼ同じ
//
//
//				// 電話帳アイテムを作成
//				ExternalTelListItem fortuneResult = new ExternalTelListItem(singleJsonObj.getInt("customer_id"),
//																			singleJsonObj.getString("customer_name"),
//																			singleJsonObj.getString("customer_name_kana"),
//																			singleJsonObj.getString("date_add"),
//																			singleJsonObj.getString("date_modify"),
//																			singleJsonObj.getString("del_flag"),
//																			singleJsonObj.getString("fax"),
//																			singleJsonObj.getString("tel"));
//
//
//				// ArrayListに格納
//				resultArray.add(fortuneResult);
//			}
//
//		// 解析に失敗したときは、nullを戻す
//		} catch (JSONException e) {
////			Log.e(Util.LOG_TAG,"  外線帳用Json文字列を解析失敗  " + e.getMessage());
//
//
//			return null;
//		}
//
//
//		return resultArray;
//	}
//
//
//
//
//
//	/**
//	 * 内線帳用Json文字列を解析する
//	 * @param jsonStr	json文字列
//	 * @return 			電話帳アイテムの可変配列
//	 */
//	public static ArrayList<InternalTelListItem> parceJsonForInternalTable(String jsonStr, Context context) {
////		Log.e(Util.LOG_TAG,"  NwControl.parceJsonForInternalTable  ");
//
//
//		// 結果を収める可変配列
//		ArrayList<InternalTelListItem> resultArray = new ArrayList<InternalTelListItem>();
//
//		// Jsonオブジェクトの生成
//		try {
//			// 今回は配列形式のJSONの為、JSONArrayで
//			JSONArray jsonArray = new JSONArray(jsonStr);
//
//
//			int size = jsonArray.length();
//			for (int i=0; i<size; i++) {
//
//				JSONObject singleJsonObj = jsonArray.getJSONObject(i);	// ArrayList.get(i) とほぼ同じ
//
//
//				String uid = singleJsonObj.getString("sip_id");
//
//
//				// 電話帳アイテムを作成
//				InternalTelListItem fortuneResult = new InternalTelListItem(singleJsonObj.getInt("id"),
//																			singleJsonObj.getString("sip_group_id"),
//																			singleJsonObj.getString("department_name"),
//																			uid,
//																			singleJsonObj.getString("user_name"),
//																			singleJsonObj.getString("user_name_kana"),
//																			singleJsonObj.getInt("login_status"));
//
//
//				// ArrayListに格納
//				resultArray.add(fortuneResult);
//			}
//
//		// 解析に失敗したときは、nullを戻す
//		} catch (JSONException e) {
////			Log.e(Util.LOG_TAG,"  内線帳用Json文字列を解析失敗  " + e.getMessage());
//
//
//			return null;
//		}
//
//
//		return resultArray;
//	}
//
//
//
//
//
//	/**
//	 * 保留リスト用Json文字列を解析する
//	 * @param jsonStr	json文字列
//	 * @return 			保留リストアイテムの可変配列
//	 */
//	public static ArrayList<HoldListItem> parceJsonForHoldList(String jsonStr) {
////		Log.e(Util.LOG_TAG,"  NwControl.parceJsonForHoldList  ");
//
//
//		// 結果を収める可変配列
//		ArrayList<HoldListItem> resultArray = new ArrayList<HoldListItem>();
//
//		// 空の場合
//		if (TextUtils.isEmpty(jsonStr) || jsonStr.equals("[]")) {
//			return resultArray;
//		}
//
//		// Jsonオブジェクトの生成
//		try {
//			// 今回は配列形式のJSONの為、JSONArrayで
//			JSONArray jsonArray = new JSONArray(jsonStr);
//
//			int size = jsonArray.length();
//			for (int i=0; i<size; i++) {
//
//				JSONObject singleJsonObj = jsonArray.getJSONObject(i);
//
//
//				// 電話帳アイテムを作成
//				HoldListItem fortuneResult = new HoldListItem(singleJsonObj.getInt("ID"),
//															  singleJsonObj.getString("ParkingNum"),
//															  singleJsonObj.getString("Caller"),
//															  singleJsonObj.getString("CallerName"),
//															  singleJsonObj.getString("Responders"),
//															  singleJsonObj.getString("HoldTime"));
//
//
//				// ArrayListに格納
//				resultArray.add(fortuneResult);
//			}
//
//		} catch (JSONException e) {
////			Log.e(Util.LOG_TAG,"  保留リスト用Json文字列を解析失敗  " + e.getMessage());
//			e.getStackTrace();
//		}
//
//		return resultArray;
//	}
//
//
//
//
//
//	/**
//	 * 保留を確認し、指定の名前が含まれているかチェック
//	 * @param jsonStr	json文字列
//	 * @param name		指定の名前
//	 * @return	結果
//	 */
//	public static boolean checkHoldName(String jsonStr, String name) {
////		Log.e(Util.LOG_TAG,"  NwControl.checkHoldMe  ");
//
//
//		if(jsonStr == null || name == null){
//			return false;
//		}
//
//		// Jsonオブジェクトの生成
//		try {
//			// 今回は配列形式のJSONの為、JSONArrayで
//			JSONArray jsonArray = new JSONArray(jsonStr);
//
//			int size = jsonArray.length();
//			for (int i=0; i<size; i++) {
//
//				JSONObject singleJsonObj = jsonArray.getJSONObject(i);
//
//				if(singleJsonObj.getString("Caller").equals(name)){
//					return true;
//				}
//
//			}
//		} catch (JSONException e) { }
//
//
//		return false;
//	}
//
//
//
//
//
//	/**
//	 * 履歴リスト用Json文字列を解析する
//	 * @param jsonStr	json文字列
//	 * @return 			保留リストアイテムの可変配列
//	 */
//	public static ArrayList<HistoryListItem> parceJsonForHistoryList(String jsonStr) {
////		Log.e(Util.LOG_TAG,"  NwControl.parceJsonForHistoryList  ");
//
//
//		// 結果を収める可変配列
//		ArrayList<HistoryListItem> resultArray = new ArrayList<HistoryListItem>();
//
//		// カレンダーを作成
//		BuzbizCalendar calendar = new BuzbizCalendar();
//
//
//		// Jsonオブジェクトの生成
//		try {
//			// 今回は配列形式のJSONの為、JSONArrayで
//			JSONArray jsonArray = new JSONArray(jsonStr);
//
//			int size = jsonArray.length();
//			for (int i=0; i<size; i++) {
//
//				JSONObject singleJsonObj = jsonArray.getJSONObject(i);
//
//				// 電話帳アイテムを作成
//				HistoryListItem fortuneResult = new HistoryListItem(i,
//																	singleJsonObj.getString("date"),
//																	singleJsonObj.getString("src"),
//																	singleJsonObj.getString("srcname"),
//																	singleJsonObj.getString("dst"),
//																	singleJsonObj.getString("disposition"),
//																	calendar);
//
//
//				// ArrayListに格納
//				resultArray.add(fortuneResult);
//			}
//
//		// 解析に失敗したときは、nullを戻す
//		} catch (JSONException e) {
////			Log.e(Util.LOG_TAG,"  履歴リスト用Json文字列を解析失敗  " + e.getMessage());
//
//			return null;
//		}
//
//
//		return resultArray;
//	}
//
//
//
//
//
//	/**
//	 * ログイン情報用Json文字列を解析する
//	 * @param jsonStr	json文字列
//	 * @return			解析結果   要素0:ID   要素1:パスワード
//	 */
//	public static String[] parceJsonForLoginInfo(String jsonStr){
////		Log.e(Util.LOG_TAG,"  NwControl.parceJsonForLoginInfo  ");
//
//
//		// 結果を収める配列
//		String[] resultArray = new String[3];
//
//
//		// Jsonオブジェクトの生成
//		try {
//			// 今回は配列形式のJSONの為、JSONArrayで
//			JSONArray jsonArray = new JSONArray(jsonStr);
//
//			int size = jsonArray.length();
//			for (int i=0; i<size; i++) {
//
//				JSONObject singleJsonObj = jsonArray.getJSONObject(i);
//
//
//				resultArray[0] = singleJsonObj.getString("sip_id");
//				resultArray[1] = singleJsonObj.getString("sip_pass");
//				resultArray[2] = singleJsonObj.getString("sip_group_id");
//			}
//
//		// 解析に失敗したときは、nullを戻す
//		} catch (JSONException e) {
////			Log.e(Util.LOG_TAG,"  ログイン情報用Json文字列を解析失敗  " + e.getMessage());
//			return null;
//		}
//		return resultArray;
//	}
//
//
//
//
//
//	/**
//	 * 名前検索用Json文字列を解析する
//	 * @param jsonStr	json文字列
//	 * @return			名前
//	 */
//	public static String parceJsonForSerchName(String jsonStr, TelNummber telNum){
////		Log.e(Util.LOG_TAG,"  NwControl.parceJsonForSerchName  ");
//
//		try {
//			String rusult			= Util.STRING_EMPTY;
//			String group			= Util.STRING_EMPTY;
//			String myAddresses		= Util.STRING_EMPTY;
//			String name				= Util.STRING_EMPTY;
//			String num				= Util.STRING_EMPTY;
//			String groupName		= Util.STRING_EMPTY;
//			String groupNum			= Util.STRING_EMPTY;
//
//
//			// 解析
//			JSONObject singleJsonObj = new JSONObject(jsonStr);
//			group = singleJsonObj.getString("group");
//			myAddresses = singleJsonObj.getString("myAddresses");
//
//			JSONArray jsonArray;
//			if(!TextUtils.isEmpty(myAddresses) && !"[]".equals(myAddresses)){
//				jsonArray = new JSONArray(myAddresses);
//				JSONObject groupJsonObj = jsonArray.getJSONObject(0);
//				name = groupJsonObj.getString("name");
//				num  = groupJsonObj.getString("num");
//			}
//
//			if(!TextUtils.isEmpty(group) && !"[]".equals(group)){
//				jsonArray = new JSONArray(group);
//				JSONObject myAddressesJsonObj = jsonArray.getJSONObject(0);
//				groupName = myAddressesJsonObj.getString("groupName");
//				groupNum  = myAddressesJsonObj.getString("groupNum");
//			}
//
//
//			// ラベル情報の作成
//			if (telNum.isInternal()) {
//				rusult = "内線";
//				if (!TextUtils.isEmpty(name)) {
//					rusult += "\n" + name;
//				}
//			} else {
//				if (!TextUtils.isEmpty(name)) {
//					rusult += name;
//				}
//				if (!TextUtils.isEmpty(rusult)) {
//					rusult += "\n";
//				}
//				if (!TextUtils.isEmpty(num)) {
//					rusult += num;
//				}
//				if (!TextUtils.isEmpty(groupName)) {
//					rusult += "\n(回線: " + groupName + ")";
//				} else if (!TextUtils.isEmpty(groupNum)) {
//					rusult += "\n(回線: " + groupNum  + ")";
//				}
//			}
//
//			rusult = rusult.replace("anonymous", "非通知");
//			return rusult;
//		} catch (Exception e) {
//			return "不明";
//		}
//	}
//
//
//
//
//	/**
//	 * 電話番号より名前を調べる
//	 * 		※ 電話帳に存在しない場合は空文字列を返す
//	 * @param telNum	検索する電話番号
//	 * @return			検索結果
//	 */
//	public static String serchNameFormTelNum(final TelNummber telNum, final Context context){
////		Log.e(Util.LOG_TAG,"  NwControl.serchNameFormTelNum  ");
//
//
//		RunnableForGettingInfo run = new RunnableForGettingInfo(){
//			@Override
//			public void run() {
//				// HTTP 通信開始
//				String url = String.format(context.getString(R.string.name_serch_url), MainService.getSipServerIp());
//				String[] keys	= {"user_id", "user_pass", "searchNum"};
//				String[] values	= {File.getValue(context, File.LOGIN_SUCCESS_BUZBIZ_NAME),
//								   File.getValue(context, File.LOGIN_SUCCESS_BUZBIZ_SIPPASSWORD),
//						   		   telNum.getBaseString()};
//				info = doPost(url, keys, values, context);
//			}
//		};
//		Thread thread = new Thread (run);
//		thread.start();
//		try {
//			thread.join(5000);
//		} catch (InterruptedException e) { }
//
//
//		// JSON文字列を解析
//		if (!TextUtils.isEmpty(run.info)){
//			return parceJsonForSerchName(run.info, telNum);
//		}
//
//		if (telNum.isEmpty()) {
//			return "不明";
//		} else {
//			return telNum.getBaseString();
//		}
//	}
//
//
//
//
//
//	/**
//	 * 証明書を取得
//	 * @param serverIp	サーバーのIP
//	 */
//	public static void getCertificate(String serverIp, Context context) {
////		Log.e(Util.LOG_TAG,"  NwControl.getCertificate  ");
//
//
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		String urlStr = String.format(context.getString(R.string.cert_url), serverIp);
//
//
//		try {
//			//証明書情報 全て空を返す
//			TrustManager[] tm = { new X509TrustManager() {
//				public X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//				@Override
//				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
//				@Override
//				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
//			} };
//			SSLContext sslcontext = SSLContext.getInstance("SSL");
//			sslcontext.init(null, tm, null);
//
//			// ホスト名の検証ルール 何が来てもtrueを返す
//			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//				@Override
//				public boolean verify(String hostname, SSLSession session) {
//					return true;
//				}
//			});
//
//
//			URL url = new URL(urlStr);
//			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
//	        https.setRequestMethod("GET");
//	        https.setSSLSocketFactory(sslcontext.getSocketFactory());
//	        https.connect();
//
//	        InputStream in = https.getInputStream();
//
//			for (int i = 0; i < 10; i++) {
//				int len = in.read(cert);
//				if (len < 0) {
//					break;
//				}
//				os.write(cert, 0, len);
//			}
//
//			os.close();
//	        in.close();
//		} catch (Exception e) {
//            Logger.e("Certificate err " + e);
//		}
//	}
//
//
//
//
//
//	/**
//	 * Postで通信を行う
//	 *
//	 * @param strUrl	アドレス
//	 * @param keys		ポストするキー配列
//	 * @param values	ポストする値配列
//	 * @param context	コンテキスト
//	 * @return			JSON文字列 ( 失敗した場合はnullを返す )
//	 */
//	public static String doPost(String strUrl,
//								String[] keys,
//								String[] values,
//								Context context) {
////		Log.d(Util.LOG_TAG,"  NwControl.doPost  ");
//
//
//		String postResult = null;		// 通信結果
//
//
//		// URLを生成
//		URI url = null;
//		try {
//			url = new URI(strUrl);
////			Log.d(Util.LOG_TAG, "URL生成に成功");
//		} catch (URISyntaxException e) {
////			Log.d(Util.LOG_TAG, "URL生成に失敗");
//			return null;
//		}
//
//
//		// POSTリクエストを構築
//		HttpPost request = new HttpPost(url);
//		if(keys != null && values != null){
//			List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//			try {
//				for(int i = 0; i < keys.length; i++){
//					post_params.add(new BasicNameValuePair(keys[i], values[i]));
//				}
//
//				// 送信パラメータのエンコードを指定
//				request.setEntity(new UrlEncodedFormEntity(post_params, "UTF-8"));
////				Log.d(Util.LOG_TAG, "POSTリクエストを構築に成功");
//			} catch (Exception e) {
////				Log.d(Util.LOG_TAG, "POSTリクエストを構築に失敗");
//				return null;
//			}
//		}
//
//
//		if(context == null){
//			return null;
//		}
//
//
//		final ByteArrayInputStream in = new ByteArrayInputStream(cert);
//
//
//		// http通信クライアントを作成
//		final HttpClient client = new DefaultHttpClient() {
//			@Override
//			protected ClientConnectionManager createClientConnectionManager() {
//				SchemeRegistry registry = new SchemeRegistry();
//				registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory(), 80));
//				try {
//					registry.register(new Scheme("https",createSSLSocketFactory(), 443));
//				} catch (Exception e) {}
//				return new SingleClientConnManager(getParams(),registry);
//			}
//
//			private SSLSocketFactory createSSLSocketFactory()throws Exception {
//				KeyStore keyStore = KeyStore.getInstance("BKS");
//				try {
//					keyStore.load(in, certPasswd);
//				} finally {
//					in.close();
//				}
//				SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
//				socketFactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//				return socketFactory;
//			}
//		};
//
//
//		try {
////			Log.d(Util.LOG_TAG, "POST開始 " + request);
//			postResult = client.execute(request,
//					new ResponseHandler<String>() {
//						@Override
//						public String handleResponse(HttpResponse response)throws IOException {
////							Log.d(Util.LOG_TAG, "レスポンスコード："+ response.getStatusLine().getStatusCode());
//							switch (response.getStatusLine().getStatusCode()) {
//							// 正常に受信できた場合は、
//							case HttpStatus.SC_OK:
////								Log.d(Util.LOG_TAG, "レスポンス取得に成功");
//
//								// レスポンスデータをエンコード済みの文字列として取得する
//								return EntityUtils.toString(response.getEntity(),"UTF-8");
//
//							// エラーの場合は、
//							case HttpStatus.SC_NOT_FOUND:
////								Log.d(Util.LOG_TAG, "データが存在しない");
//								return null;
//							default:
////								Log.d(Util.LOG_TAG, "通信エラー");
//								return null;
//							}
//						}
//					});
//
//		} catch (Exception e) {
////			Log.d(Util.LOG_TAG, "通信に失敗：" + e.getMessage());
//			return null;
//		} finally {
//			// 終了処理
//			client.getConnectionManager().shutdown();
//		}
//		return postResult;
//	}
//
//
//
//
//
//	/**
//	 * GCMへのレジスト処理
//	 */
//	public static boolean registeGcm(final Context context) {
//
//		final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
//
//		try {
//			String token = gcm.register("1004302863274");
//
////			Log.e(Util.LOG_TAG, "token " + token);
//
//			// ---------------------------------------------
//			// ★platformの値
//			// 0 ipad
//			// 1 iphone
//			// 2 android
//			// ---------------------------------------------
//			String url = String.format(context.getString(R.string.push_registe_url), MainService.getSipServerIp());
//			String[] keys	= {"user_id", "user_pass", "platform", "token"};
//			String[] values	= {File.getValue(context, File.LOGIN_SUCCESS_BUZBIZ_NAME),
//							   File.getValue(context, File.LOGIN_SUCCESS_BUZBIZ_SIPPASSWORD),
//							   "2",
//							   token};
//			String result = doPost(url, keys, values, context);
//
////			Log.e(Util.LOG_TAG, "result " + result);
//
//			if(!"ok".equals(result)){
//				return false;
//			}
//
//			return true;
//		} catch (Exception ex) {
//            Logger.e("registeGcm fail" + ex.getMessage());
//		}
//
//		return false;
//	}
//}