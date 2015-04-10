package jp.pulseanddecibels.buzbiz_onpre;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 *
 * 外線帳用のリストアダプター
 *
 * @author 普天間
 *
 */
public class ExternalTelListAdapter extends BaseAdapter {
	private Activity activity = null;						// コンテキスト
	private ArrayList<ExternalTelListItem> data = null;		// 内容となる可変配列
	private int resource = 0;								// ビューのID





	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param data		リストの各項目になる可変配列
	 * @param resource	リストのビュー
	 */
	public ExternalTelListAdapter(Context context, ArrayList<ExternalTelListItem> data, int resource) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.コンストラクタ  ");


		this.activity	= (Activity)context;
		this.data		= data;
		this.resource	= resource;
	}





	/**
	 * リストのサイズを取得
	 */
	@Override
	public int getCount() {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getCount  ");


		return data.size();
	}





	/**
	 * インでクスを削除し、指定位置のリストのデータを取得
	 */
	@Override
	public Object getItem(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getItem  ");


		return data.get(position);
	}





	/**
	 * 指定位置のリストのデータを取得し、フィルタリングフラグをオフ
	 */
	public Object getItemWithIndexOff(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getItem  ");


		ExternalTelListItem item = data.get(position);
		item.setFilteringFlag(true);
		return item;
	}





	/**
	 * 指定位置のリストのデータを取得し、フィルタリングフラグをオン
	 */
	public Object getItemWithIndexOn(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getItem  ");


		ExternalTelListItem item = data.get(position);
		item.setFilteringFlag(false);
		return item;
	}





	/**
	 * 指定位置のリストのIDを取得
	 */
	@Override
	public long getItemId(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getItemId  ");


		return data.get(position).getCustomerId();
	}





	/**
	 * 指定位置のリストのお客様名を取得
	 */
	public String getCustomerName(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getCustomerName  ");


		return data.get(position).getCustomerName();
	}





	/**
	 * 指定位置のリストのお客様名（かな）を取得
	 */
	public String getCustomerNameKana(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getCustomerNameKana  ");


		return data.get(position).getCustomerNameKana();
	}





	/**
	 * 指定位置のリストの電話番号を取得
	 */
	public String getTel(int position) {
//		Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getTel  ");


	return data.get(position).getTel();
}





	/**
	 * 指定位置のリストのお客様名（かな）の先頭文字を取得
	 */
	public char getTopCharOfCustomerNameKana(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getTopCharOfCustomerNameKana  ");


		return data.get(position).getTopCharOfCustomerNameKana();
	}





	/**
	 * 指定位置のリストのお客様名（かな）の先頭文字はアルファベットかどうかを確認
	 */
	public int getCharType(int position) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.isAlphabetMe  ");


		return data.get(position).getCharType();
	}





	/**
	 * 指定位置のリストを取得
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//			Log.e(Util.LOG_TAG,"  ExternalTelListAdapter.getView  ");


		// 指定位置のリストのデータを取得
		ExternalTelListItem item = (ExternalTelListItem) getItem(position);


		// ビューを設定
		View view = convertView;
		if (view == null) {
			view = activity.getLayoutInflater().inflate(resource, null);
		}


		// 背景を設定する、
		((ViewGroup) view.findViewById(R.id.list_body_layout)).setBackgroundResource(R.drawable.image_list_cell);


		// リストの表示部分にお客様名を設定
		((TextView) view.findViewById(R.id.tel_list_name)).setText(item.getCustomerName());


		// インデックスを取得
		String index = data.get(position).getIndex();
		// インデックスが設定されていない場合、またはフィルタリングされている場合は、ヘッダーを非表示に設定
		if(index == null || data.get(position).isFilteringFlag() ){
			view.findViewById(R.id.tel_list_index_layout).setVisibility(View.GONE);
		// インデックスが設定されている場合は、
		}else{
			// ヘッダーを表示に設定
			view.findViewById(R.id.tel_list_index_layout).setVisibility(View.VISIBLE);
			// インデックスをヘッダーに設定
			TextView title = (TextView) view.findViewById(R.id.tel_list_index_textview);
			title.setText(index);
		}


		// インフレートしたビューを返す
		return view;
	}
}