package jp.pulseanddecibels.buzbiz_onpre.models;

/**
 *
 * 情報取得用スレッド
 *
 * @author 普天間
 *
 */
public abstract class RunnableForGettingInfo implements Runnable {
	/** 取得情報 */
	public String info;
}