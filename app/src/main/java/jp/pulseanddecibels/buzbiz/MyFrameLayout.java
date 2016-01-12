package jp.pulseanddecibels.buzbiz;

import jp.pulseanddecibels.buzbiz.data.CallInfo;
import jp.pulseanddecibels.buzbiz.models.File;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.util.AttributeSet;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * 各フリッパーの親となるクラス
 * 		※ 本クラスでメイン画面のページ遷移を管理
 */
public class MyFrameLayout extends FrameLayout {

	// 各アニメーション
	private final Animation left_in;
	private final Animation right_in;
	private final Animation left_out;
	private final Animation right_out;
	private final Animation button_left_in;
	private final Animation button_right_in;
	private final Animation button_left_out;
	private final Animation button_right_out;


	// 各画面用レイアウト
	private final View cover;					// 表紙
	private final ViewGroup externalTelLayout;	// 外線表用レイアウト
	private final ViewGroup keypadLayout;		// キーパッド用レイアウト
	private final ViewGroup internalTelLayout;	// 内線表用レイアウト
	private final ViewGroup holdLayout;			// 保留画面用レイアウト
	private final ViewGroup historyLayout;		// 履歴画面用レイアウト


    private final ViewGroup tabLayout;			// タブ用レイアウト
    private final ViewGroup rightBottons;		// 右のタグボタンのレイアウト
    private final ViewGroup leftBottons;		// 左のタグボタンのレイアウト

    private final Button buttonGoToKeypad;		// キーパッドへ移動する為のボタン
    private final Button buttonGoToHold;		// 保留画面へ移動する為のボタン
    private final Button buttonGoToIn;			// 内線表へ移動する為のボタン
    private final Button buttonGoToEx;			// 外線表へ移動する為のボタン
    private final Button buttonGoToHst;			// 履歴画面へ移動する為のボタン

    private final Button buttonChangeTab;		// タブの表示の可否変更ボタン
    private final Button buttonChangeTabOnHS;	// タブの表示の可否変更ボタン(保留画面用)
    private final Button buttonChangeTabOnIT;	// タブの表示の可否変更ボタン(内線画面用)
    private final Button buttonChangeTabOnET;	// タブの表示の可否変更ボタン(外線画面用)
    private final Button buttonChangeTabOnHST;	// タブの表示の可否変更ボタン(履歴画面用)



	// タブの設定
	static int tabPosition = 0;					// タブの位置	（0:通常	1:反転）
	static int tabOperation = 0;				// タブの操作	（0:タップ	1:スワイプ）
	static boolean tagFlag = true;				// タブの表示用フラグ
	private boolean tabLockFlag = false;		// タブのロック用フラグ


	/** 画面キャプチャー */
    public static Bitmap screenCapture;

    /** ボタンタッチ時のX座標 */
    private float lastTouchX;


    // ------------------------------------------------------------------------------------ //
    // 各画面の制御用
	// ------------------------------------------------------------------------------------ //
    final HoldScreen holdScreen;					// 保留画面
    final KaypadScreen kaypadScreen;				// キーパッド画面
    final InternalTableScreen internalTableScreen;	// 内線表画面
    final ExternalTableScreen externalTableScreen;	// 外線表画面
    final HistoryScreen historyScreen;				// 履歴画面
    // ------------------------------------------------------------------------------------ //





	/**
	 * コンストラクタ
	 */
	public MyFrameLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
//        Log.e(Util.LOG_TAG,"  MyFrameLayout.コンストラクタ  ");


        // 各staticフィールドを初期化
        tagFlag = true;
		if(screenCapture != null){
			screenCapture.recycle();
			screenCapture = null;
		}


        // ビューをインフレート
        LayoutInflater.from(context.getApplicationContext()).inflate(jp.pulseanddecibels.buzbiz.R.layout.screen, this);


        // 各レイアウトを取得
        cover				= findViewById(jp.pulseanddecibels.buzbiz.R.id.cover);
        externalTelLayout	= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.external_table_screen_layout);
        keypadLayout		= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.keypad_screen_layout);
        internalTelLayout	= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.internal_table_screen_layout);
        holdLayout			= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.hold_screen_layout);
        historyLayout		= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.history_screen_layout);


		// タブの設定が通常の時は、
        if(tabPosition == 0){
            // ボタンのレイアウトを取得
        	tabLayout = (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.include_tab);

            // 各遷移ボタンを設定
            buttonGoToKeypad	= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.button_to_key);
            buttonGoToHold		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.button_to_hold);
            buttonGoToIn		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.button_to_in);
            buttonGoToEx		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.button_to_ex);
            buttonGoToHst		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.button_to_hst);

            rightBottons		= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.tab_screen_right_layout);
            leftBottons			= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.tab_screen_left_layout);


         // タブの設定が反転している時は、
		}else{
			// ボタンのレイアウトを取得
			tabLayout = (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.include_reverse_tab);

            // 各遷移ボタンを設定
            buttonGoToKeypad	= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_button_to_key);
            buttonGoToHold		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_button_to_hold);
            buttonGoToIn		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_button_to_in);
            buttonGoToEx		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_button_to_ex);
            buttonGoToHst		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_button_to_hst);

            rightBottons		= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_tab_screen_right_layout);
            leftBottons			= (ViewGroup)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_tab_screen_left_layout);
		}

        // タブ用ボタンにリスナーを設定
        addTabBottonListener();


        // タブの表示切替ボタンを設定
        buttonChangeTab		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.change_tab_button);
        buttonChangeTabOnHS	= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.hold_title_button);
        buttonChangeTabOnIT = (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.internal_table_title_button);
        buttonChangeTabOnET	= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.external_table_title_button);
        buttonChangeTabOnHST= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.history_title_button);
        buttonChangeTab		.setOnClickListener(changeTabClickEvent);
        buttonChangeTabOnHS	.setOnClickListener(changeTabClickEvent);
        buttonChangeTabOnIT	.setOnClickListener(changeTabClickEvent);
        buttonChangeTabOnET	.setOnClickListener(changeTabClickEvent);
        buttonChangeTabOnHST.setOnClickListener(changeTabClickEvent);


//		 通常時は、
//		if(MainService.curentKaypadScreen == MainService.NOMAL){
			if(MainService.curentKaypadScreen == MainService.KeyPadStates.NOMAL){
	        // 表示画面を表紙画面に設定
//			MainService.CurentScreenState = MainService.COVER;
				MainService.CurentScreenState = MainService.ScreenStates.COVER;
	        // 表紙以外を非表示に設定
			cover				.setVisibility(View.VISIBLE);
	        internalTelLayout	.setVisibility(View.GONE);
	        externalTelLayout	.setVisibility(View.GONE);
	        keypadLayout		.setVisibility(View.GONE);
	        holdLayout			.setVisibility(View.GONE);
	        historyLayout		.setVisibility(View.GONE);


	     // 通話中の場合は、
		} else {
	        // 表示画面をキーパッド画面に設定
//			MainService.CurentScreenState = MainService.KAYPUD;
				MainService.CurentScreenState = MainService.ScreenStates.KAYPAD;
	        // それ以外のレイアウトは非表示に設定
			cover				.setVisibility(View.GONE);
	        internalTelLayout	.setVisibility(View.GONE);
	        externalTelLayout	.setVisibility(View.GONE);
	        holdLayout			.setVisibility(View.GONE);
	        historyLayout		.setVisibility(View.GONE);

	        // キーパッドボタンを薄表示に設定
	        buttonGoToKeypad.getBackground().setAlpha(50);

	        // タブを非表示に設定
	        tabLayout.setVisibility(View.GONE);

	        ((TextView)findViewById(jp.pulseanddecibels.buzbiz.R.id.display_caller_name_textview)).setText(CallInfo.INSTANCE.displayCallerName);
		}


		// 各アニメーションを設定する
		left_in			= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.left_in);
		right_in		= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.right_in);
		left_out		= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.left_out);
		right_out		= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.right_out);
		button_left_in	= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.button_left_in);
		button_right_in	= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.button_right_in);
		button_left_out	= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.button_left_out);
		button_right_out= AnimationUtils.loadAnimation(context.getApplicationContext(), jp.pulseanddecibels.buzbiz.R.anim.button_right_out);

		// 画面遷移のアニメーションにリスナーを設定
		left_in		.setAnimationListener(moveScreenAnimationListener);
		right_in	.setAnimationListener(moveScreenAnimationListener);

		// 左ボタンのアニメーション終了時にタグレイアウトを非表示に
		// 	※ 最後にアニメスタートが左ボタンなので、右ボタンには不要
		button_left_out.setAnimationListener(buttonHideAnimationListener);


		// ------------------------------------------------------------------------------------ //
		// 各画面の制御オブジェクトを生成
		// ------------------------------------------------------------------------------------ //
		kaypadScreen		= new KaypadScreen			(context, this);
		holdScreen			= new HoldScreen			(context, this);
		internalTableScreen	= new InternalTableScreen	(context, this);
		externalTableScreen	= new ExternalTableScreen	(context, this);
		historyScreen		= new HistoryScreen			(context, this);
		// ------------------------------------------------------------------------------------ //
    }





	/**
	 * タブの設定を取得
	 */
	public static void getTabSetting(Context context){
		tagFlag = true;

		// タブの位置を取得
		String tmpString = File.getValue(context.getApplicationContext(), File.TAB_POSITION);
		if (tmpString.equals("Reversal")) {
			MyFrameLayout.tabPosition = 1;
		} else {
			MyFrameLayout.tabPosition = 0;
		}


		// タブの操作を取得
		tmpString = File.getValue(context.getApplicationContext(), File.TAB_OPERATION);
		if (tmpString.equals("Frick")) {
			MyFrameLayout.tabOperation = 1;
		} else {
			MyFrameLayout.tabOperation = 0;
		}
	}





	/**
	 * タブの設定の初期化
	 */
	public static void initTabSetting(Activity activity){
		// 取得したタブの設定情報より、タブか反転したタブの表示を設定
		if (MyFrameLayout.tabPosition == 0) {
			activity.findViewById(jp.pulseanddecibels.buzbiz.R.id.include_reverse_tab).setVisibility(View.GONE);
		} else {
			activity.findViewById(jp.pulseanddecibels.buzbiz.R.id.include_tab).setVisibility(View.GONE);
		}
	}





	/**
	 * 各タブ用ボタンにリスナーを設定
	 */
	private void addTabBottonListener(){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.addTabBottonListener  ");


		// タップ操作の時は、
        if(tabOperation == 0){
            buttonGoToKeypad.setOnClickListener(goToKeypadClickEvent);
            buttonGoToHold	.setOnClickListener(goToHoldClickEvent);
            buttonGoToIn	.setOnClickListener(goToInClickEvent);
            buttonGoToEx	.setOnClickListener(goToExClickEvent);
            buttonGoToHst	.setOnClickListener(goToHstClickEvent);


         // スワイプ操作時は、
		}else{
            buttonGoToKeypad.setOnTouchListener(buttonTouchListener);
            buttonGoToHold	.setOnTouchListener(buttonTouchListener);
            buttonGoToIn	.setOnTouchListener(buttonTouchListener);
            buttonGoToEx	.setOnTouchListener(buttonTouchListener);
            buttonGoToHst	.setOnTouchListener(buttonTouchListener);
		}
	}




	/**
	 * 画面遷移アニメーション用リスナー
	 */
	private final AnimationListener moveScreenAnimationListener = new AnimationListener() {
		private final Runnable wakuRun = new Runnable() {
			@Override
			public void run() {
				try{
					// 枠を変更する
					MainActivity.me.setNormalWaku();
				}catch(Exception ex){ }
			}
		};


		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationStart(Animation animation) {}

		@Override
		public void onAnimationEnd(Animation animation) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
						System.gc();
					} catch (Exception e) {}

					// タブのロックを解除
					tabLockFlag = false;
				}
			}).start();

			MainActivity.getHandler().postDelayed(wakuRun, 50);
		}
	};





	/**
	 * タブを隠すアニメーション用リスナー
	 */
	private final AnimationListener buttonHideAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
	        // タブを非表示
	        tabLayout.setVisibility(View.GONE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationStart(Animation animation) {}
	};





	/**
	 * タブを隠す
	 * 	※別クラスからの呼び出し用
	 */
	public static void hideTabButton() {
		if (MainActivity.me == null || MainActivity.me.isFinishing()) {
			return;
		}

		MainActivity.me.firstScreen.hideTab();
	}





	/**
	 * タブを復活させる
	 * 	※別クラスからの呼び出し用
	 */
	public static void revivalTabButton() {
		if (MainActivity.me == null || MainActivity.me.isFinishing()) {
			return;
		}

		MainActivity.me.firstScreen.revivalTab();
	}





	/**
	 * タブを隠す
	 */
	private void hideTab(){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.<<タブを隠す>>  ");


		// 既に隠れているときは終了
		if(!tagFlag){
			return;
		}

		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// 各画面タイトルの背景を設定
				buttonChangeTab		.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.ks_change_tab_button_off);
				buttonChangeTabOnHS	.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_off);
				buttonChangeTabOnIT	.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_off);
				buttonChangeTabOnET	.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_off);
				buttonChangeTabOnHST.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_off);

				// アニメーションスタート
				rightBottons.startAnimation(button_left_out);
				leftBottons.startAnimation(button_right_out);
			}
		});

        tagFlag = false;

		// タブのロックを解除
		tabLockFlag = false;
	}



	/**
	 * タブを復活させる
	 */
	private void revivalTab(){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.<<タブを復活させる>>  ");


		// 既に表示しているときは終了
		if(tagFlag){
			return;
		}

		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// 画面ちらつき防止用に１度アニメーションを実施
				rightBottons.startAnimation(button_left_in);

				// タブを表示に設定
				tabLayout.setVisibility(View.VISIBLE);

				// 背景を設定
				buttonChangeTab.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.ks_change_tab_button_on);
				buttonChangeTabOnHS.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_on);
				buttonChangeTabOnIT.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_on);
				buttonChangeTabOnET.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_on);
				buttonChangeTabOnHST.setBackgroundResource(jp.pulseanddecibels.buzbiz.R.drawable.title_button_on);

				// アニメーションスタート
				rightBottons.startAnimation(button_left_in);
				leftBottons.startAnimation(button_right_in);
			}
		});

        tagFlag = true;

		// タブのロックを解除
		tabLockFlag = false;
	}


	private void setTabStates(MainService.ScreenStates states) {

		MainService.CurentScreenState = states;// 表示画面を変更


		keypadLayout.setVisibility(View.GONE);
		cover.setVisibility(View.GONE);
		holdLayout.setVisibility(View.GONE);
		internalTelLayout.setVisibility(View.GONE);
		externalTelLayout.setVisibility(View.GONE);
		historyLayout.setVisibility(View.GONE);

		buttonGoToKeypad.getBackground().setAlpha(255);
		buttonGoToHold.getBackground().setAlpha(255);
		buttonGoToIn.getBackground().setAlpha(255);
		buttonGoToEx.getBackground().setAlpha(255);
		buttonGoToHst.getBackground().setAlpha(255);


		switch (states) {
			case KAYPAD:
				keypadLayout.setVisibility(View.VISIBLE);
				buttonGoToKeypad.getBackground().setAlpha(50);
				break;

			case HOLD:
				holdLayout.setVisibility(View.VISIBLE);
				buttonGoToHold.getBackground().setAlpha(50);
				break;

			case INTERNAL_TABLE:
				internalTelLayout.setVisibility(View.VISIBLE);
				buttonGoToIn.getBackground().setAlpha(50);
				break;

			case EXTERBAN_TABLE:
				externalTelLayout.setVisibility(View.VISIBLE);
				buttonGoToEx.getBackground().setAlpha(50);
				break;

			case HISTORY:
				historyLayout.setVisibility(View.VISIBLE);
				buttonGoToHst.getBackground().setAlpha(50);
				break;
		}
	}

	/**
	 * 次画面の表示レイアウトを準備
	 * @param selectLayout 次画面のレイアウト
	 */
//	private void prepareScreen(int selectLayout) {
	private void prepareScreen(MainService.ScreenStates selectLayout) {

		//タブの設定及びステータスの設定
		setTabStates(selectLayout);

		switch(selectLayout){
//		case KEYPAD :
			case KAYPAD:
			// ボタンUIを初期化
			kaypadScreen.initUiMuteAndSpeakerButton();
			break;


		case HOLD :
			// 保留画面を表示設定に変更
			holdScreen.startHoldScreenTimer();					// 保留リストを更新
			break;


			case INTERNAL_TABLE :
			// 内線画面を表示設定に変更
			internalTableScreen.initInternalTable();			// 内線画面を初期化
			internalTableScreen.downloadInternalTable();				// 内線帳リストを設定
			break;


			case EXTERBAN_TABLE:
			// 外線画面を表示設定に変更
			externalTableScreen.initExternalTable();			// 外線画面を初期化
			externalTableScreen.downloadExternalTable();				// 外線帳リストを設定
			break;


		case HISTORY :
			// 履歴画面を表示設定に変更
			historyScreen.downloadHistoryList();						// 履歴リストを設定
			break;
		}
	}





	/**
	 * 現在の画面をキャプチャーし、ダミー画面に設定
	 */
	private void setBackground(){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.setBackground  ");


		ImageView dummyScreen = (ImageView) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.dummy_image);


		try{
			// 画面キャプチャーを解放
			if(screenCapture != null){
				dummyScreen.setImageDrawable(null);
				screenCapture.recycle();
			}

			// 現在の画面キャプチャーを取得
			setDrawingCacheEnabled(false);
			setDrawingCacheEnabled(true);
			screenCapture = Bitmap.createBitmap(getDrawingCache());
			setDrawingCacheEnabled(false);

			dummyScreen.setImageDrawable(new BitmapDrawable(null, screenCapture));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}



	/**
	 * 『タブの表示の可否変更ボタン』押下時の処理
	 */
	private final OnClickListener changeTabClickEvent = new OnClickListener(){
		@Override
		public void onClick(View v) {
//			Log.e(Util.LOG_TAG,"  MyFrameLayout.changeTabClickEvent  ");


			if(tagFlag){
				MyFrameLayout.hideTabButton();
			}else{
				MyFrameLayout.revivalTabButton();
			}
		}
	};





	/**
	 * 『保留画面へ移動する為のボタン』押下時の処理
	 */
	private final OnClickListener goToHoldClickEvent = new OnClickListener(){
		@Override
		public void onClick(View v) {
//			Log.e(Util.LOG_TAG,"  MyFrameLayout.goToHoldClickEvent  ");


			v.setClickable(false);

			// 保留画面へ移動
			goToHold ();

			v.setClickable(true);
		}
	};





	/**
	 * 『内線表へ移動する為のボタン』押下時の処理
	 */
	private final OnClickListener goToInClickEvent = new OnClickListener(){
		@Override
		public void onClick(View v) {
//			Log.e(Util.LOG_TAG,"  MyFrameLayout.goToInClickEvent  ");


			v.setClickable(false);

			// 内線表へ移動
			goToIn ();

			v.setClickable(true);
		}
	};





	/**
	 * 『外線表へ移動する為のボタン』押下時の処理
	 */
	private final OnClickListener goToExClickEvent = new OnClickListener(){
		@Override
		public void onClick(View v) {
//			Log.e(Util.LOG_TAG,"  MyFrameLayout.goToExClickEvent  ");


			v.setClickable(false);

			// 外線表へ移動
			goToEx ();

			v.setClickable(true);
		}
	};





	/**
	 * 『キーパッドへ移動する為のボタン』押下時の処理
	 */
	private final OnClickListener goToKeypadClickEvent = new OnClickListener(){
		@Override
		public void onClick(View v) {
//			Log.e(Util.LOG_TAG,"  MyFrameLayout.goToKeypadClickEvent  ");


			v.setClickable(false);

			// キーパッドへ移動
			goToKeypad ();

			v.setClickable(true);
		}
	};





	/**
	 * 『履歴画面へ移動する為のボタン』押下時の処理
	 */
	private final OnClickListener goToHstClickEvent = new OnClickListener(){
		@Override
		public void onClick(View v) {
//			Log.e(Util.LOG_TAG,"  MyFrameLayout.goToHSTClickEvent  ");


			v.setClickable(false);

			// 履歴画面へ移動
			goToHst ();

			v.setClickable(true);
		}
	};





	/**
	 * ボタンタッチ時の処理
	 */
	private final OnTouchListener buttonTouchListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
//			Log.e(Util.LOG_TAG,"  MyFrameLayout.buttonTouchListener  ");


            switch (event.getAction()) {
            // タッチ時は、
            case MotionEvent.ACTION_DOWN:
            	// タッチ場所を保存
                lastTouchX = event.getX();
                System.gc();
                break;


            // スワイプ時は、
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();

                // 『キーパッドへ移動する為のボタン』タッチ時の処理
				if (v.equals(buttonGoToKeypad)) {
					if (tabPosition == 0 && lastTouchX > currentX + 50) {
						goToKeypad();
					} else if (tabPosition == 1 && lastTouchX < currentX - 50) {
						goToKeypad();
					}


				}else if(v.equals(buttonGoToHold)){
					if (tabPosition == 0 && lastTouchX < currentX - 50) {
						goToHold();
					} else if (tabPosition == 1 && lastTouchX > currentX + 50) {
						goToHold();
					}


				}else if(v.equals(buttonGoToIn)){
					if (tabPosition == 0 && lastTouchX < currentX - 50) {
						goToIn();
					} else if (tabPosition == 1 && lastTouchX > currentX + 50) {
						goToIn();
					}


				}else if(v.equals(buttonGoToEx)){
					if (tabPosition == 0 && lastTouchX < currentX - 50) {
						goToEx();
					} else if (tabPosition == 1 && lastTouchX > currentX + 50) {
						goToEx();
					}


				}else if(v.equals(buttonGoToHst)){
					if (tabPosition == 0 && lastTouchX > currentX + 50) {
						goToHst();
					} else if (tabPosition == 1 && lastTouchX < currentX - 50) {
						goToHst();
					}
				}
                break;

        }


            return true;
		}
	};





	/**
	 * 通話開始の際、通話画面に遷移せせる
	 */
	public void setKaypadScreenWhenStartCall(){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.setKaypadScreenWhenStartCall  ");


		// 現在の画面がキーパド画面であれば何もしない
//		if(MainService.CurentScreenState == MainService.KAYPUD)	return;
		if(MainService.CurentScreenState == MainService.ScreenStates.KAYPAD)	return;

		// 画面を保留画面へ設定
//		prepareScreen(KEYPAD);
		prepareScreen(MainService.ScreenStates.KAYPAD);
	}





	/**
	 * 通話終了の際、保留画面に遷移せせる
	 */
	public void setHoldScreenWhenEndCall(){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.setHoldScreenWhenEndCall  ");


		// 現在の画面が保留画面であれば何もしない
//		if(MainService.CurentScreenState == MainService.HOLD)	return;
		if(MainService.CurentScreenState == MainService.ScreenStates.HOLD)	return;
		// タブを表示
		tabLayout.setVisibility(View.VISIBLE);

		// 画面を保留画面へ設定
//		prepareScreen(HOLD);
		prepareScreen(MainService.ScreenStates.HOLD);
	}





	/**
	 * キーパッドへ移動
	 */
	private void goToKeypad (){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.goToKeypad  ");


		// タブが消えているときは何もしない
		if(tagFlag == false)	return;


		// 現在の画面がキーパド画面であれば何もしない
//		if (MainService.CurentScreenState == MainService.KAYPUD)	return;
		if(MainService.CurentScreenState == MainService.ScreenStates.KAYPAD)	return;


		if (tabLockFlag) {
			return;
		}
		tabLockFlag = true;


		final ViewFlipper parentViewFlipper		= (ViewFlipper) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.viewflipper);
		final TagFrameLayout nextFlipperItem	= (TagFrameLayout) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.second_screen);


		// 背景を設定
		setBackground();


		nextFlipperItem.setTabScreen(TagFrameLayout.GO_TO_KEYPAD);


		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// アニメーション無でタブ画面に移動
				parentViewFlipper.showNext();


				// 次画面を準備
//				prepareScreen(KEYPAD);
				prepareScreen(MainService.ScreenStates.KAYPAD);
			}
		});



		// タブの設定が通常の時は、
        if(tabPosition == 0){
			// アニメーションを設定
			parentViewFlipper.setInAnimation(right_in);
			parentViewFlipper.setOutAnimation(left_out);

			// 次画面へ移動
			parentViewFlipper.showNext();


		// タブが反転しているときは、
        }else{
        	// アニメーションを設定
			parentViewFlipper.setInAnimation(left_in);
			parentViewFlipper.setOutAnimation(right_out);

			// 次画面へ移動
			parentViewFlipper.showPrevious();
        }
	}





	/**
	 * 保留画面へ移動
	 */
	private void goToHold (){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.goToHold  ");


		// タブが消えているときは何もしない
		if(tagFlag == false)	return;


		// 現在の画面が保留画面であれば何もしない
//		if (MainService.CurentScreenState == MainService.HOLD)	return;
		if(MainService.CurentScreenState == MainService.ScreenStates.HOLD)	return;


		if (tabLockFlag) {
			return;
		}
		tabLockFlag = true;


		final ViewFlipper parentViewFlipper		= (ViewFlipper) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.viewflipper);
		final TagFrameLayout nextFlipperItem	= (TagFrameLayout) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.second_screen);


		// 背景を設定
		setBackground();


		nextFlipperItem.setTabScreen(TagFrameLayout.GO_TO_HOLD);


		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// アニメーション無でタブ画面に移動
				parentViewFlipper.showNext();


				// 次画面を準備
//				prepareScreen(HOLD);
				prepareScreen(MainService.ScreenStates.HOLD);
			}
		});


		// タブの設定が通常の時は、
        if(tabPosition == 0){
			// アニメーションを設定
			parentViewFlipper.setInAnimation(left_in);
			parentViewFlipper.setOutAnimation(right_out);

			// 次画面へ移動
			parentViewFlipper.showPrevious();


		// タブが反転しているときは、
        }else{
			// アニメーションを設定
			parentViewFlipper.setInAnimation(right_in);
			parentViewFlipper.setOutAnimation(left_out);

			// 次画面へ移動
			parentViewFlipper.showNext();
        }
	}





	/**
	 * 内線表へ移動
	 */
	private void goToIn (){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.goToIn  ");


		// タブが消えているときは何もしない
		if(tagFlag == false)	return;


		// 現在の画面が内線表画面であれば何もしない
//		if (MainService.CurentScreenState == MainService.INTERNAL_TABLE)	return;
		if(MainService.CurentScreenState == MainService.ScreenStates.INTERNAL_TABLE)	return;


		if (tabLockFlag) {
			return;
		}
		tabLockFlag = true;


		final ViewFlipper parentViewFlipper		= (ViewFlipper) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.viewflipper);
		final TagFrameLayout nextFlipperItem	= (TagFrameLayout) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.second_screen);


		// 背景を設定
		setBackground();


		nextFlipperItem.setTabScreen(TagFrameLayout.GO_TO_IN);


		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// アニメーション無でタブ画面に移動
				parentViewFlipper.showNext();


				// 次画面を準備
//				prepareScreen(INTERNAL_TEL);
				prepareScreen(MainService.ScreenStates.INTERNAL_TABLE);
			}
		});


		// タブの設定が通常の時は、
        if(tabPosition == 0){
			// アニメーションを設定
			parentViewFlipper.setInAnimation(left_in);
			parentViewFlipper.setOutAnimation(right_out);

			// 次画面へ移動
			parentViewFlipper.showPrevious();


			// タブが反転しているときは、
        }else{
			// アニメーションを設定
			parentViewFlipper.setInAnimation(right_in);
			parentViewFlipper.setOutAnimation(left_out);

			// 次画面へ移動
			parentViewFlipper.showNext();
        }
	}





	/**
	 * 外線表へ移動
	 */
	private void goToEx (){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.goToEx  ");


		// タブが消えているときは何もしない
		if(tagFlag == false)	return;


		// 現在の画面が外線表画面であれば何もしない
//		if (MainService.CurentScreenState == MainService.EXTERBAN_TABLE)	return;
		if(MainService.CurentScreenState == MainService.ScreenStates.EXTERBAN_TABLE)	return;


		if (tabLockFlag) {
			return;
		}
		tabLockFlag = true;


		final ViewFlipper parentViewFlipper		= (ViewFlipper) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.viewflipper);
		final TagFrameLayout nextFlipperItem	= (TagFrameLayout) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.second_screen);


		// 背景を設定
		setBackground();


		nextFlipperItem.setTabScreen(TagFrameLayout.GO_TO_EX);


		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// アニメーション無でタブ画面に移動
				parentViewFlipper.showNext();


				// 次画面を準備
//				prepareScreen(EXTERNAL_TEL);
				prepareScreen(MainService.ScreenStates.EXTERBAN_TABLE);
			}
		});


		// タブの設定が通常の時は、
        if(tabPosition == 0){
			// アニメーションを設定
			parentViewFlipper.setInAnimation(left_in);
			parentViewFlipper.setOutAnimation(right_out);

			// 次画面へ移動
			parentViewFlipper.showPrevious();


		// タブが反転しているときは、
        }else{
			// アニメーションを設定
			parentViewFlipper.setInAnimation(right_in);
			parentViewFlipper.setOutAnimation(left_out);

			// 次画面へ移動
			parentViewFlipper.showNext();
        }
	}





	/**
	 * 履歴画面へ移動
	 */
	private void goToHst (){
//		Log.e(Util.LOG_TAG,"  MyFrameLayout.goToHst  ");


		// タブが消えているときは何もしない
		if (tagFlag == false)	return;


		// 現在の画面が外線表画面であれば何もしない
//		if (MainService.CurentScreenState == MainService.HISTORY) 	return;
		if(MainService.CurentScreenState == MainService.ScreenStates.HISTORY)	return;


		if (tabLockFlag) {
			return;
		}
		tabLockFlag = true;


		final ViewFlipper parentViewFlipper		= (ViewFlipper) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.viewflipper);
		final TagFrameLayout nextFlipperItem	= (TagFrameLayout) MainActivity.me.findViewById(jp.pulseanddecibels.buzbiz.R.id.second_screen);


		// 背景を設定
		setBackground();


		// ダミーのタブを設定
		nextFlipperItem.setTabScreen(TagFrameLayout.GO_TO_HST);


		MainActivity.getHandler().post(new Runnable() {
			@Override
			public void run() {
				// アニメーション無でタブ画面に移動
				parentViewFlipper.showNext();


				// 次画面を準備
//				prepareScreen(HISTORY);
				prepareScreen(MainService.ScreenStates.HISTORY);
			}
		});


		// タブの設定が通常の時は、
        if(tabPosition == 0){
			// アニメーションを設定
			parentViewFlipper.setInAnimation(right_in);
			parentViewFlipper.setOutAnimation(left_out);

			// 次画面へ移動
			parentViewFlipper.showNext();


		// タブが反転しているときは、
        }else{
        	// アニメーションを設定
			parentViewFlipper.setInAnimation(left_in);
			parentViewFlipper.setOutAnimation(right_out);

			// 次画面へ移動
			parentViewFlipper.showPrevious();
        }
	}
}