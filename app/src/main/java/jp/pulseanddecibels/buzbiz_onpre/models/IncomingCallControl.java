package jp.pulseanddecibels.buzbiz_onpre.models;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;

import jp.pulseanddecibels.buzbiz_onpre.IncomingCallItem;
import jp.pulseanddecibels.buzbiz_onpre.MainService;
import jp.pulseanddecibels.buzbiz_onpre.data.TelNumber;

/**
 *
 * 着信の制御
 *
 * @author 普天間
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
	public void anserTo(String callId) {
		synchronized (items) {
			// 指定の着信に応答
			MainService.LIB_OP.anserCall(callId);

			// 応答した着信アイテムを削除
			items.remove(callId);
		}
	}





	/**
	 * 着信を拒否
	 */
	public void rejectAll() {
		synchronized (items) {
			// 全着信を拒否
			for (String key : items.keySet()) {
				MainService.LIB_OP.rejectCall(key);
			}

			// 着信情報を初期化
			items.clear();
		}
	}





	/**
	 * 不審な電話番号を弾く
	 * 		現在、突然100より着信が有る為
	 * @param telNum	着信番号
	 * @return			不審番号であればtrue
	 */
	public static boolean checkBugTelNummber(TelNumber telNum){
		if ("100".equals(telNum.getBaseString())) {
			return true;
		} else {
			return false;
		}
	}
}