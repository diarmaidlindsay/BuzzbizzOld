package jp.pulseanddecibels.buzbiz.models;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import jp.pulseanddecibels.buzbiz.IncomingCallItem;
import jp.pulseanddecibels.buzbiz.MainService;
import jp.pulseanddecibels.buzbiz.data.TelNumber;
import jp.pulseanddecibels.buzbiz.pjsip.BuzBizCall;

/**
 *
 * 着信の制御
 *
 *
 */
public enum IncomingCallControl {
	INSTANCE;





	/** 着信情報一覧 */
	private final ConcurrentHashMap<String, IncomingCallItem> items =
			new ConcurrentHashMap<String, IncomingCallItem>();





	/**
	 * 着信中であるか確認
	 * @return
	 */
	public boolean isDuringIncomingCall() {
		synchronized (items) {
			if (items.size() == 0) {
				return false;
			} else {
				return true;
			}
		}
	}





	/**
	 * 着信情報を保存
	 * @param item	追加する着信情報
	 */
	public void addItem(IncomingCallItem item) {
		synchronized (items) {
			items.put(item.callId, item);
		}
	}





	/**
	 * 着信情報を削除
	 * @param callId	削除する着信ID
	 */
	public void removeItem(String callId) {
		synchronized (items) {
			items.remove(callId);
		}
	}





	/**
	 * 着信一覧を取得
	 */
	public ArrayList<IncomingCallItem> getIncomingCallList() {
		ArrayList<IncomingCallItem> result = new ArrayList<IncomingCallItem>();
		synchronized (items) {
			for (IncomingCallItem item : items.values()) {
				result.add(item.getNewCopy());
			}
		}
		return result;
	}





	/**
	 * 着信に応答
	 * @param callId	応答する着信ID
	 */
	public void answerTo(String callId, BuzBizCall call) {
		synchronized (items) {
			// 応答した着信アイテムを削除
			items.remove(callId);
		}
		// 指定の着信に応答
		MainService.LIB_OP.answerCall(call);
	}





	/**
	 * 着信を拒否
	 */
	public void rejectAll() {
		synchronized (items) {
			// 全着信を拒否
			for (String key : items.keySet()) {
				MainService.LIB_OP.rejectCall(items.get(key).call);
			}

			// 着信情報を初期化
			clearCallList();
		}
	}


	public void clearCallList() {
		items.clear();
	}



	/**
	 * 不審な電話番号を弾く
	 * 		幽霊着信対策
	 * @param telNum	着信番号
	 * @return			不審番号であればtrue
	 */
	public static boolean checkBugTelNummber(TelNumber telNum){

		if(telNum.getBaseString().length() == 10){//固定電話番号
//			Log.d("着信判定","固定電話番号");
			return false;
		}else if(telNum.getBaseString().length() == 11){//IP電話番号
//			Log.d("着信判定","IP電話番号");
			return false;
		}else if(telNum.getBaseString().equals("Anonymous")){//非通知 184~
//			Log.d("着信判定","非通知");
			return false;
		}else if(Pattern.compile("[2-4][0-9][0-9][0-9]").matcher(telNum.getBaseString()).matches()) {
//			2000-4999まで
//			Log.d("着信判定","BUZBIZ内線");
			return false;
		}else if(Pattern.compile("[1-9][0-9]").matcher(telNum.getBaseString()).matches()) {
			//10-99まで
//			Log.d("着信判定","ビジネスフォン内線");
			return false;
		}


		return true;

	}
}