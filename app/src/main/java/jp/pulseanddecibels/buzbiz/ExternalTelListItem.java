package jp.pulseanddecibels.buzbiz;

import jp.pulseanddecibels.buzbiz.util.Util;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;





/**
 *
 * 外線帳アイテム
 *
 *
 */
public class ExternalTelListItem {

    /** お客様ID */
    @SerializedName("customer_id")
	private int customerId;

    /** お客様名 */
    @SerializedName("customer_name")
	private String customerName;

    /** お客様名（かな） */
    @SerializedName("customer_name_kana")
	private String customerNameKana;

    /** データの追加日 */
    @SerializedName("date_add")
	private String dateAdd;

    /** 修正 */
    @SerializedName("date_modify")
	private String dateModify;

    /** 削除フラグ */
    @SerializedName("del_flag")
	private String delFlag;

    /** FAX番号 */
	private String fax;

    /** 電話番号 */
	private String tel;


	private String index 					= null;		// インデクス
	private char topCharOfCustomerNameKana;				// お客様名（かな）の先頭文字
	private int charType 					= 0;		// お客様名（かな）の先頭文字はアルファベットかどうか	(0: ひらがな // 1: アルファベット // 2: それ以外)
	private boolean filteringFlag 			= false;	// フィルタリングされているかどうか





	/**
	 *  コンストラクタ
	 */
	public ExternalTelListItem(int customerId,
									 String customerName,
									 String customerNameKana,
									 String dateAdd,
									 String dateModify,
									 String delFlag,
									 String fax,
									 String tel){
		this.customerId = customerId;
		this.customerName = customerName;
		this.customerNameKana = customerNameKana;
		this.dateAdd = dateAdd;
		this.dateModify = dateModify;
		this.delFlag = delFlag;
		this.fax = fax;
		this.tel = tel;

		setTopCharOfCustomerNameKana();
	}





	/**
	 * お客様名（かな）の先頭文字を分類
	 */
	public void setTopCharOfCustomerNameKana() {
		// nullは記号扱い
		//  ※ JSONから切り取っている為、文字列nullでチェック
		if(TextUtils.isEmpty(customerNameKana) || customerNameKana.equals(Util.NULL_KOMOJI)){
			charType = 2;
			return;
		}

		// 先頭文字を取得
		topCharOfCustomerNameKana = customerNameKana.charAt(0);

		// アルファベットの場合
		if(topCharOfCustomerNameKana >= 'a' && topCharOfCustomerNameKana <= 'z'){
			charType = 1;
		// アルファベット,ひらがな以外は記号
		}else if(topCharOfCustomerNameKana < 'あ' && topCharOfCustomerNameKana > 'ん'){
			charType = 2;
		}
	}





	// 各項目のアクセサー
	public char getTopCharOfCustomerNameKana() {
		return topCharOfCustomerNameKana;
	}
	public void setTopCharOfCustomerNameKana(char topCharOfCustomerNameKana) {
		this.topCharOfCustomerNameKana = topCharOfCustomerNameKana;
	}
	public int getCharType() {
		return charType;
	}
	public void setCharType(int alphabetMe) {
		this.charType = alphabetMe;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerNameKana() {
		return customerNameKana;
	}
	public void setCustomerNameKana(String customerNameKana) {
		this.customerNameKana = customerNameKana;
	}
	public String getDateAdd() {
		return dateAdd;
	}
	public void setDateAdd(String dateAdd) {
		this.dateAdd = dateAdd;
	}
	public String getDateModify() {
		return dateModify;
	}
	public void setDateModify(String dateModify) {
		this.dateModify = dateModify;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public boolean isFilteringFlag() {
		return filteringFlag;
	}
	public void setFilteringFlag(boolean filteringFlag) {
		this.filteringFlag = filteringFlag;
	}
}