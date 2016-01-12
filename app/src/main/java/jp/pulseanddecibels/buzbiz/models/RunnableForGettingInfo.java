package jp.pulseanddecibels.buzbiz.models;

/**
 *
 * 情報取得用スレッド
 *
 *
 */
public abstract class RunnableForGettingInfo implements Runnable {
	/** 取得情報 */
	public String info;
}