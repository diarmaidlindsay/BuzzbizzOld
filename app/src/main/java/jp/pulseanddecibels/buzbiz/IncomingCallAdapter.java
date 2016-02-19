package jp.pulseanddecibels.buzbiz;

import java.util.ArrayList;

import jp.pulseanddecibels.buzbiz.data.CallInfo;
import jp.pulseanddecibels.buzbiz.models.IncomingCallControl;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class IncomingCallAdapter extends BaseAdapter {
	private IncomingCallActivity activity = null;		// コンテキスト
	private ArrayList<IncomingCallItem> data = null;	// 内容となる可変配列
	private int resource = 0;							// ビューのID





	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param data		リストの各項目になる可変配列
	 * @param resource	リストのビュー
	 */
	public IncomingCallAdapter(Context context, ArrayList<IncomingCallItem> data, int resource) {
		activity		= (IncomingCallActivity)context;
		this.data		= data;
		this.resource	= resource;
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





	public long getItemId(int position) {
		return data.get(position).callId.hashCode();
	}






	/**
	 * 指定位置のリストを取得
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 指定位置のリストのデータを取得
		final IncomingCallItem item = (IncomingCallItem) getItem(position);


		// ビューを設定
		View view = convertView;
		if (view == null) {
			view = activity.getLayoutInflater().inflate(resource, null);
		}


		// 『応答』ボタンを設定
		final OnClickListener answer = new OnClickListener(){
			@Override
			public void onClick(View v) {
				// 通話情報を設定
				CallInfo.ClearData();
				CallInfo.INSTANCE.callerNumber = item.telNum.getBaseString();
				KeypadScreen.setCallInformation(item.telNum.getBaseString(), item.label);

				// キーパッド画面を通話中に設定
//				MainService.currentKeypadScreen = MainService.CALLING_NO_KAYPUD;
				MainService.currentKeypadScreen = MainService.KeyPadStates.CALLING_NO_KAYPUD;

				// メイン画面に戻る
				activity.returnMainActivity();

				// 応答
				IncomingCallControl.INSTANCE.answerTo(item.callId, item.call);
			}
		};
		((ImageButton) view.findViewById(jp.pulseanddecibels.buzbiz.R.id.answer_button)).setOnClickListener(answer);


		// 表示名を設定
		((TextView) view.findViewById(jp.pulseanddecibels.buzbiz.R.id.incoming_call_label)).setText(item.getDisplayInfo());

		// インフレートしたビューを返す
		return view;
	}
}