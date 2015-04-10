package jp.pulseanddecibels.buzbiz_onpre.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jp.pulseanddecibels.buzbiz_onpre.util.Util;

/**
 *
 * BUZBIZ用日付表示のカレンダー
 *
 * @author 普天間
 *
 */
public class BuzbizCalendar {
	private static final String BLANK 		= " ";

	private static final String YESTERDAY 	= "昨日";
	private static final String SUNDAY		= "日曜日";
	private static final String MONDAY		= "月曜日";
	private static final String TUESDAY		= "火曜日";
	private static final String WEDNESDAY	= "水曜日";
	private static final String THURSDAY	= "木曜日";
	private static final String FRIDAY		= "金曜日";
	private static final String SATURDAY	= "土曜日";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE);

	/**
	 * １週間
	 * 		要素0 : 今日
	 * 		要素1 : 昨日
	 * 			以下、順に１日ずれる
	 * 		要素6 : 6日前
	 */
	private final Day[] week = new Day[7];





	/**
	 * コンストラクタ
	 */
	public BuzbizCalendar(){
		// カレンダーを取得
		Calendar cal= Calendar.getInstance();

		// 今日の日付を設定
		week[0] = new Day(getDate(cal, sdf), Util.STRING_EMPTY); // 注意 : 例外で、「今日」であれば表示形式を使わない

		// 昨日の日付を設定
		cal.add(Calendar.DATE, -1);
		week[1] = new Day(getDate(cal, sdf), YESTERDAY);

		// 2～6日前の日付を設定
		for (int i = 2; i < week.length; i++) {
			cal.add(Calendar.DATE, -1);
			week[i] = new Day(getDate(cal, sdf), getDayOfWeek(cal));
		}
	}





	/**
	 * カレンダーより日時を取得
	 * @param cal	使用するカレンダー
	 * @param sdf	取得する形式
	 * @return		日時
	 */
	private String getDate(Calendar cal, SimpleDateFormat sdf){
		return sdf.format(cal.getTime()).toString();
	}





	/**
	 * カレンダーより曜日を取得
	 * @param cal	使用するカレンダー
	 * @return		曜日
	 */
	private String getDayOfWeek(Calendar cal){
		return formatDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
	}





	/**
	 * 曜日を漢字にフォーマットする
	 * @param dayOfWeekNum		数字の曜日
	 * @return					日本語の曜日
	 */
	private String formatDayOfWeek(int dayOfWeekNum){
		switch (dayOfWeekNum) {
			case 1:
				return SUNDAY;
			case 2:
				return MONDAY;
			case 3:
				return TUESDAY;
			case 4:
				return WEDNESDAY;
			case 5:
				return THURSDAY;
			case 6:
				return FRIDAY;
			case 7:
				return SATURDAY;
			default:
				return Util.STRING_EMPTY;
		}
	}





	/**
	 * 日時をBUZBIZのリスト用にフォーマットする
	 * @param date	変換する形式
	 * @return		BUZBIZのリスト用にフォーマットされた日時
	 */
	public String formatForBuzbizListItem(String date) {
		// 本日であれば時刻部分を返す
		if (week[0].equalDate(date)) {
			return Util.splitGet(date, BLANK, 1);
		}

		// 昨日から６日前は表示用の形式を返す
		for (int i = 1; i < week.length; i++) {
			if(week[i].equalDate(date)){
				return week[i].displayDate;
			}
		}

		// ６日前より過去のものは、年月日部分を返す
		return Util.splitGet(date, BLANK, 0);
	}





	private class Day {
		/** 日付 yyyy/mm/dd形式 */
		String date;
		/** 日付 表示用の形式 */
		String displayDate;


		/**
		 * コンストラクタ
		 */
		Day(String date, String displayDate){
			this.date			= date;
			this.displayDate	= displayDate;
		}


		/**
		 * 渡された文字列が本日であるか確認する
		 * @param dateString	確認する文字列形式の日付
		 */
		boolean equalDate(String dateString){
			if (dateString.contains(date)) {
				return true;
			} else {
				return false;
			}
		}
	}
}