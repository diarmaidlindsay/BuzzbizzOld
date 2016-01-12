package jp.pulseanddecibels.buzbiz;



import com.google.gson.annotations.SerializedName;

import jp.pulseanddecibels.buzbiz.util.Util;





/**
 * 保留リストアイテム
 */
public class HoldListItem {

    /** 保留リストID */
    @SerializedName("ID")
    private int holdListId;

    /** 保留パーク番号 */
    @SerializedName("ParkingNum")
    private String parkingNum;

    /** 保留相手の電話番号 */
    @SerializedName("Caller")
    private String caller;

    /** 保留相手の名前 */
    @SerializedName("CallerName")
    private String callerName;

    /** 対応者の内線番号 */
    @SerializedName("Responders")
    private String responders;

    /** 保留時間 */
    @SerializedName("HoldTime")
    private String holdTime;





    /**
     * コンストラクタ
     */
    public HoldListItem(int holdListId,
                        String parkingNum,
                        String caller,
                        String callerName,
                        String responders,
                        String holdTime) {
        this.holdListId = holdListId;
        this.parkingNum = parkingNum;
        this.caller = caller;
        this.callerName = callerName;
        this.responders = responders;
        this.holdTime = holdTime;

        init();
    }





    public void init(){
        callerName = Util.checkJsonParceString(callerName);
    }





    // 各項目のアクセサー
    public int getHoldListId() {
        return holdListId;
    }
    public void setHoldListId(int holdListId) {
        this.holdListId = holdListId;
    }
    public String getParkingNum() {
        return parkingNum;
    }
    public void setParkingNum(String parkingNum) {
        this.parkingNum = parkingNum;
    }
    public String getCaller() {
        return caller;
    }
    public void setCaller(String caller) {
        this.caller = caller;
    }
    public String getResponders() {
        return responders;
    }
    public void setResponders(String responders) {
        this.responders = responders;
    }
    public String getCallerName() {
        return callerName;
    }
    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }
    public String getHoldTime() {
        return holdTime;
    }
    public void setHoldTime(String holdTime) {
        this.holdTime = holdTime;
    }
}