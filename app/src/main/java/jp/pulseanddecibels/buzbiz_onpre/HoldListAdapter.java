package jp.pulseanddecibels.buzbiz_onpre;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 保留リスト用のリストアダプター
 * @author 普天間
 */
public class HoldListAdapter extends BaseAdapter {
	private Activity activity = null;				// コンテキスト
	private ArrayList<HoldListItem> data = null;	// 内容となる可変配列
	private int resource = 0;						// ビューのID





	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param data		リストの各項目になる可変配列
	 * @param resource	リストのビュー
	 */
	public HoldListAdapter(Context context, ArrayList<HoldListItem> data, int resource) {
//			Log.e("###   普天間ログ   ###","  HoldListAdapter.コンストラクタ  ");


		activity = (Activity)context;
		this.data = data;
		this.resource = resource;
	}





	/**
	 * リストのサイズを取得
	 */
	@Override
	public int getCount() {
//		Log.e("###   普天間ログ   ###","  HoldListAdapter.getCount  ");


		return data.size();
	}





	/**
	 * インでクスを削除し、指定位置のリストのデータを取得
	 */
	@Override
	public Object getItem(int position) {
//		Log.e("###   普天間ログ   ###","  HoldListAdapter.getItem  ");


		return data.get(position);
	}





	/**
	 * 指定位置のリストのIDを取得
	 */
	public String getCaller(int position) {
//		Log.e("###   普天間ログ   ###","  HoldListAdapter.getItemCaller  ");


		return data.get(position).getCaller();
	}





	public long getItemId(int position) {


		return data.get(position).getHoldListId();
	}





	/**
	 * 指定位置のリストを取得
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		android.util.Log.e("###   普天間ログ   ###", "  HoldListAdapter.getView  ");

		// 指定位置のリストのデータを取得
		HoldListItem item = (HoldListItem) getItem(position);


		// ビューを設定
		View view = convertView;
		if (view == null) {
			view = activity.getLayoutInflater().inflate(resource, null);
		}


		// 保留相手の電話番号を取得
		String callerName = item.getCallerName();
		// 取得に失敗した場合は、
		if(callerName == null || callerName.equals("null") || callerName.equals("")){
			// リストの表示部分には電話番号を設定
			String caller = item.getCaller();
			((TextView) view.findViewById(R.id.hold_list_caller_name)).setText(caller);
		// 取得に成功した場合は、
		}else{
			// リストの表示部分にお客様名を設定
			((TextView) view.findViewById(R.id.hold_list_caller_name)).setText(callerName);
		}


		// 対応者の内線番号を取得
		String responders = item.getResponders();
		// リストの表示部分に対応者名を設定
		((TextView) view.findViewById(R.id.hold_list_responders_name)).setText("対応者 " + responders);


		// 保留時間を表示
		int time = Integer.parseInt(item.getHoldTime());
		int tTimeSec = 0;
		int timeMin = 0;
		timeMin = time / 60;
		tTimeSec = time % 60;
		((TextView) view.findViewById(R.id.hold_list_timer)).setText(String.format("時間 %02d:%02d", timeMin, tTimeSec));


		// インフレートしたビューを返す
		return view;
	}
}