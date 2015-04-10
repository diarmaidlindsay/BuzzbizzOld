package jp.pulseanddecibels.buzbiz_onpre.data;





import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import jp.pulseanddecibels.buzbiz_onpre.models.Setting;





/**
 * Asteriskアカウント情報
 *
 * Created by 普天間 on 2015/04/01.
 */
public class AsteriskAccount {
    @SerializedName("sip_id")
    public String sipId;

    @SerializedName("sip_pass")
    public String sipPass;

    @SerializedName("sip_group_id")
    public String sipGroupId;





    public boolean isEmpty() {
        if (TextUtils.isEmpty(sipId)) {
            return true;
        }
        if (TextUtils.isEmpty(sipPass)) {
            return true;
        }
        if (TextUtils.isEmpty(sipGroupId)) {
            return true;
        }
        return false;
    }





    public void save(Context context) {
        Setting setting = new Setting();
        setting.saveAsteriskAccount(context, this);
    }
}
