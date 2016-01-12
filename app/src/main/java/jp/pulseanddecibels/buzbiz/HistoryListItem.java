package jp.pulseanddecibels.buzbiz;



import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import jp.pulseanddecibels.buzbiz.models.BuzbizCalendar;
import jp.pulseanddecibels.buzbiz.util.Util;


/**
 * 履歴リストアイテム
 *
 */
public class HistoryListItem {

    /** ID */
    private int id;

    /** 着信日時 */
    private String date;

    /** (着信元 or 発信先)電話番号 */
    @SerializedName("tel")
    private String telNum;

    /** (着信元 or 発信先)の名前 */
    private String name;

    /** 電話に出たかどうかのステータス */
    @SerializedName("disposition")
    private String disposition;

    /** 着信 or 発信のステータス */
    @SerializedName("status")
    private String callStatus;

    /** 履歴に表示する名前 */
    private String displayName;

    /** 履歴に表示する時間 */
    private String displayTime;

    /** 着信応答フラグ */
    private boolean answerFlag;


    public static final String ANONYMOUS = "anonymous";

    public static final String HITUUTI = "非通知";

    public static final String UNKNOWN = "不明";

    public static final String ANSER = "ANSWERED";





    /**
     * コンストラクタ
     */
    public HistoryListItem(int id,
                           String date,
                           String telNum,
                           String name,
                           String disposition,
                           String callStatus,
                           BuzbizCalendar calendar) {
        this.date = date;
        this.telNum = telNum;
        this.name = name;
        this.disposition = disposition;
        this.callStatus = callStatus;

        init(id, calendar);
    }





    /**
     * アイテムの初期化
     */
    public void init(int id, BuzbizCalendar calendar){
        this.id = id;

        telNum = Util.checkJsonParceString(telNum);
        name   = Util.checkJsonParceString(name);

        // 表示名を設定
        if (!TextUtils.isEmpty(name)) {
            displayName = name;
        } else {
            displayName = telNum;
        }
        if (TextUtils.isEmpty(displayName)) {
            displayName = UNKNOWN;
        } else if (ANONYMOUS.equals(displayName.trim())) {
            displayName = HITUUTI;
        }

        // 着信に応答したかを判定
        if (disposition.equals(ANSER)) {
            answerFlag = true;
        }

        // 表示用時間を設定、
        displayTime = calendar.formatForBuzbizListItem(date);
    }





    public String getTelNum() {
        return telNum;
    }
    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCallStatus() {
        return callStatus;
    }
    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDisposition() {
        return disposition;
    }
    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public boolean isAnswerFlag() {
        return answerFlag;
    }
    public void setAnswerFlag(boolean answerFlag) {
        this.answerFlag = answerFlag;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayTime() {
        return displayTime;
    }
    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }





    public boolean isInStatus() {
        if ("in".equals(callStatus)) {
            return true;
        } else {
            return false;
        }
    }





    public boolean isOutStatus() {
        if ("out".equals(callStatus)) {
            return true;
        } else {
            return false;
        }
    }
}