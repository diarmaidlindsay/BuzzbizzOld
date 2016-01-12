package jp.pulseanddecibels.buzbiz;

import java.util.ArrayList;

import jp.pulseanddecibels.buzbiz.data.TelNumber;
import jp.pulseanddecibels.buzbiz.models.JsonParser;
import jp.pulseanddecibels.buzbiz.models.VolleyOperator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;





/**
 * 履歴画面の制御クラス
 *
 */
public class HistoryScreen {

	/** 親のコンテクスト */
	private final Context context;





	/**
	 * コンストラクタ
	 */
	public HistoryScreen(Context context, MyFrameLayout myFrameLayout) {
		this.context = context;

		// 履歴リストを設定
		ListView historyList = (ListView)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.history_list);
		historyList.setOnItemClickListener(historyListItemClickListener);
		historyList.setOnScrollListener(historyListScrollListener);
	}





	/**
	 * 履歴リストのアイテム選択時の処理
	 */
	private final OnItemClickListener historyListItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// タブを隠す
			MyFrameLayout.hideTabButton();

			// コールできるかチェック
			if(MainService.isEnableCall() == false){
				return;
			}

            // クリックされたアイテムを取得
			ListView listView = (ListView) parent;
			HistoryListItem item = (HistoryListItem) listView.getItemAtPosition(position);

			// 電話番号番号の取得
			final TelNumber telNum = new TelNumber(item.getTelNum());
			// 架電相手の名前を取得
			final String callerName = item.getName();

			// 電話番号番号が取得できていないものは何もしない
			if (telNum.isEmpty()) {
				return;
			}


			DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 通話の情報を設定
					if(telNum.isExternal()){
						KaypadScreen.setCallInformation(telNum.getBaseString(), callerName);
					}else {
						KaypadScreen.setCallInformation("内線", callerName);
					}

					// 架電を実施
					MainService.LIB_OP.startCall(telNum);

					// 通話中画面へ設定
					MainActivity.me.firstScreen.kaypadScreen.setCallScreen();
					MainActivity.me.firstScreen.setKaypadScreenWhenStartCall();
				}
			};


			// 架電するか確認する為、ダイアログを表示
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(telNum.getLineTypeString())
				   .setMessage("電話を掛けますか？")
				   .setIcon(jp.pulseanddecibels.buzbiz.R.drawable.buzbiz_icon)
				   .setPositiveButton("はい",   yes)
				   .setNeutralButton ("いいえ", null)
				   .show();
		}
	};





	/**
	 * 履歴リストのスクロール時の処理
	 */
	private final OnScrollListener historyListScrollListener = new OnScrollListener(){
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == 1){		// 1 = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
				MyFrameLayout.hideTabButton();
			}
		}
	};





	/**
	 * 履歴リストをダウンロード
	 */
	public void downloadHistoryList(){
        // ログインしていない場合は、何もしない
        if (!MainService.LIB_OP.isLogined()) {
            return;
        }

        // 通信開始
        VolleyOperator.downloadHistoryList(context, okDownloadHistoryList, null);
	}





    /**
     * 外線帳リストをダウンロードできた場合の処理
     */
    private final Response.Listener okDownloadHistoryList = new Response.Listener() {
        private final JsonParser jsonParser = new JsonParser();

        @Override
        public void onResponse(final Object response) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<HistoryListItem> parcedArray;
                    try{
                        String json = response.toString();
                        parcedArray = jsonParser.parceJsonForHistoryList(json);
                    }catch (Exception ex){
                        return;
                    }

                    // 少し遅らせUIに反映
                    MainActivity.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 本アクティビティーが生きている場合は、
                            if (((Activity) context).isFinishing() == false) {
                                // アダプターを作成し、外線帳リストにセット
                                HistoryListAdapter baseAdapter = new HistoryListAdapter(context, parcedArray, jp.pulseanddecibels.buzbiz.R.layout.history_list_item);
                                ListView listView = (ListView) ((Activity) context).findViewById(jp.pulseanddecibels.buzbiz.R.id.history_list);
                                listView.setAdapter(baseAdapter);
                            }
                        }
                    }, 300);
                }
            }).start();
        }
    };
}