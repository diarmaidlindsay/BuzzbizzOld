package jp.pulseanddecibels.buzbiz_onpre;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 *
 * 電話帳横の「あかさたな・・・・PTZの検索」用レイアウト
 *
 * @author 普天間
 *
 */
public class CharSerchLayout extends FrameLayout {


	private int myLayoutHeight = 0;				// 本レイアウトの高さ

	private final int[] charY = new int[18];	// 検索文字の境目





	/**
	 * コンストラクタ
	 */
	public CharSerchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
//		Log.e(Util.LOG_TAG,"  CharSerchLayout.コンストラクタ  ");


		// ビューをインフレート
		LayoutInflater.from(context).inflate(R.layout.char_serch_layout, this);


		// タッチ操作を設定
		this.setOnTouchListener(charSerchLayoutOnTouchListener);
	}





	/**
	 * 検索用レイアウトのタッチ操作
	 */
	private final OnTouchListener charSerchLayoutOnTouchListener = new OnTouchListener(){
		public boolean onTouch(View v, MotionEvent event) {
//			Log.e(Util.LOG_TAG,"  CharSerchLayout.charSerchLayoutOnTouchListener  ");


			MyFrameLayout.hideTabButton();


			// 初回のみ各高さを設定
			if(myLayoutHeight == 0){
				// レイアウトに配置されて後に、自分の高さを取得
				myLayoutHeight = findViewById(R.id.external_char_serch).getHeight();


				// 文字1つ分の高さを算出
				int h = myLayoutHeight / (charY.length + 1);


				// 各文字の境目の高さを算出
				charY[0] = h;
				for(int i = 1; i < charY.length; i++){
					charY[i] = charY[i - 1] + h;
				}
			}



			// タッチした時または、そこから動かした時は、
			int action = event.getAction();
			if(action == MotionEvent.ACTION_DOWN ||
			   action == MotionEvent.ACTION_MOVE){
				// タッチしている座標を取得
		        int x = (int) event.getX();
		        int y = (int) event.getY();


		        // 指がビューを大きく超えた場合は何もしない
		        if(x < -20){
		        	return true;
		        }



		        // タッチされているY座標より、選択されている文字を割り出す
		        char selectedChar = '\uffff';
		        if		(y >= 0 && y < charY[0])			{	selectedChar = 'あ';	}
		        else if	(y >= charY[0]  && y < charY[1])	{	selectedChar = 'か';	}
		        else if	(y >= charY[1]  && y < charY[2])	{	selectedChar = 'さ';	}
		        else if	(y >= charY[2]  && y < charY[3])	{	selectedChar = 'た';	}
		        else if	(y >= charY[3]  && y < charY[4])	{	selectedChar = 'な';	}
		        else if	(y >= charY[4]  && y < charY[5])	{	selectedChar = 'は';	}
		        else if	(y >= charY[5]  && y < charY[6])	{	selectedChar = 'ま';	}
		        else if	(y >= charY[6]  && y < charY[7])	{	selectedChar = 'や';	}
		        else if	(y >= charY[7]  && y < charY[8])	{	selectedChar = 'ら';	}
		        else if	(y >= charY[8]  && y < charY[9])	{	selectedChar = 'わ';	}
		        else if	(y >= charY[9]  && y < charY[10])	{	selectedChar = 'a';		}
		        else if	(y >= charY[10] && y < charY[11])	{	selectedChar = 'd';		}
		        else if	(y >= charY[11] && y < charY[12])	{	selectedChar = 'g';		}
		        else if	(y >= charY[12] && y < charY[13])	{	selectedChar = 'j';		}
		        else if	(y >= charY[13] && y < charY[14])	{	selectedChar = 'm';		}
		        else if	(y >= charY[14] && y < charY[15])	{	selectedChar = 'p';		}
		        else if	(y >= charY[15] && y < charY[16])	{	selectedChar = 't';		}
		        else if	(y >= charY[16] && y < charY[17])	{	selectedChar = 'z';		}
		        else if	(y >= charY[17])					{	selectedChar = '\uaaaa';}


				// 文字が選択されている場合は、リストを移動させる
				if(selectedChar != '\uffff'){
					// 外線用
					MyFrameLayout mfl = (MyFrameLayout) getParent().getParent().getParent().getParent();
					mfl.externalTableScreen.moveList(selectedChar);
				}
			}


			// ACTION_MOVEイベントを取得する為、trueに設定
			return true;
		}
	};
}