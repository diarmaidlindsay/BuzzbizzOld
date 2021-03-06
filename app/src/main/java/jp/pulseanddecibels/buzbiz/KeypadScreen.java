package jp.pulseanddecibels.buzbiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Response;

import jp.pulseanddecibels.buzbiz.data.CallInfo;
import jp.pulseanddecibels.buzbiz.data.DtmfCode;
import jp.pulseanddecibels.buzbiz.data.TelNumber;
import jp.pulseanddecibels.buzbiz.models.CallTimer;
import jp.pulseanddecibels.buzbiz.models.DTMF;
import jp.pulseanddecibels.buzbiz.models.JsonParser;
import jp.pulseanddecibels.buzbiz.models.VolleyOperator;
import jp.pulseanddecibels.buzbiz.util.Util;





/**
 *
 * キーパッド画面
 *
 *
 */
public class KeypadScreen {

	private final TextView inputTelNumbers;				// 電話番号入力欄
	private final TextView displayTelNumbers;			// 電話番号入力欄(通話中 / 簡易情報)
	private final ViewGroup displayCallerImfoLayout;	// 電話番号入力欄(通話中 / 詳細情報)
	private final TextView displayCallerName;			// 電話番号入力欄(通話中 / 詳細情報)の通話相手の名前
	private final TextView displayCallerTimer;			// 電話番号入力欄(通話中 / 詳細情報)の通話相手のタイマー


	// 画面上部
	private final ViewGroup withKaypatTop;
	private final ViewGroup hideKaypadTop;
	private final ViewGroup beforeCallTopItem;
	private final ViewGroup callingKaypadTopItem;
	private final ViewGroup Kaypad;
	// 画面下部
	private final Button beforeCallUnder;
	private final ImageButton moveTelListButtonUnder;
	private final ViewGroup callingUnder;


	// 各トグルボタン
	private final ToggleButton muteButton;
	private final ToggleButton topMuteButton;
	private final ToggleButton speakerButton;
	private final ToggleButton topSpeakerButton;

	private final MyFrameLayout myFrameLayout;	// 親のレイアウト
	private final Context context;				// 親のコンテキスト
	private static final String LOG_TAG = KeypadScreen.class.getSimpleName();



	/**
	 * コンストラクタ
	 */
	public KeypadScreen(Context context, MyFrameLayout myFrameLayout) {
//		Log.e(Util.LOG_TAG,"  キーパッド画面.コンストラクタ  ");


		this.myFrameLayout = myFrameLayout;
		this.context = context;


		// 電話番号入力欄エディットテキストを設定
		inputTelNumbers			= (TextView) myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.tel_numbers_input_textview);
		// 通話中の表示領域を取得
		displayTelNumbers 		= (TextView)	myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.tel_numbers_display_textview);
		displayCallerImfoLayout = (ViewGroup)	myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.display_caller_imfo_layout);
		displayCallerName 		= (TextView)	myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.display_caller_name_textview);
		displayCallerTimer		= (TextView)	myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.display_caller_timer);


		// 各キーパッドのボタンを設定
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_one_button)		.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_two_button)		.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_three_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_four_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_five_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_six_button)		.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_seven_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_eight_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_nine_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_zero_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_kome_button)	.setOnClickListener(keypadClickListener);
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_sharp_button)	.setOnClickListener(keypadClickListener);


		// バックスペースボタンの設定
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.tel_num_backspace_button).setOnClickListener(backspaceClickListener);


		// 画面を設定
		withKaypatTop 			= (ViewGroup)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.with_kaypad_top_layout);
		hideKaypadTop 			= (ViewGroup)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.hide_kaypad_top_layout);
		beforeCallTopItem 		= (ViewGroup)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.before_call_top_item);
		callingKaypadTopItem	= (ViewGroup)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_kaypad_top_item);
		Kaypad					= (ViewGroup)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_kaypad);
		callingUnder			= (ViewGroup)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_under);


		// 架電ボタンの設定
		beforeCallUnder			= (Button)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_call_button);
		beforeCallUnder.setOnClickListener(callClickListener);

		// 電話帳参照ボタン
		moveTelListButtonUnder	= (ImageButton)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.btn_move_tel_list);
		moveTelListButtonUnder.setOnClickListener(moveTelListButtonClickListener);

		// 各設定ボタンの設定
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.display_kaypad_button).setOnClickListener(displayKaypadClickListener);
		muteButton = (ToggleButton)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.middle_mute_button);
		muteButton.setOnCheckedChangeListener(muteCheckedChangeListener);
		topMuteButton = (ToggleButton)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.top_mute_button);
		topMuteButton.setOnCheckedChangeListener(muteCheckedChangeListener);
		speakerButton = (ToggleButton)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.middle_speaker_button);
		speakerButton.setOnCheckedChangeListener(speakerCheckedChangeListener);
		topSpeakerButton = (ToggleButton)myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.top_speaker_button);
		topSpeakerButton.setOnCheckedChangeListener(speakerCheckedChangeListener);


		// 保留ボタンの設定
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_hold_button).setOnClickListener(callingHoldClickListener);


		// 切断ボタンの設定
		myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_hangup_button).setOnClickListener(callingHungUpClickListener);


		// 画面を現在の状態に
		if(MainService.currentKeypadScreen == MainService.KeyPadStates.NOMAL){
//			if(MainService.currentKeypadScreen == MainService.NOMAL){
			initText();
			setNormalScreen();
//		}else if(MainService.currentKeypadScreen == MainService.CALLING_NO_KAYPUD){
		}else if(MainService.currentKeypadScreen == MainService.KeyPadStates.CALLING_NO_KAYPUD){
			KeypadScreen.startUsingUiMuteAndSpeakerButton();
			setCallScreen();
//		}else if(MainService.currentKeypadScreen == MainService.CALLING_KAYPUD){
		}else if(MainService.currentKeypadScreen == MainService.KeyPadStates.CALLING_KAYPAD){
			KeypadScreen.startUsingUiMuteAndSpeakerButton();
			setCallScreenWithKaypad();
		}
	}


	/**
	 * 各テキストを初期化
	 */
	private void initText(){
		CallInfo.INSTANCE.inputTelNum = Util.STRING_EMPTY;
		inputTelNumbers		.setText(Util.STRING_EMPTY);
		displayTelNumbers	.setText(Util.STRING_EMPTY);
		displayCallerName	.setText(Util.STRING_EMPTY);
	}



	/**
	 * 入力電話番号を設定する
	 *
	 * @param telNum 入力電話番号
	 */
	public void setInputNumber(String telNum) {
		inputTelNumbers.setText(telNum);
		CallInfo.INSTANCE.inputTelNum = telNum;
	}





	/**
	 * キーパッドのボタン押下時の処理
	 */
	private final OnClickListener keypadClickListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
//			Log.e(Util.LOG_TAG,"  キーパッド画面.keypadClickListener  ");


			// -----------------  ノーマル画面の場合は、 -----------------
//			if(MainService.currentKeypadScreen == MainService.NOMAL){
			if(MainService.currentKeypadScreen == MainService.KeyPadStates.NOMAL){
				MyFrameLayout.hideTabButton();

				// 現在までの入力値を取得
				String str = inputTelNumbers.getText().toString();
				// 現在までの入力値に、押下されたボタンの数を付け加える
				str += view.getTag().toString();
				inputTelNumbers.setText(str);

				// 入力されて電話番号を更新
				CallInfo.INSTANCE.inputTelNum = str;

				return;
			}

			// -----------------  通話中画面の場合は、 -----------------
			// 現在までの入力値を取得
			String str = displayTelNumbers.getText().toString();
			// 現在までの入力値に、押下されたボタンの数を付け加える
			str += view.getTag().toString();
			displayTelNumbers.setText(str);


			// 通話中で内線でなければ、DTMF音を相手に送る
//			if (CallInfo.INSTANCE.displayCallerNumber != null &&
//				CallInfo.INSTANCE.displayCallerNumber.equals("内線") == false) {
			if (CallInfo.INSTANCE.displayCallerNumber != null) {
				DtmfCode dtmf = DtmfCode.chengeToDtmfCode(view.getTag().toString());
				try {
					MainService.LIB_OP.sendDtmf(dtmf);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

//			}
		}
	};





	/**
	 * バックスペースボタン押下時の処理
	 */
	private final OnClickListener backspaceClickListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
//			Log.e(Util.LOG_TAG,"  キーパッド画面.backspaceClickListener  ");


			MyFrameLayout.hideTabButton();


			// 現在までの入力値を取得
			String input = inputTelNumbers.getText().toString();
			// 現在までの入力値が1文字でもあれば、1文字削除
			if (input.length() > 0){
				String newInput = input.substring(0, input.length() - 1);
				inputTelNumbers.setText(newInput);
				CallInfo.INSTANCE.inputTelNum = newInput;
			}
		}
	};





	/**
	 * 架電ボタン押下時の処理
	 *
	 * ① 電話番号の入力チェック
	 * ② コールできるかチェック
	 * ③ 架電処理
	 */
	private final OnClickListener callClickListener = new OnClickListener(){
		@Override
		public void onClick(final View view) {
			view.setClickable(false);

			MyFrameLayout.hideTabButton();

			TelNumber telNum = new TelNumber(CallInfo.INSTANCE.inputTelNum);


			// ① 電話番号の入力チェック
			if (telNum.isEmpty() ||
			// ② コールできるかチェック
					!MainService.isEnableCall()) {
				myFrameLayout.findViewById(jp.pulseanddecibels.buzbiz.R.id.num_call_button).setClickable(true);
				return;
			}

			// 通話データ初期化
			CallInfo.ClearData();

			// 通話の情報を設定
			setCallInformation(null, null);

			if(MainActivity.me != null && !MainActivity.me.isFinishing()){
				// 通話中画面へ設定
				setCallScreen();
			}

			// 架電を実施
			MainService.LIB_OP.startCall(telNum, context);

			// 入力番号を初期化
			CallInfo.INSTANCE.inputTelNum = Util.STRING_EMPTY;

			view.setClickable(true);
		}
	};





	/**
	 * 電話帳参照ボタン押下時の処理
	 */
	private final OnClickListener moveTelListButtonClickListener = new OnClickListener(){
		@Override
		public void onClick(final View view) {
			view.setClickable(false);

			MyFrameLayout.hideTabButton();

			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
			((Activity)context).startActivityForResult(intent, MainActivity.MY_ID);

			view.setClickable(true);
		}
	};





	/**
	 * キーパッド表示ボタンの処理
	 */
	private final OnClickListener displayKaypadClickListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
//			Log.e(Util.LOG_TAG,"  キーパッド画面.displayKaypadClickListener  ");


			setCallScreenWithKaypad();
		}
	};





	/**
	 * ミュート・スピーカーボタンUIを初期化
	 */
	public void initUiMuteAndSpeakerButton(){
		Log.d(LOG_TAG, "initUiMuteAndSpeakerButton");

		muteButton		.setChecked  (false);
		muteButton		.setClickable(false);
		topMuteButton	.setChecked  (false);
		topMuteButton	.setClickable(false);
		speakerButton	.setChecked  (false);
		speakerButton	.setClickable(false);
		topSpeakerButton.setChecked  (false);
		topSpeakerButton.setClickable(false);
	}





	/**
	 * 近接センサーからのロック
	 */
	public static void lockFromProximitySensor(){
		Log.d(LOG_TAG, "lockFromProximitySensor");
		try {
			if (MainActivity.me != null && !MainActivity.me.isFinishing()) {
				MainActivity.me.firstScreen.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_hold_button)	.setClickable(false);
				MainActivity.me.firstScreen.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_hangup_button)	.setClickable(false);
				MainActivity.me.firstScreen.keypadScreen.topMuteButton				.setClickable(false);
				MainActivity.me.firstScreen.keypadScreen.muteButton					.setClickable(false);
				MainActivity.me.firstScreen.keypadScreen.topMuteButton				.setClickable(false);
				MainActivity.me.firstScreen.keypadScreen.speakerButton				.setClickable(false);
				MainActivity.me.firstScreen.keypadScreen.topSpeakerButton			.setClickable(false);
			}
		} catch (Exception ex) { }
	}





	/**
	 * 近接センサーからのロック解除
	 */
	public static void unlockFromProximitySensor(){
		Log.d(LOG_TAG, "unlockFromProximitySensor");
		try {
			if (MainActivity.me != null && !MainActivity.me.isFinishing()) {
				MainActivity.me.firstScreen.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_hold_button)	.setClickable(true);
				MainActivity.me.firstScreen.findViewById(jp.pulseanddecibels.buzbiz.R.id.calling_hangup_button)	.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.topMuteButton				.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.muteButton					.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.topMuteButton				.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.speakerButton				.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.topSpeakerButton			.setClickable(true);
			}
		} catch (Exception ex) { }
	}





	/**
	 * ミュート・スピーカーボタンUIを使用できるように設定
	 */
	public static void startUsingUiMuteAndSpeakerButton(){
		Log.d(LOG_TAG, "startUsingUiMuteAndSpeakerButton");
		try {
			if (MainActivity.me != null && !MainActivity.me.isFinishing()) {
				MainActivity.me.firstScreen.keypadScreen.muteButton		.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.topMuteButton	.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.speakerButton	.setClickable(true);
				MainActivity.me.firstScreen.keypadScreen.topSpeakerButton.setClickable(true);
			}
		} catch (Exception ex) { }
	}





	/**
	 * ミュートトグルボタンの処理
	 */
	private final OnCheckedChangeListener muteCheckedChangeListener = new OnCheckedChangeListener(){
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//			Log.e(Util.LOG_TAG,"  キーパッド画面.muteCheckedChangeListener  ");


			// 各ミュートボタンを同期
			if(!buttonView.equals(muteButton))	muteButton   .setChecked(isChecked);
			if(!buttonView.equals(topMuteButton))	topMuteButton.setChecked(isChecked);

			//ミュートに設定
			MainService.LIB_OP.mute(isChecked);
		}
	};





	/**
	 * スピーカートグルボタンの処理
	 */
	private final OnCheckedChangeListener speakerCheckedChangeListener = new OnCheckedChangeListener(){
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//			Log.e(Util.LOG_TAG,"  キーパッド画面.speakerCheckedChangeListener  ");


			// 各スピーカーボタンを同期
			if(!buttonView.equals(speakerButton))	speakerButton   .setChecked(isChecked);
			if(!buttonView.equals(topSpeakerButton))	topSpeakerButton.setChecked(isChecked);

			// スピーカを設定
			MainService.LIB_OP.speeker(context.getApplicationContext(), isChecked);
		}
	};





	/**
	 * 保留ボタン押下時の処理
	 */
	private final OnClickListener callingHoldClickListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
			Log.d(LOG_TAG,"  callingHoldClickListener  ");
			view.setClickable(false);

			// パーキングに転送
			MainService.LIB_OP.hold(context);

			view.setClickable(true);
		}
	};





	/**
	 * 切断ボタン押下時の処理
	 */
	private final OnClickListener callingHungUpClickListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
			view.setClickable(false);
			// 切断
			MainService.LIB_OP.endCall();
			stopTimer();
			view.setClickable(true);
		}
	};





	/**
	 * 通話前画面に設定
	 */
	public void setNormalScreen(){
//		Log.e(Util.LOG_TAG,"  キーパッド画面.setNormalScreen  ");


		// キーパッド画面の状態を保存
//		MainService.currentKeypadScreen = MainService.NOMAL;
		MainService.currentKeypadScreen = MainService.KeyPadStates.NOMAL;

		// 表示の設定
		withKaypatTop 			.setVisibility(View.VISIBLE);
		hideKaypadTop 			.setVisibility(View.GONE);
		beforeCallTopItem		.setVisibility(View.VISIBLE);
		displayCallerImfoLayout	.setVisibility(View.GONE);
		callingKaypadTopItem	.setVisibility(View.GONE);
		Kaypad					.setVisibility(View.VISIBLE);
		beforeCallUnder			.setVisibility(View.VISIBLE);
		moveTelListButtonUnder	.setVisibility(View.VISIBLE);
		callingUnder			.setVisibility(View.GONE);
	}





	/**
	 * 電話中画面に設定	(キーパッド無)
	 */
	public void setCallScreen(){
//		Log.e(Util.LOG_TAG,"  キーパッド画面.setCallScreen  ");


		// キーパッド画面の状態を保存
//		MainService.currentKeypadScreen = MainService.CALLING_NO_KAYPUD;
		MainService.currentKeypadScreen = MainService.KeyPadStates.CALLING_NO_KAYPUD;

		// キーボードを隠す
		Util.hideKeypad((Activity)this.context);

		// 表示の設定
		withKaypatTop 			.setVisibility(View.GONE);
		hideKaypadTop 			.setVisibility(View.VISIBLE);
		beforeCallTopItem		.setVisibility(View.GONE);
		displayCallerImfoLayout	.setVisibility(View.VISIBLE);
		callingKaypadTopItem	.setVisibility(View.GONE);
		Kaypad					.setVisibility(View.GONE);
		beforeCallUnder			.setVisibility(View.GONE);
		moveTelListButtonUnder	.setVisibility(View.GONE);
		callingUnder			.setVisibility(View.VISIBLE);
	}





	/**
	 * 電話中画面に設定	(キーパッド有)
	 */
	public void setCallScreenWithKaypad(){
//		Log.e(Util.LOG_TAG,"  キーパッド画面.setCallScreenWithKaypad  ");


		// キーパッド画面の状態を保存
//		MainService.currentKeypadScreen = MainService.CALLING_KAYPUD;
		MainService.currentKeypadScreen = MainService.KeyPadStates.CALLING_KAYPAD;

		// キーボードを隠す
		Util.hideKeypad((Activity)this.context);

		// 表示の設定
		withKaypatTop 			.setVisibility(View.VISIBLE);
		hideKaypadTop 			.setVisibility(View.GONE);
		beforeCallTopItem		.setVisibility(View.GONE);
		displayCallerImfoLayout	.setVisibility(View.GONE);
		callingKaypadTopItem	.setVisibility(View.VISIBLE);
		Kaypad					.setVisibility(View.VISIBLE);
		beforeCallUnder			.setVisibility(View.GONE);
		callingUnder			.setVisibility(View.VISIBLE);
		displayTelNumbers		.setVisibility(View.VISIBLE);
	}





	/**
	 * 通話の終了処理
	 * ①通話情報を表示
	 * ②各部品の初期化
	 * ③保留画面へ遷移
	 */
	public void displayInformationWhenCallEnd(){
//		Log.e(Util.LOG_TAG,"  キーパッド画面.displayInformationWhenCallEnd  ");

		// 通話時間を表示
		CallTimer.showCallTime();

		// ボタンUIを初期化
		initUiMuteAndSpeakerButton();

		// 通話前画面に戻す
		setNormalScreen();

		// 保留画面に遷移
		myFrameLayout.setHoldScreenWhenEndCall();

		// 初期化
		initText();
		CallInfo.ClearData();
	}





	/**
	 * 通話情報を設定
	 * @param callerTelNum	通話相手の電話番号
	 * @param callerName	通話相手の名前
	 */
	public static void setCallInformation(String callerTelNum, String callerName){
//		Log.e(Util.LOG_TAG,"  キーパッド画面.setCallInformation  ");


		// 通話相手の電話番号を設定
		if(callerTelNum == null){
			CallInfo.INSTANCE.displayCallerNumber = CallInfo.INSTANCE.inputTelNum;
			CallInfo.INSTANCE.callerNumber        = CallInfo.INSTANCE.inputTelNum;
		}else{
			CallInfo.INSTANCE.displayCallerNumber = callerTelNum;
			CallInfo.INSTANCE.callerNumber 		  = callerTelNum;
		}

		// 内線か確認する
		if(!TextUtils.isEmpty(CallInfo.INSTANCE.callerNumber)){
			char c = CallInfo.INSTANCE.callerNumber.charAt(0);
			if('2' <= c && c <= '9'){
				CallInfo.INSTANCE.displayCallerNumber = "内線";
			}
		}


		// 通話相手の名前を設定
        if (callerName == null) {
            // 最初の表示は電話番号とする
            final TelNumber telNum = new TelNumber(CallInfo.INSTANCE.callerNumber);
            CallInfo.INSTANCE.displayCallerName = telNum.getBaseString();

            // サーバに名前を問い合わせ、返答が有れば名前に置き換える
            final Response.Listener ok = new Response.Listener() {
                @Override
                public void onResponse(final Object response) {
                    try {
                        String json = response.toString();
                        String name = new JsonParser().parceJsonForSerchName(json, telNum);
                        if(!TextUtils.isEmpty(name)){
                            CallInfo.INSTANCE.displayCallerName = name;
                        }
                    } catch (Exception ex) { }
                }
            };
            VolleyOperator.resolverName(MainService.me.getApplicationContext(), telNum, ok, null);

        } else {
            CallInfo.INSTANCE.displayCallerName = callerName;
        }
    }





	/**
	 * 通話時間用タイマースタート
	 */
	public static void startTimer() {
		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// メイン画面が存在する場合は、
				if (MainActivity.me != null && !MainActivity.me.isFinishing()) {
					try{

						// 電話番号入力欄(通話中 / 詳細情報)の設定
						MainActivity.me.firstScreen.keypadScreen.displayCallerName	.setText(CallInfo.INSTANCE.displayCallerName);

						MainActivity.me.firstScreen.keypadScreen.displayCallerTimer	.setVisibility(View.VISIBLE);
						MainActivity.me.firstScreen.keypadScreen.displayCallerName	.setVisibility(View.VISIBLE);

						MainActivity.me.firstScreen.keypadScreen.displayCallerTimer	.setText("00:00");
					}catch(Exception ex){
						ex.getStackTrace();
					}
				}

				CallTimer.start();

				DTMF.flag = true;
				DTMF.start();

			}
		});
	}





	/**
	 * 通話時間用タイマーストップ
	 */
	public static void stopTimer() {
		CallTimer.stop();

		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// メイン画面が存在する場合は、
				if (MainActivity.me != null && !MainActivity.me.isFinishing()) {
					try{
						MainActivity.me.firstScreen.keypadScreen.displayCallerTimer.setText("00:00");
					}catch(Exception ex){
						ex.getStackTrace();
					}
				}
			}
		});
	}





	/**
	 * 通話時間をせっていする
	 * @param time	通話時間
	 */
	public static void setDisplayCallerTimer(final String time){
		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// メイン画面が存在する場合は、
				if (MainActivity.me != null && !MainActivity.me.isFinishing()) {
					try{
						MainActivity.me.firstScreen.keypadScreen.displayCallerTimer.setText(time);
					}catch(Exception ex){
						ex.getStackTrace();
					}
				}
			}
		});
	}
}