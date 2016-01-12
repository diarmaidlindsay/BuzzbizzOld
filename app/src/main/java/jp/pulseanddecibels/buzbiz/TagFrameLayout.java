package jp.pulseanddecibels.buzbiz;

import android.content.Context;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 *
 * 遷移時のアニメーションの引っ張られていくタグ用レイアウト
 *
 *
 */
public class TagFrameLayout extends FrameLayout {


    private final Button dummyButtonGoToKeypad;	// キーパッドへ移動する為のボタンのダミー
    private final Button dummyButtonGoToHold;	// 保留画面へ移動する為のボタンのダミー
    private final Button dummyButtonGoToIn;		// 内線表へ移動する為のボタンのダミー
    private final Button dummyButtonGoToEx;		// 外線表へ移動する為のボタンのダミー
    private final Button dummyButtonGoToHST;	// 履歴画面へ移動する為のボタンのダミー


    // 各タブ判定用
    public static final int GO_TO_KEYPAD	= 0;
    public static final int GO_TO_HOLD		= 1;
    public static final int GO_TO_IN		= 2;
    public static final int GO_TO_EX		= 3;
    public static final int GO_TO_HST		= 4;

	/**
	 * コンストラクタ
	 */
	public TagFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
//		Log.e(Util.LOG_TAG,"  TagFrameLayout.コンストラクタ  ");


		// ビューをインフレート
		LayoutInflater.from(context.getApplicationContext()).inflate(jp.pulseanddecibels.buzbiz.R.layout.tag_flipper_item, this);


		// タブの設定が通常の場合は、
		if (MyFrameLayout.tabPosition == 0) {
			// 反転バージョンのダミータグを非表示にする
			findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_dummy_tab_layout).setVisibility(View.GONE);

			// 各ダミーボタンを取得
			dummyButtonGoToKeypad	= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.dummy_button_to_key);
			dummyButtonGoToHold		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.dummy_button_to_hold);
			dummyButtonGoToIn		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.dummy_button_to_in);
			dummyButtonGoToEx		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.dummy_button_to_ex);
			dummyButtonGoToHST		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.dummy_button_to_hst);


		// タブの設定が反転している場合は、
		}else{
			// 通常のダミータグを非表示にする
			findViewById(jp.pulseanddecibels.buzbiz.R.id.dummy_tab_layout).setVisibility(View.GONE);

			// 各ダミーボタンを取得
			dummyButtonGoToKeypad	= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_dummy_button_to_key);
			dummyButtonGoToHold		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_dummy_button_to_hold);
			dummyButtonGoToIn		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_dummy_button_to_in);
			dummyButtonGoToEx		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_dummy_button_to_ex);
			dummyButtonGoToHST		= (Button)findViewById(jp.pulseanddecibels.buzbiz.R.id.reverse_dummy_button_to_hst);
		}
	}

	/**
	 * 本タブ画面を、押下されたボタン以外を非表示設定にする
	 * @param btn 押下されたボタン
	 */
	public void setTabScreen(int btn){
//		Log.e(Util.LOG_TAG,"  TagFrameLayout.setTabScreen  ");


		if(btn == GO_TO_KEYPAD){
			dummyButtonGoToKeypad.setVisibility(View.VISIBLE);
		}else{
			dummyButtonGoToKeypad.setVisibility(View.INVISIBLE);
		}

		if(btn == GO_TO_HOLD){
			dummyButtonGoToHold.setVisibility(View.VISIBLE);
		}else{
			dummyButtonGoToHold.setVisibility(View.INVISIBLE);
		}

		if(btn == GO_TO_IN){
			dummyButtonGoToIn.setVisibility(View.VISIBLE);
		}else{
			dummyButtonGoToIn.setVisibility(View.INVISIBLE);
		}

		if(btn == GO_TO_EX){
			dummyButtonGoToEx.setVisibility(View.VISIBLE);
		}else{
			dummyButtonGoToEx.setVisibility(View.INVISIBLE);
		}

		if(btn == GO_TO_HST){
			dummyButtonGoToHST.setVisibility(View.VISIBLE);
		}else{
			dummyButtonGoToHST.setVisibility(View.INVISIBLE);
		}
	}
}