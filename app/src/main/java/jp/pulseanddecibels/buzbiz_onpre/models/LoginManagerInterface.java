package jp.pulseanddecibels.buzbiz_onpre.models;



import android.content.Context;





/**
 * Created by 普天間 on 2015/04/01.
 */
public interface LoginManagerInterface {

    /**
     * メッセージを表示する
     *
     * @param message 表示するメッセージ
     */
    void showMessage(String message);

    /**
     * コンテキストを取得
     */
    Context getContext();
}
