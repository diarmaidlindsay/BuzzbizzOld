package jp.pulseanddecibels.buzbiz_onpre;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;





/**
 * 内線帳アイテム
 * @author 普天間
 */
public class InternalTelListItem {


	private int id = -1;					// ID
    @SerializedName("sip_group_id")
	private String sipGroupId = null;		// SIPグループのID
    @SerializedName("department_name")
	private String departmentName = null;	// SIPグループの名前
    @SerializedName("sip_id")
	private String sipId = null;			// 内線ユーザのID
    @SerializedName("user_name")
	private String userName = null;			// 内線ユーザ名
    @SerializedName("user_name_kana")
	private String userNameKana = null;		// 内線ユーザ名(かな)
    @SerializedName("login_status")
	private int loginStatus = -1;			// ログイン状態


	private String index = null;			// インデクス
	private boolean filteringFlag = false;	// フィルタリングされているかどうか





	/**
	 *  コンストラクタ
	 */
    public InternalTelListItem(int id,
                               String sipGroupId,
                               String departmentName,
                               String sipId,
                               String userName,
                               String userNameKana,
                               int loginStatus) {
        this.id = id;
        this.sipGroupId = sipGroupId;
        this.departmentName = departmentName;
        this.sipId = sipId;
        this.userName = userName;
        this.userNameKana = userNameKana;
        this.loginStatus = loginStatus;

        setDepartmentName();
    }





    /**
     * SIPグループの名前を設定する
     */
    public void setDepartmentName() {
        // SIPグループの名前がない場合は、SIPグループのIDと同じにする
        // nullは記号扱い
        //  ※ JSONから切り取っている為、文字列nullでチェック
        if (TextUtils.isEmpty(departmentName) || departmentName.equals("null")) {
            departmentName = sipGroupId;
        }
    }





	// 各項目のアクセサー
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSipGroupId() {
		return sipGroupId;
	}
	public void setSipGroupId(String sipGroupId) {
		this.sipGroupId = sipGroupId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getSipId() {
		return sipId;
	}
	public void setSipId(String sipId) {
		this.sipId = sipId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNameKana() {
		return userNameKana;
	}
	public void setUserNameKana(String userNameKana) {
		this.userNameKana = userNameKana;
	}
	public int getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
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