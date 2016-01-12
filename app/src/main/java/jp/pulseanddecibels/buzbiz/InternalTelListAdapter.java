package jp.pulseanddecibels.buzbiz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 内線帳用のリストアダプター
 */
public class InternalTelListAdapter extends BaseAdapter {
	private Activity activity = null;						// コンテキスト
	private ArrayList<InternalTelListItem> data = null;		// 内容となる可変配列
	private int resource = 0;								// ビューのID





	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param data		リストの各項目になる可変配列
	 * @param resource	リストのビュー
	 */
	public InternalTelListAdapter(Context context, ArrayList<InternalTelListItem> data, int resource) {



		activity = (Activity)context;
		this.data = data;
		this.resource = resource;
	}





	/**
	 * リストのサイズを取得
	 */
	@Override
	public int getCount() {


		return data.size();
	}





	/**
	 * インでクスを削除し、指定位置のリストのデータを取得
	 */
	@Override
	public Object getItem(int position) {


		return data.get(position);
	}





	/**
	 * 指定位置のリストのデータを取得
	 */
	public Object getItemWithIndexOff(int position) {



		InternalTelListItem item = data.get(position);
		item.setFilteringFlag(true);
		return item;
	}





	/**
	 * 指定位置のリストのデータを取得
	 */
	public Object getItemWithIndexOn(int position) {


		InternalTelListItem item = data.get(position);
		item.setFilteringFlag(false);
		return item;
	}





	/**
	 * 指定位置のリストのIDを取得
	 */
	@Override
	public long getItemId(int position) {



		return data.get(position).getId();
	}





	/**
	 * 指定位置のリストの内線ユーザ名を取得
	 */
	public String getUserName(int position) {



		return data.get(position).getUserName();
	}





	/**
	 * 指定位置のリストの内線ユーザ名(かな)を取得
	 */
	public String getUserNameKana(int position) {


		return data.get(position).getUserNameKana();
	}





	/**
	 * 指定位置のリストを取得
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {



		// 指定位置のリストのデータを取得
		InternalTelListItem item = (InternalTelListItem) getItem(position);


		// ビューを設定
		View view = convertView;
	    if (view == null) {
	        view = activity.getLayoutInflater().inflate(resource, null);
	    }


		// リストの表示部分に内線ユーザ名を設定
		TextView userNameTextView =(TextView) view.findViewById(jp.pulseanddecibels.buzbiz.R.id.tel_list_name);
		userNameTextView.setText(item.getUserName());


		// 通話可能かにより背景を設定する、
		ViewGroup body = (ViewGroup)  view.findViewById(jp.pulseanddecibels.buzbiz.R.id.list_body_layout);
		if(item.getLoginStatus() == 1){
			body.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.image_list_cell);
		}else{
			body.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.image_list_not_use_cell);
		}


		// インデックスを取得
		String index = data.get(position).getIndex();
		// インデックスが設定されていない場合、またはフィルタリングされている場合は、ヘッダーを非表示に設定
		if(index == null || data.get(position).isFilteringFlag() ){
			view.findViewById(jp.pulseanddecibels.buzbiz.R.id.tel_list_index_layout).setVisibility(View.GONE);
		// インデックスが設定されている場合は、
		}else{
			// ヘッダーを表示に設定
			view.findViewById(jp.pulseanddecibels.buzbiz.R.id.tel_list_index_layout).setVisibility(View.VISIBLE);
			// インデックスをヘッダーに設定
			TextView title = (TextView) view.findViewById(jp.pulseanddecibels.buzbiz.R.id.tel_list_index_textview);
			title.setText(index);
		}


		// インフレートしたビューを返す
		return view;
	}
}