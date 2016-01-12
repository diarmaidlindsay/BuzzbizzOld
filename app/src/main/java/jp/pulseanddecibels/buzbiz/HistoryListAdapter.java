package jp.pulseanddecibels.buzbiz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * 履歴リストアダプター
 *
 *
 */
public class HistoryListAdapter extends BaseAdapter {
	private Activity activity 				= null;		// コンテキスト
	private ArrayList<HistoryListItem> data = null;		// 内容となる可変配列
	private int resource 					= 0;		// ビューのID





	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param data		リストの各項目になる可変配列
	 * @param resource	リストのビュー
	 */
	public HistoryListAdapter(Context context, ArrayList<HistoryListItem> data, int resource) {
//			Log.e(Util.LOG_TAG,"  HistoryListAdapter.コンストラクタ  ");


		this.activity 	= (Activity)context;
		this.data 		= data;
		this.resource 	= resource;
	}





	/**
	 * リストのサイズを取得
	 */
	@Override
	public int getCount() {
//			Log.e(Util.LOG_TAG,"  HistoryListAdapter.getCount  ");


		return data.size();
	}





	/**
	 * インでクスを削除し、指定位置のリストのデータを取得
	 */
	@Override
	public Object getItem(int position) {
//			Log.e(Util.LOG_TAG,"  HistoryListAdapter.getItem  ");


		return data.get(position);
	}





	/**
	 * 指定位置のリストのIDを取得
	 */
	public long getItemId(int position) {
//			Log.e(Util.LOG_TAG,"  HistoryListAdapter.getItemId  ");


		return data.get(position).getId();
	}





	/**
	 * 指定位置のリストを取得
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//			Log.e(Util.LOG_TAG,"  HistoryListAdapter.getView  ");


		// 指定位置のリストのデータを取得
		HistoryListItem item = (HistoryListItem) getItem(position);


		// ビューを設定
		View view = convertView;
		if (view == null) {
			view = activity.getLayoutInflater().inflate(resource, null);
		}


		// 発信元を設定
		TextView sourceNameTextView =(TextView) view.findViewById(jp.pulseanddecibels.buzbiz.R.id.history_list_source_name);
		sourceNameTextView.setText(item.getDisplayName());


		// 履歴の時間を設定
		TextView timeTextView = (TextView) view.findViewById(jp.pulseanddecibels.buzbiz.R.id.history_time);
		timeTextView.setText(item.getDisplayTime());


		// 着信に応答したかで文字の色を変える
		// 赤	：	応答なし
		// 黒	：	通話
		if(item.isAnswerFlag()){
			sourceNameTextView	.setTextColor(Color.BLACK);
		}else{
			sourceNameTextView	.setTextColor(Color.RED);
		}


		// 着信か発信でマークを変える
		ImageView mark = (ImageView) view.findViewById(jp.pulseanddecibels.buzbiz.R.id.iv_in_or_out_mark);
		if (item.isInStatus()) {
			mark.setImageDrawable(activity.getResources().getDrawable(jp.pulseanddecibels.buzbiz.R.drawable.in_mark));
		} else if (item.isOutStatus()) {
			mark.setImageDrawable(activity.getResources().getDrawable(jp.pulseanddecibels.buzbiz.R.drawable.out_mark));
		} else {
			mark.setVisibility(View.GONE);
		}


		// インフレートしたビューを返す
		return view;
	}
}