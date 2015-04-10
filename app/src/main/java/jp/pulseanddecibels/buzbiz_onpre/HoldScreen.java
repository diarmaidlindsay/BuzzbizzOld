package jp.pulseanddecibels.buzbiz_onpre;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import jp.pulseanddecibels.buzbiz_onpre.data.TelNumber;
import jp.pulseanddecibels.buzbiz_onpre.models.JsonParser;
import jp.pulseanddecibels.buzbiz_onpre.models.PageRenderer;
import jp.pulseanddecibels.buzbiz_onpre.models.VolleyOperator;
import jp.pulseanddecibels.buzbiz_onpre.util.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;





/**
 * 保留画面の設定クラス
 */
public class HoldScreen {

    private final Context context;						// 親のコンテクスト
	private final MyFrameLayout myFrameLayout;			// 親のレイアウト


    private final ListView holdList;					// 保留リスト





	/**
	 * コンストラクタ
	 */
	public HoldScreen(Context context, MyFrameLayout myFrameLayout) {
		this.myFrameLayout	= myFrameLayout;
		this.context		= context;

		// ダミーのログイン画面のインフレート
		dummyLogin = ((Activity) context).getLayoutInflater().inflate(R.layout.login_screen, null);

		// 設定画面に遷移する為のボタンを設定
		Button buttonSetting = (Button)myFrameLayout.findViewById(R.id.setting);
        buttonSetting.setOnTouchListener(goToSetting);

		// 外線帳リストを設定
        holdList = (ListView) myFrameLayout.findViewById(R.id.hold_list);
        holdList.setOnItemClickListener(holdListItemClickListener);
        holdList.setOnScrollListener(holdLisScrollListener);

        // 保留リスト取得用タイマーをスタート
		startHoldScreenTimer();
	}




    //  ----------  内部処理  ----------
    private void showMessage(String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }





    /**
     * 保留リスト取得後の処理
     */
    private class HoldResponseListener implements Response.Listener {
        private final JsonParser jsonParser = new JsonParser();

        /** 保留リスト更新後のチェック処理 */
        protected void check(ArrayList<HoldListItem> list){ }

        @Override
        public void onResponse(final Object response) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<HoldListItem> parcedArray;
                    try{
                        Logger.e(response);
                        String json = response.toString();
                        parcedArray = jsonParser.parceJsonForHoldList(json);
                    }catch (Exception ex){
                        String msg = "保留の解析に失敗しました\n" + ex.getMessage();
                        showMessage(msg);
                        return;
                    }

                    // UIに反映
                    MainActivity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            // 本アクティビティーが生きている場合は、
                            if (((Activity) context).isFinishing() == false) {
                                // アダプターを作成し、外線帳リストにセット
                                HoldListAdapter baseAdapter = new HoldListAdapter(context, parcedArray, R.layout.hold_list_item);
                                holdList.setAdapter(baseAdapter);

                                // 空の場合はメッセージを表示
                                boolean isListEmpty = parcedArray.size() == 0;
                                setHoldListEmptyMessage(isListEmpty);

                                check(parcedArray);
                            }
                        }
                    });
                }
            }).start();
        }
    }





	/**
	 * 保留がない場合のメッセージを設定
	 * @param isEmpty	保留リストが空であるかどうか
	 */
	private void setHoldListEmptyMessage(boolean isEmpty) {
		View message = myFrameLayout.findViewById(R.id.hold_list_empty_message);
		if (isEmpty) {
			message.setVisibility(View.VISIBLE);
		} else {
			message.setVisibility(View.GONE);
		}
	}





	/**
	 * 保留を取る
	 * @param item	選択された保留リストのアイテム
	 */
	private void takeHoldCall(HoldListItem item){
		final TelNumber parkingNum	= new TelNumber(item.getParkingNum());	// 保留パーク番号の取得
		final String callerNum		= item.getCaller();						// 保留相手の電話番号を取得
		final String caller			= item.getCallerName();					// 保留相手の名前を取得

        // 成功時
        final HoldResponseListener checkDownloadHistoryList = new HoldResponseListener() {
            @Override
            protected void check(ArrayList<HoldListItem> list) {
                // 更新した保留リストを走査
                for(HoldListItem newItem : list){
                    // もし同じ通話相手がいれば、
                    if(newItem.getCaller().equals(callerNum)){

                        // 通話の情報を設定
                        KaypadScreen.setCallInformation(callerNum, caller);

                        // 架電を実施
                        MainService.LIB_OP.startCall(parkingNum);

                        // 通話中画面へ設定
                        MainActivity.me.firstScreen.kaypadScreen.setCallScreen();
                        MainActivity.me.firstScreen.setKaypadScreenWhenStartCall();

                        return;
                    }
                }

                // 選択された保留リストがもうない場合は、ダイアログを表示
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("保留")
                       .setIcon(R.drawable.buzbiz_icon)
                       .setMessage("選択された保留アイテムは現在存在しません")
                       .setPositiveButton("OK", null).show();
            }
        };

        // 失敗時
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = "サーバへの通信が失敗しました\n" + error.getMessage();
                showMessage(msg);
            }
        };

        // 保留リストを取得する
        VolleyOperator.downloadHoldList(context, checkDownloadHistoryList, err);
	}





    // ----------- イベントハンドラ ------------
	/**
	 * 保留リストのアイテム選択時の処理
	 */
	private final OnItemClickListener holdListItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			MyFrameLayout.hideTabButton();

			// コールできるかチェック
			if (!MainService.isEnableCall()) {
				return;
			}

            // クリックされたアイテムを取得
			final HoldListItem item = (HoldListItem)((ListView) parent).getItemAtPosition(position);

			// 「はい」選択時の処理
			final OnClickListener yes = new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 保留を取る
					takeHoldCall(item);
				}
			};

			// 架電するか確認する為、ダイアログを表示
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("保留")
				   .setMessage("保留をとりますか？")
				   .setIcon(R.drawable.buzbiz_icon)
				   .setPositiveButton("はい",      yes)
				   .setNeutralButton ("キャンセル",null)
				   .show();
		}
	};





	/**
	 * 保留リストのスクロール時の処理
	 */
	private final OnScrollListener holdLisScrollListener = new OnScrollListener(){
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == 1){		// 1 = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
//				Log.e(Util.LOG_TAG,"  HoldScreen.holdLisScrollListener.onScrollStateChanged  ");
				MyFrameLayout.hideTabButton();
			}
		}
	};





    // -------------- タイマー --------------
    /** 保留リスト取得用タイマー */
    private Timer holdListTimer;

    /** 同期用オブジェクト */
    private final Object lock = new Object();





    /**
     * 保留リストタイマーをスタート
     */
    public void startHoldScreenTimer() {
        // メッセージを非表示
        setHoldListEmptyMessage(false);

        // ログインしていない場合は、何もしない
        if (!MainService.LIB_OP.isLogined()) {
            return;
        }

        synchronized (lock) {
            try {
                stopHoldScreenTimer();
                holdListTimer = new Timer();
                holdListTimer.schedule(new HoldScreenTimerTask(), 500, 10000);
            } catch (Exception e) {
                Logger.e("  保留リスト更新タイマー失敗  " + e.toString());
            }
        }
    }





    /**
     * 保留リスト取得用タイマーを止める
     */
    private synchronized void stopHoldScreenTimer(){
        synchronized (lock) {
            if (holdListTimer == null) {
                return;
            }

            holdListTimer.cancel();
            holdListTimer.purge();
            holdListTimer = null;
        }
    }





    /**
     * 保留リスト取得用タイマー処理
     */
    private class HoldScreenTimerTask extends TimerTask {
        private final HoldResponseListener okDownloadHistoryList =
                new HoldResponseListener();

        @Override
        public void run() {
            // 保留画面以外に遷移していた場合はタイマー終了
            if(MainService.CurentScreenState != MainService.HOLD){
                stopHoldScreenTimer();
                return;
            }

            // メインアクティビティーがなくなっていたらタイマー終了
            if (MainActivity.me == null || MainActivity.me.isFinishing()) {
                stopHoldScreenTimer();
                return;
            }

            // 画面が暗転している時はとばす ※タイマー終了はしない
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                return;
            }

            // 保留リストを取得する
            VolleyOperator.downloadHoldList(context, okDownloadHistoryList, null);
        }
    };





    // ---------- ページカール用 ----------
    private final View dummyLogin;						// ダミーのログイン画面
    private GLSurfaceView glSurface;					// ページカール用のOpenGLビュー
    private PageRenderer renderer;						// OpenGLビューの表示部
    private float startX=0;								// ページカール時の指の初期位置
    private int glCount = 0;
    private Timer glTimer;





    /**
     * 設定画面へ遷移する為のボタンのタッチ処理
     */
    private final OnTouchListener goToSetting = new OnTouchListener(){
        @Override
        public synchronized boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
//				Log.e(Util.LOG_TAG,"  ACTION_DOWN  ");

                    // 2回タッチを防ぐ
                    if (glFlag) {
                        glFlag = false;
                    } else {
                        return false;
                    }

                    startX = event.getX();

                    try{
                        // 画面キャプチャーを解放
                        if (MyFrameLayout.screenCapture != null) {
                            ((ImageView) ((Activity) context).findViewById(R.id.dummy_image)).setImageDrawable(null);
                            MyFrameLayout.screenCapture.recycle();
                            System.gc();
                        }


                        // 現在の画面キャプチャを取得
                        MainActivity.me.parentLayout.setDrawingCacheEnabled(false);
                        MainActivity.me.parentLayout.setDrawingCacheEnabled(true);
                        Bitmap tmpBitmap = Bitmap.createBitmap(MainActivity.me.parentLayout.getDrawingCache());
                        MainActivity.me.parentLayout.setDrawingCacheEnabled(false);


                        if (myFrameLayout.getHeight() < 1000) {
                            MyFrameLayout.screenCapture = Bitmap.createScaledBitmap(tmpBitmap, 256, 512, false);
                        } else {
                            MyFrameLayout.screenCapture = Bitmap.createScaledBitmap(tmpBitmap, 512, 1024, false);
                        }

                        tmpBitmap.recycle();
                        System.gc();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    // 新しいGL画面を作成
                    glSurface = new GLSurfaceView(context.getApplicationContext());


                    // ------------------------------------------------------
                    // 画面透過設定①
                    glSurface.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
                    // ------------------------------------------------------


                    renderer = new PageRenderer(context.getApplicationContext());
                    glSurface.setRenderer(renderer);


                    // ------------------------------------------------------
                    // 画面透過設定②
                    SurfaceHolder glHolder = glSurface.getHolder();
                    glHolder.setFormat(PixelFormat.TRANSLUCENT);

                    // 画面透過設定③
                    glSurface.setZOrderOnTop(true);
                    // ------------------------------------------------------


                    MainActivity.me.parentLayout.addView(glSurface, 0);


                    return true;



                case MotionEvent.ACTION_UP:
//				Log.e(Util.LOG_TAG,"  ACTION_UP  ");

                    if(glTimer == null){
                        // ダミーのログイン画面の追加
                        ((FrameLayout)((Activity)context).findViewById(R.id.gl_plus_layout)).addView(dummyLogin, 1);


                        dummyLogin.setDrawingCacheEnabled(false);
                        dummyLogin.setDrawingCacheEnabled(true);
                        dummyLogin.setDrawingCacheEnabled(false);


                        // 別スレッドで、
                        MainActivity.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(50);
                                } catch (Exception e) {}

                                MainActivity.me.firstScreen	.setVisibility(View.GONE);
                                MainActivity.me.vsWaku	    .setVisibility(View.GONE);
                            }
                        });


                        glTimer =new Timer();
                        glTimer.schedule(new glOperateTimerTask(), 100, 5);
                        return false;
                    }
            }

            return true;
        }
    };

    /** 2重タッチを防ぐためのフラグ */
    private boolean glFlag = true;





    /**
     * ページめくり用タイマータスク
     */
    private class glOperateTimerTask extends TimerTask {
        /** 遷移用インテント */
        private final Intent intentToSetting;


        /** コンストラクタ */
        public glOperateTimerTask(){
            intentToSetting = new Intent(context.getApplicationContext(), LoginActivity.class);
            intentToSetting.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }


        @Override
        public void run() {
            if(glCount >= 3){
                if(startX >= -1300){
                    renderer.page.curlCirclePosition -= (6.0f/(float)renderer.page.GRID);
                    startX -= 10;
                }else{
                    // ログイン画面のアクティビティ起動
                    context.startActivity(intentToSetting);

                    // 保留リスト取得用タイマーを止める
                    stopHoldScreenTimer();

                    this.cancel();
                }
            }

            glCount++;
        }
    };
}