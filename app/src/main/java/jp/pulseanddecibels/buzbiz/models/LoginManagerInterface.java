package jp.pulseanddecibels.buzbiz.models;



import android.content.Context;





/**
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
