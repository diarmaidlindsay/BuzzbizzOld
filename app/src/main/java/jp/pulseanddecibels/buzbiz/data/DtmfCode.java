package jp.pulseanddecibels.buzbiz.data;

import android.text.TextUtils;

/**
 *
 * DTMFコード
 *
 *
 */
public class DtmfCode {
	/** DTMF文字 */
	private String dtmfString;
	/** DTMFのVaxでの管理数値 */
	private int dtmfInt;





	private DtmfCode(String dtmfString, int dtmfInt){
		this.dtmfString = dtmfString;
		this.dtmfInt	= dtmfInt;
	}





	// 各アクセサ
	public String getDtmfString() {
		return dtmfString;
	}
	public int getDtmfInt() {
		return dtmfInt;
	}





	// 各DTMFコード
	public static final DtmfCode code0 			= new DtmfCode("0", 0);
	public static final DtmfCode code1 			= new DtmfCode("1", 1);
	public static final DtmfCode code2 			= new DtmfCode("2", 2);
	public static final DtmfCode code3 			= new DtmfCode("3", 3);
	public static final DtmfCode code4 			= new DtmfCode("4", 4);
	public static final DtmfCode code5 			= new DtmfCode("5", 5);
	public static final DtmfCode code6 			= new DtmfCode("6", 6);
	public static final DtmfCode code7 			= new DtmfCode("7", 7);
	public static final DtmfCode code8 			= new DtmfCode("8", 8);
	public static final DtmfCode code9 			= new DtmfCode("9", 9);
	public static final DtmfCode codeAsterisk 	= new DtmfCode("*", 10);
	public static final DtmfCode codeSharp 		= new DtmfCode("#", 11);





	/**
	 * 文字列をDTMFコードに変換する
	 * @param word	変換する文字列 (1文字目だけが対象)
	 * @return	DTMFコード
	 */
	public static DtmfCode chengeToDtmfCode(String word) {
		if (TextUtils.isEmpty(word) == false) {
			switch(word.charAt(0)){
				case '0':
					return code0;
				case '1':
					return code1;
				case '2':
					return code2;
				case '3':
					return code3;
				case '4':
					return code4;
				case '5':
					return code5;
				case '6':
					return code6;
				case '7':
					return code7;
				case '8':
					return code8;
				case '9':
					return code9;
				case '*':
					return codeAsterisk;
				case '#':
					return codeSharp;
			}
		}

		throw new RuntimeException("DTMFに変換失敗");
	}
}