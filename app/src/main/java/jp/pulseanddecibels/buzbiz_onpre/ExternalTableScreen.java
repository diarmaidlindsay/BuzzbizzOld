package jp.pulseanddecibels.buzbiz_onpre;

import java.util.ArrayList;

import jp.pulseanddecibels.buzbiz_onpre.data.CallInfo;
import jp.pulseanddecibels.buzbiz_onpre.data.TelNumber;
import jp.pulseanddecibels.buzbiz_onpre.models.JsonParser;
import jp.pulseanddecibels.buzbiz_onpre.models.VolleyOperator;
import jp.pulseanddecibels.buzbiz_onpre.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;





/**
 *
 * 外線表画面の制御クラス
 *
 * @author 普天間
 *
 */
public class ExternalTableScreen {

//	private final MyFrameLayout myFrameLayout;	// 親のレイアウト
	private final Context context;				// 親のコンテキスト


	private final EditText externalSerchEdittext;				// 検索ボックス
	private final ListView externalList;						// 外線帳リスト


	private ExternalTelListAdapter baseAdapter;					// 外線帳リストのアダプター
	private ArrayList<ExternalTelListItem> resultArray;			// 外線帳リストのアダプター作成用可変配列
	private ExternalTelListAdapter filteredAdapter;				// フィルタリング後の外線帳リストのアダプター
	private ArrayList<ExternalTelListItem> filteredArrayList;	// フィルタリング後の外線帳リストのアダプター作成用可変配列


	private boolean filteringFlag = false;	// リストがフィルタリングされているか確認する為のフラグ





	/**
	 * コンストラクタ
	 * @param context メインコンテクスト
	 * @param myFrameLayout 親のレイアウト
	 */
	public ExternalTableScreen(Context context, MyFrameLayout myFrameLayout) {
//		Log.d(Util.LOG_TAG,"  ExternalTableScreen.コンストラクタ  ");


//		this.myFrameLayout = myFrameLayout;
		this.context = context;


		// 外線帳リストを設定
		externalList = (ListView)myFrameLayout.findViewById(R.id.external_table_list);
		externalList.setOnItemClickListener(externalItemClickListener);
		externalList.setOnScrollListener(externalLisScrollListener);


		// 検索ボックスを設定
		externalSerchEdittext = (EditText) myFrameLayout.findViewById(R.id.external_serch_edittext);
		externalSerchEdittext.addTextChangedListener(serchTextWatcher);
	}





	/**
	 * 検索ボックス用ウォッチャー
	 */
	private final TextWatcher serchTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		// 入力値に変更があった場合は、
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// 何も入力されていなければ、フィルターをクリアー
			if(TextUtils.isEmpty(s)){
				if(baseAdapter != null){
					filteringFlag = false;
					if(filteredAdapter != null){
						for (int i = 0; i < filteredArrayList.size(); i++) {
							baseAdapter.getItemWithIndexOn(i);
						}
					}

					externalList.setAdapter(baseAdapter);
				}

				return;
			}


			MyFrameLayout.hideTabButton();


			if(resultArray == null){
				return;
			}


			filteringFlag = true;

			// フィルタリング後のリスト用可変配列
			filteredArrayList = new ArrayList<ExternalTelListItem>();

			// ベースのアダプターアイテム数分実行、
			for (int i = 0; i < resultArray.size(); i++) {
				// 入力値がお客様名に含まれている場合は、フィルタリング後のリストに追加
				if(baseAdapter.getCustomerName(i) != null && baseAdapter.getCustomerName(i).contains(s)){
					filteredArrayList.add((ExternalTelListItem) baseAdapter.getItemWithIndexOff(i));
					continue;
				}

				// 入力値がお客様名（かな）に含まれている場合は、フィルタリング後のリストに追加
				if(baseAdapter.getCustomerNameKana(i) != null && baseAdapter.getCustomerNameKana(i).contains(s)){
					filteredArrayList.add((ExternalTelListItem) baseAdapter.getItemWithIndexOff(i));
					continue;
				}

				// 入力値がお客様電話番号に含まれている場合は、フィルタリング後のリストに追加
				if(baseAdapter.getTel(i) != null && baseAdapter.getTel(i).contains(s)){
					filteredArrayList.add((ExternalTelListItem) baseAdapter.getItemWithIndexOff(i));
					continue;
				}
			}

			// フィルタリング後のリスト用可変配列よりアダプターを作成し、外線帳リストにセット
			filteredAdapter = new ExternalTelListAdapter(ExternalTableScreen.this.context, filteredArrayList, R.layout.tel_list_item);
			externalList.setAdapter(filteredAdapter);
		}
	};





	/**
	 * 外線帳リストのアイテム選択時の処理
	 */
	private final OnItemClickListener externalItemClickListener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			Log.e(Util.LOG_TAG,"  ExternalTableScreen.externalItemClickListener  ");


			MyFrameLayout.hideTabButton();


			// コールできるかチェック
			if(MainService.isEnableCall() == false){
				return;
			}


            // クリックされたアイテムを取得
			ExternalTelListItem item = (ExternalTelListItem)((ListView) parent).getItemAtPosition(position);
			// 電話番号の取得
			CallInfo.INSTANCE.displayCallerNumber = item.getTel();
			final TelNumber telNum = new TelNumber(item.getTel());
			// お客様名を取得
			CallInfo.INSTANCE.displayCallerName = item.getCustomerName();


			// 電話番号が設定されていない時は、
			if (telNum.isEmpty()) {
				// メッセージを表示
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("外線")
					   .setIcon(R.drawable.buzbiz_icon)
					   .setMessage("選択された項目には、電話番号が設定されておりません。")
					   .setPositiveButton("OK", null)
					   .show();

				// 終了
				return;
			}


			// ダイアログのyesボタン押下時の処理
			DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 架電を実施
					MainService.LIB_OP.startCall(telNum);

					// 通話中画面へ設定
					MainActivity.me.firstScreen.kaypadScreen.setCallScreen();
					MainActivity.me.firstScreen.setKaypadScreenWhenStartCall();
				}
			};


			// 架電するか確認する為、ダイアログを表示
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("外線")
				   .setMessage("電話を掛けますか？\n\n"+ item.getCustomerName() +"\n" + item.getTel())
				   .setIcon(R.drawable.buzbiz_icon)
				   .setPositiveButton("はい", yes)
				   .setNeutralButton("いいえ", null)
				   .show();
		}
	};





	/**
	 * 外線帳リストのスクロール時の処理
	 */
	private final OnScrollListener externalLisScrollListener = new OnScrollListener(){
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == 1){		// 1 = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
//				Log.e(Util.LOG_TAG,"  ExternalTableScreen.externalLisScrollListener.onScrollStateChanged  ");
				MyFrameLayout.hideTabButton();
			}
		}
	};





	/**
	 * 選択された文字の先頭まで、リストを移動させる
	 * @param selectedChar 選択された文字
	 */
	public void moveList(char selectedChar){
//		Log.e(Util.LOG_TAG,"  ExternalTableScreen.moveList  ");


		// 選択文字がアルファベットか確認
		boolean isAlphabet = false;
		if(selectedChar >= 'a' && selectedChar <= 'z'){
			isAlphabet = true;
		}


		// フィルタリングされている場合は、
		if(filteringFlag){
			if(filteredArrayList == null)	{	return;		}

			// 選択文字の列までリストを移動
			for(int i = 0; i < filteredArrayList.size(); i++){
				if(selectedChar <= filteredAdapter.getTopCharOfCustomerNameKana(i) &&
				   isAlphabet == (filteredAdapter.getCharType(i) == 1)){
					externalList.setSelection(i);
					return;
				}
			}

			// 無い場合は最終に移動
			externalList.setSelection(filteredArrayList.size()-1);


		// フィルタリングされていない場合は、
		}else{
			if(resultArray == null)	{	return;		}

			// 選択文字の列までリストを移動
			for(int i = 0; i < resultArray.size(); i++){
				if(selectedChar <= baseAdapter.getTopCharOfCustomerNameKana(i) &&
				   isAlphabet == (baseAdapter.getCharType(i) == 1)){
					externalList.setSelection(i);
					return;
				}
			}

			// 無い場合は最終に移動
			externalList.setSelection(resultArray.size()-1);
		}
	}





	/**
	 * 外線帳リストの初期化
	 */
	public void initExternalTable(){
//		Log.e(Util.LOG_TAG,"  ExternalTableScreen.initExternalTable  ");


		// フラグを初期化
		filteringFlag = false;
		// 外線帳リストを初期化
		externalList.setAdapter(null);
		// 検索ボックスを初期化
		externalSerchEdittext.setText(Util.STRING_EMPTY);
	}






	/**
	 * 外線帳リストをダウンロード
	 */
	public void downloadExternalTable() {
        // ログインしていない場合は、何もしない
        if (!MainService.LIB_OP.isLogined()) {
            return;
        }

        // 通信開始
        VolleyOperator.downloadExternalTable(context, okDownloadExternalTable, null);
    }





    /**
     * 外線帳リストをダウンロードできた場合の処理
     */
    private final Response.Listener okDownloadExternalTable = new Response.Listener() {
        private final JsonParser jsonParser = new JsonParser();

        @Override
        public void onResponse(final Object response) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<ExternalTelListItem> parcedArray;
                    try{
                        String json = response.toString();
                        parcedArray = jsonParser.parceJsonForExternalTable(json);
                    }catch (Exception ex){
                        return;
                    }

                    // 取得した可変配列を電話帳用にフォーマット
                    resultArray = formatOfTelListArray(parcedArray);

                    // 少し遅らせUIに反映
                    MainActivity.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 本アクティビティーが生きている場合は、
                            if (((Activity) context).isFinishing() == false) {
                                // アダプターを作成し、外線帳リストにセット
                                baseAdapter = new ExternalTelListAdapter(context, resultArray, R.layout.tel_list_item);
                                externalList.setAdapter(baseAdapter);
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
	public ArrayList<ExternalTelListItem> formatOfTelListArray(ArrayList<ExternalTelListItem> tmpArray){
//		Log.e(Util.LOG_TAG,"  ExternalTableScreen.formatOfTelListArray  ");


		ArrayList<ExternalTelListItem> formatArray		= new ArrayList<ExternalTelListItem>();	// 先頭文字がアルファベットのアイテム用可変配列
		ArrayList<ExternalTelListItem> alphabetArray	= new ArrayList<ExternalTelListItem>();	// フォーマット済み可変配列
		ArrayList<ExternalTelListItem> symbolArray		= new ArrayList<ExternalTelListItem>();	// 先頭文字がアルファベット・ひらがな以外のアイテム用可変配列


		char currentAlphabetIndex	= '\uffff';		// アルファベット用の現在までに設定されたインデックス
		char currentKanaIndex		= '\uffff';		// ひらがな用の現在までに設定されたインデックス


		// 変更する可変配列を走査
		for(int i = 0; i < tmpArray.size(); i++){
			// i番目のデータを可変配列より取得
			ExternalTelListItem item = tmpArray.get(i);


			// 先頭文字がアルファベットの場合は、
			if(item.getCharType() == 1){
				// インデックスを設定する必要があるかを判断する
				char c = checkAlphabetIndex(item, currentAlphabetIndex);
				// 戻り値が初期値以外の場合は、
				if(c != '\uffff'){
					// 現在までに設定されたインデックスを更新
					currentAlphabetIndex = c;
					// データにインデックスを設定
					item.setIndex(String.valueOf(c));
				}
				// 一旦、先頭文字がアルファベットのアイテム用可変配列に追加
				alphabetArray.add(item);



			// 先頭文字がひらがなの場合は、
			}else if(item.getCharType() == 0){
				// インデックスを設定する必要があるかを判断する
				char c = checkKanaIndex(item, currentKanaIndex);
				// 戻り値が初期値以外の場合は、
				if(c != '\uffff'){
					// 現在までに設定されたインデックスを更新
					currentKanaIndex = c;
					// データにインデックスを設定
					item.setIndex(String.valueOf(c));
				}
				// フォーマット済み可変配列に追加
				formatArray.add(item);


			// 先頭文字が上記以外は、
			}else{
				// 先頭文字がアルファベット・ひらがな以外のアイテム用可変配列に追加
				symbolArray.add(item);
			}
		}


		// 先頭文字がアルファベットのアイテム用可変配列を、フォーマット済み可変配列に付け加える
		for(int i = 0; i < alphabetArray.size(); i++){
			formatArray.add(alphabetArray.get(i));
		}

		// 先頭文字がアルファベット・ひらがな以外のアイテム用可変配列を、フォーマット済み可変配列に付け加える
		for(int i = 0; i < symbolArray.size(); i++){
			formatArray.add(symbolArray.get(i));
		}

		return formatArray;
	}





	/**
	 * インデックスを設定する必要があるかを判断する
	 * @param item			インデックスを設定するアイテム
	 * @param currentChar	現在までに設定されたインデックス
	 * @return				設定したインデックス文字、またはインデックスをつける必要がない場合は初期値
	 */
	public char checkAlphabetIndex(ExternalTelListItem item, char currentChar){
//		Log.e(Util.LOG_TAG,"  ExternalTableScreen.setAlphabetIndex  ");


		// 先頭文字を取得
		char tmpChar = item.getTopCharOfCustomerNameKana();


		// 新たにインデックスをつける必要がない場合は初期値を返す
		if(currentChar != '\uffff'){
			if		(currentChar == 'a' && tmpChar <= 'c')	{	return '\uffff';	}
			else if	(currentChar == 'd' && tmpChar <= 'f')	{	return '\uffff';	}
			else if	(currentChar == 'g' && tmpChar <= 'i')	{	return '\uffff';	}
			else if	(currentChar == 'j' && tmpChar <= 'l')	{	return '\uffff';	}
			else if	(currentChar == 'm' && tmpChar <= 'o')	{	return '\uffff';	}
			else if	(currentChar == 'p' && tmpChar <= 's')	{	return '\uffff';	}
			else if	(currentChar == 't' && tmpChar <= 'y')	{	return '\uffff';	}
			else if	(currentChar == 'z')					{	return '\uffff';	}
		}


		// インデックスを設定
		if		(tmpChar >= 'a' && tmpChar <= 'c')	{	item.setTopCharOfCustomerNameKana('a');		return 'a';		}
		else if	(tmpChar >= 'd' && tmpChar <= 'f')	{	item.setTopCharOfCustomerNameKana('d');		return 'd';		}
		else if	(tmpChar >= 'g' && tmpChar <= 'i')	{	item.setTopCharOfCustomerNameKana('g');		return 'g';		}
		else if	(tmpChar >= 'j' && tmpChar <= 'l')	{	item.setTopCharOfCustomerNameKana('j');		return 'j';		}
		else if	(tmpChar >= 'm' && tmpChar <= 'o')	{	item.setTopCharOfCustomerNameKana('m');		return 'm';		}
		else if	(tmpChar >= 'p' && tmpChar <= 's')	{	item.setTopCharOfCustomerNameKana('p');		return 'p';		}
		else if	(tmpChar >= 't' && tmpChar <= 'y')	{	item.setTopCharOfCustomerNameKana('t');		return 't';		}
		else if	(tmpChar >= 'z')					{	item.setTopCharOfCustomerNameKana('z');		return 'z';		}


		return '\uffff';
	}





	/**
	 * インデックスを設定する必要があるかを判断する
	 * @param item			インデックスを設定するアイテム
	 * @param currentChar	現在までに設定されたインデックス
	 * @return				設定したインデックス文字、またはインデックスをつける必要がない場合は初期値
	 */
	public char checkKanaIndex(ExternalTelListItem item, char currentChar){
//		Log.e(Util.LOG_TAG,"  ExternalTableScreen.checkKanaIndex  ");


		// 先頭文字を取得
		char tmpChar = item.getTopCharOfCustomerNameKana();


		// 新たにインデックスをつける必要がない場合は初期値を返す
		if(currentChar != '\uffff'){
			if		(currentChar == 'あ' && tmpChar <= 'お')	{	return '\uffff';	}
			else if	(currentChar == 'か' && tmpChar <= 'こ')	{	return '\uffff';	}
			else if	(currentChar == 'さ' && tmpChar <= 'そ')	{	return '\uffff';	}
			else if	(currentChar == 'た' && tmpChar <= 'と')	{	return '\uffff';	}
			else if	(currentChar == 'な' && tmpChar <= 'の')	{	return '\uffff';	}
			else if	(currentChar == 'は' && tmpChar <= 'ほ')	{	return '\uffff';	}
			else if	(currentChar == 'ま' && tmpChar <= 'も')	{	return '\uffff';	}
			else if	(currentChar == 'や' && tmpChar <= 'よ')	{	return '\uffff';	}
			else if	(currentChar == 'ら' && tmpChar <= 'ろ')	{	return '\uffff';	}
			else if	(currentChar == 'わ')					{	return '\uffff';	}
		}


		// インデックスを設定
		if		(tmpChar >= 'あ' && tmpChar <= 'お')	{	item.setTopCharOfCustomerNameKana('あ');	return 'あ';	}
		else if	(tmpChar >= 'か' && tmpChar <= 'こ')	{	item.setTopCharOfCustomerNameKana('か');	return 'か';	}
		else if	(tmpChar >= 'さ' && tmpChar <= 'そ')	{	item.setTopCharOfCustomerNameKana('さ');	return 'さ';	}
		else if	(tmpChar >= 'た' && tmpChar <= 'と')	{	item.setTopCharOfCustomerNameKana('た');	return 'た';	}
		else if	(tmpChar >= 'な' && tmpChar <= 'の')	{	item.setTopCharOfCustomerNameKana('な');	return 'な';	}
		else if	(tmpChar >= 'は' && tmpChar <= 'ほ')	{	item.setTopCharOfCustomerNameKana('は');	return 'は';	}
		else if	(tmpChar >= 'ま' && tmpChar <= 'も')	{	item.setTopCharOfCustomerNameKana('ま');	return 'ま';	}
		else if	(tmpChar >= 'や' && tmpChar <= 'よ')	{	item.setTopCharOfCustomerNameKana('や');	return 'や';	}
		else if	(tmpChar >= 'ら' && tmpChar <= 'ろ')	{	item.setTopCharOfCustomerNameKana('ら');	return 'ら';	}
		else if	(tmpChar >= 'わ')					{	item.setTopCharOfCustomerNameKana('わ');	return 'わ';	}


		return '\uffff';
	}
}