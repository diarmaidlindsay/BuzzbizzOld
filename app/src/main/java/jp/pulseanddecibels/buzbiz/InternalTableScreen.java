package jp.pulseanddecibels.buzbiz;

import java.util.ArrayList;

import jp.pulseanddecibels.buzbiz.models.JsonParser;
import jp.pulseanddecibels.buzbiz.models.VolleyOperator;
import jp.pulseanddecibels.buzbiz.util.Util;
import jp.pulseanddecibels.buzbiz.data.TelNumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;





/**
 * 内線表画面の設定クラス
 */
public class InternalTableScreen {


//	private final MyFrameLayout myFrameLayout;					// 親のレイアウト
	private final Context context;								// 親のコンテキスト


	private final EditText internalSerchEdittext;				// 検索ボックス
	private final ListView internalList;						// 内線帳リスト


	private InternalTelListAdapter baseAdapter;					// 内線帳リストのアダプター
	private ArrayList<InternalTelListItem> resultArray;			// 内線帳リストのアダプター作成用可変配列
	private InternalTelListAdapter filteredAdapter;				// フィルタリング後の内線帳リストのアダプター
	private ArrayList<InternalTelListItem> filteredArrayList;	// フィルタリング後の内線帳リストのアダプター作成用可変配列





	/**
	 * コンストラクタ
	 * @param context メインコンテクスト
	 * @param myFrameLayout 親のレイアウト
	 */
	public InternalTableScreen(Context context, MyFrameLayout myFrameLayout) {
//		Log.e(Util.LOG_TAG,"  InternalTableScreen.コンストラクタ  ");


//		// 親の参照を保存
//		this.myFrameLayout = myFrameLayout;
		this.context = context;


		// 内線帳リストを設定
		internalList = (ListView)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.internal_table_list);
		internalList.setOnItemClickListener(internalItemClickListener);
		internalList.setOnScrollListener(internalListScrollListener);


		// 検索ボックスを設定
		internalSerchEdittext = (EditText) myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.internal_serch_edittext);
		internalSerchEdittext.addTextChangedListener(serchTextWatcher);
	}





	/**
	 * 検索ボックスのウォッチャー
	 */
	private final TextWatcher serchTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		// 入力値に変更があった場合は、
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// 何も入力されていなければ、フィルターをクリアー
			if(s == null || (s.toString()).equals("")){
				if(baseAdapter != null){
					if(filteredAdapter != null){
						for (int i = 0; i < filteredArrayList.size(); i++) {
							baseAdapter.getItemWithIndexOn(i);
						}
					}

					internalList.setAdapter(baseAdapter);
				}

			// 入力値があれば、入力値でフィルタリング
			}else{
				MyFrameLayout.hideTabButton();


				if(resultArray != null){
					// フィルタリング後のリスト用可変配列
					filteredArrayList = new ArrayList<InternalTelListItem>();

					// ベースのアダプターアイテム数分実行、
					for (int i = 0; i < resultArray.size(); i++) {
						// 入力値がお客様名に含まれている場合は、フィルタリング後のリストに追加
						if(baseAdapter.getUserName(i) != null && baseAdapter.getUserName(i).contains(s)){
							filteredArrayList.add((InternalTelListItem) baseAdapter.getItemWithIndexOff(i));
							continue;
						}

						// 入力値がお客様名（かな）に含まれている場合は、フィルタリング後のリストに追加
						if(baseAdapter.getUserNameKana(i) != null && baseAdapter.getUserNameKana(i).contains(s)){
							filteredArrayList.add((InternalTelListItem) baseAdapter.getItemWithIndexOff(i));
						}
					}

					// フィルタリング後のリスト用可変配列よりアダプターを作成し、外線帳リストにセット
					filteredAdapter = new InternalTelListAdapter(InternalTableScreen.this.context, filteredArrayList, jp.pulseanddecibels.buzbiz.R.layout.tel_list_item);
					internalList.setAdapter(filteredAdapter);
				}
			}
		}
	};





	/**
	 * 内線帳リストのアイテム選択時の処理
	 */
	private final OnItemClickListener internalItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			Log.e(Util.LOG_TAG,"  InternalTableScreen.internalItemClickListener  ");


			MyFrameLayout.hideTabButton();


			// コールできるかチェック
			if(MainService.isEnableCall() == false){
				return;
			}


			// クリックされたアイテムを取得
			InternalTelListItem item = (InternalTelListItem)((ListView) parent).getItemAtPosition(position);


//			// 相手がログイン状態で無ければ何もしない
//			if(item.getLoginStatus() != 1){
//				return;
//			}


			// 内線番号の取得
			final TelNumber telNum =  new TelNumber(item.getSipId());
			// 内線ユーザ名を取得
			final String callerName = item.getUserName();



			// 電話番号が設定されていない時は、
			if(telNum.isEmpty()){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("内線")
					   .setMessage("選択された項目には、電話番号が設定されておりません。")
					   .setIcon(jp.pulseanddecibels.buzbiz.R.drawable.buzbiz_icon)
					   .setPositiveButton("OK", null)
					   .show();

				// 終了
				return;
			}


			// ダイアログのyesボタン押下時の処理
			DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 通話の情報を設定
					KaypadScreen.setCallInformation("内線", callerName);

					// 架電を実施
					MainService.LIB_OP.startCall(telNum);

					// 通話中画面へ設定
					MainActivity.me.firstScreen.kaypadScreen.setCallScreen();
					MainActivity.me.firstScreen.setKaypadScreenWhenStartCall();
				}
			};


			// 架電するか確認する為、ダイアログを表示
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("内線")
				   .setMessage("電話を掛けますか？\n\n" + item.getUserName() + " \n(内線)")
				   .setIcon(jp.pulseanddecibels.buzbiz.R.drawable.buzbiz_icon)
				   .setPositiveButton("はい", yes)
				   .setNeutralButton("いいえ", null)
				   .show();
		}
	};





	/**
	 * 内線帳リストのスクロール時の処理
	 */
	private final OnScrollListener internalListScrollListener = new OnScrollListener(){
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == 1){		// 1 = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
//				Log.e(Util.LOG_TAG,"  InternalTableScreen.internalListScrollListener.onScrollStateChanged  ");
				MyFrameLayout.hideTabButton();
			}
		}
	};





	/**
	 * 外線帳リストの初期化
	 */
	public void initInternalTable(){
//		Log.e(Util.LOG_TAG,"  InternalTableScreen.initInternalTable  ");


		// 外線帳リストを初期化
		internalList.setAdapter(null);
		// 検索ボックスを初期化
		internalSerchEdittext.setText(Util.STRING_EMPTY);
	}





	/**
	 * 内線帳リストをダウンロード
	 */
	public void downloadInternalTable(){
		// ログインしていない場合は、何もしない
        if (!MainService.LIB_OP.isLogined()) {
            return;
        }

        // 通信開始
        VolleyOperator.downloadInternalTable(context, okDownloadInternalTable, null);
	}





    /**
     * 内線帳リストをダウンロードできた場合の処理
     */
    private final Response.Listener okDownloadInternalTable = new Response.Listener() {
        private final JsonParser jsonParser = new JsonParser();

        @Override
        public void onResponse(final Object response) {
            new Thread(new Runnable() {
                @Override
                public void run() {
					ArrayList<InternalTelListItem> parcedArray;
                    try{
                        String json = response.toString();
                        parcedArray = jsonParser.parceJsonForInternalTable(json);
                    }catch (Exception ex){
                        return;
                    }

                    // 取得した可変配列を電話帳用にフォーマット
                    resultArray = formatOfTelListArray(parcedArray);

                    MainActivity.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 本アクティビティーが生きている場合は、
                            if(((Activity) context).isFinishing() == false){
                                // アダプターを作成し、外線帳リストにセット
                                baseAdapter = new InternalTelListAdapter(context, resultArray, jp.pulseanddecibels.buzbiz.R.layout.tel_list_item);
                                internalList.setAdapter(baseAdapter);
                            }
                        }
                    }, 300);
                }
            }).start();
        }
    };





	/**
	 * 可変配列を電話帳リスト向けにフォーマットする
	 * @param tmpArray	変更する可変配列
	 * @return			フォーマットされた可変配列
	 */
	public ArrayList<InternalTelListItem> formatOfTelListArray(ArrayList<InternalTelListItem> tmpArray){
//		Log.e(Util.LOG_TAG,"  InternalTableScreen.formatOfTelListArray  ");


		// 現在までに設定されたSIPグループのID
		String currentSipGroupId	= Util.STRING_EMPTY;
		// 一時格納用
		String tmpSipGroupId 		= Util.STRING_EMPTY;
		String tmpDepartmentName	= Util.STRING_EMPTY;


		// 変更する可変配列を走査
		for(int i = 0; i < tmpArray.size(); i++){
			// i番目のデータを可変配列より取得
			InternalTelListItem item = tmpArray.get(i);


			// リストの最初のアイテムはインデックスを作成
			if(i == 0){
				currentSipGroupId = item.getSipGroupId();
				tmpDepartmentName = item.getDepartmentName();

				if (TextUtils.isEmpty(tmpDepartmentName) || tmpDepartmentName.equals("null")) {
					item.setIndex(currentSipGroupId);
				} else {
					item.setIndex(tmpDepartmentName);
				}
			}


			// 現在のインデックスと違うグループの場合はインデックスを作成
			tmpSipGroupId = item.getSipGroupId();
			if(tmpSipGroupId.equals(currentSipGroupId) == false){
				currentSipGroupId = tmpSipGroupId;
				tmpDepartmentName = item.getDepartmentName();

				if (TextUtils.isEmpty(tmpDepartmentName) || tmpDepartmentName.equals("null")) {
					item.setIndex(currentSipGroupId);
				} else {
					item.setIndex(tmpDepartmentName);
				}
			}
		}

		return tmpArray;
	}
}