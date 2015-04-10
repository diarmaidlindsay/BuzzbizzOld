package jp.pulseanddecibels.buzbiz_onpre.models;



import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import jp.pulseanddecibels.buzbiz_onpre.ExternalTelListItem;
import jp.pulseanddecibels.buzbiz_onpre.HistoryListItem;
import jp.pulseanddecibels.buzbiz_onpre.HoldListItem;
import jp.pulseanddecibels.buzbiz_onpre.InternalTelListItem;
import jp.pulseanddecibels.buzbiz_onpre.data.AsteriskAccount;
import jp.pulseanddecibels.buzbiz_onpre.data.TelNumber;
import jp.pulseanddecibels.buzbiz_onpre.util.Logger;
import jp.pulseanddecibels.buzbiz_onpre.util.Util;





/**
 * Created by 普天間 on 2015/02/12.
 */
public class JsonParser {

    /**
     * ライセンスキー用JSONの解析
     *
     * @param json 解析するJSON
     * @return ライセンスキー
     * @throws org.json.JSONException 解析中の例外
     */
    public String parseLicenceKey(String json) throws JSONException {
        BaseJsonData baseData = new BaseJsonData(json);
        return baseData.data;
    }





    /**
     * Asteriskのアカウントを解析する
     *
     * @param json 解析するJSON
     * @return Asteriskのアカウント
     * @throws org.json.JSONException 解析中の例外
     */
    public AsteriskAccount parseAsteriskAccount(String json) throws JSONException {
        BaseJsonData baseData = new BaseJsonData(json);

        Type collectionType = new TypeToken<ArrayList<AsteriskAccount>>() { }.getType();
        ArrayList<AsteriskAccount> list = new Gson().fromJson(baseData.data, collectionType);
        return list.get(0);
    }





    /**
     * JSONを解析し、GCMへのレジストが成功したか確認する
     *
     * @param json 解析するJSON
     * @return GCMへのレジストが成功したばあいはtrue
     * @throws JSONException 解析中の例外
     */
    public boolean parseRegisteGcmOk(String json) throws JSONException {
        BaseJsonData baseData = new BaseJsonData(json);
        return baseData.isDataOk;
    }





    /**
     * JSONを解析し、外線帳を作成する
     *
     * @param json 解析するJSON
     * @return 外線帳
     * @throws JSONException 解析中の例外
     */
    public ArrayList<ExternalTelListItem> parceJsonForExternalTable(String json) throws JSONException {
        BaseJsonData baseData = new BaseJsonData(json);

        Type collectionType = new TypeToken<ArrayList<ExternalTelListItem>>() {
        }.getType();
        ArrayList<ExternalTelListItem> list = new Gson().fromJson(baseData.data, collectionType);

        for (ExternalTelListItem item : list) {
            item.setTopCharOfCustomerNameKana();
        }
        return list;
    }





    /**
     * JSONを解析し、内線帳を作成する
     *
     * @param json 解析するJSON
     * @return 内線帳
     * @throws JSONException 解析中の例外
     */
    public ArrayList<InternalTelListItem> parceJsonForInternalTable(String json) throws JSONException {
        BaseJsonData baseData = new BaseJsonData(json);

        Type collectionType = new TypeToken<ArrayList<InternalTelListItem>>() {
        }.getType();
        ArrayList<InternalTelListItem> list = new Gson().fromJson(baseData.data, collectionType);

        for (InternalTelListItem item : list) {
            item.setDepartmentName();
        }
        return list;
    }





    /**
     * JSONを解析し、内線帳を作成する
     *
     * @param json 解析するJSON
     * @return 内線帳
     * @throws JSONException 解析中の例外
     */
    public ArrayList<HistoryListItem> parceJsonForHistoryList(String json) throws JSONException {
        BaseJsonData baseData = new BaseJsonData(json);

        Type collectionType = new TypeToken<ArrayList<HistoryListItem>>() {
        }.getType();
        ArrayList<HistoryListItem> list = new Gson().fromJson(baseData.data, collectionType);

        // アイテムを整形
        BuzbizCalendar calendar = new BuzbizCalendar();
        int id = 0;
        for (HistoryListItem item : list) {
            item.init(id++, calendar);
        }
        return list;
    }





    /**
     * JSONを解析し、保留リストを作成する
     *
     * @param json 解析するJSON
     * @return 内線帳
     * @throws JSONException 解析中の例外
     */
    public ArrayList<HoldListItem> parceJsonForHoldList(String json) throws JSONException {
        BaseJsonData baseData = new BaseJsonData(json);

        Type collectionType = new TypeToken<ArrayList<HoldListItem>>() {
        }.getType();
        ArrayList<HoldListItem> list = new Gson().fromJson(baseData.data, collectionType);

        for (HoldListItem item : list) {
            item.init();
        }
        return list;
    }





    /**
     * 名前検索用Json文字列を解析する
     * @param json        json文字列
     * @return 名前
     */
    public String parceJsonForSerchName(String json, TelNumber telNum){
        try {
            BaseJsonData baseData = new BaseJsonData(json);
            String jsonStr = baseData.data;

            String rusult			= Util.STRING_EMPTY;
            String group			= Util.STRING_EMPTY;
            String myAddresses		= Util.STRING_EMPTY;
            String name				= Util.STRING_EMPTY;
            String num				= Util.STRING_EMPTY;
            String groupName		= Util.STRING_EMPTY;
            String groupNum			= Util.STRING_EMPTY;


            // 解析
            JSONObject singleJsonObj = new JSONObject(jsonStr);
            group = singleJsonObj.getString("group");
            myAddresses = singleJsonObj.getString("myAddresses");

            JSONArray jsonArray;
            if(!TextUtils.isEmpty(myAddresses) && !"[]".equals(myAddresses)){
                jsonArray = new JSONArray(myAddresses);
                JSONObject groupJsonObj = jsonArray.getJSONObject(0);
                name = groupJsonObj.getString("name");
                num  = groupJsonObj.getString("num");
            }

            if(!TextUtils.isEmpty(group) && !"[]".equals(group)){
                jsonArray = new JSONArray(group);
                JSONObject myAddressesJsonObj = jsonArray.getJSONObject(0);
                groupName = myAddressesJsonObj.getString("groupName");
                groupNum  = myAddressesJsonObj.getString("groupNum");
            }


            // ラベル情報の作成
            if (telNum.isInternal()) {
                rusult = "内線";
                if (!TextUtils.isEmpty(name)) {
                    rusult += "\n" + name;
                }
            } else {
                if (!TextUtils.isEmpty(name)) {
                    rusult += name;
                }
                if (!TextUtils.isEmpty(rusult)) {
                    rusult += "\n";
                }
                if (!TextUtils.isEmpty(num)) {
                    rusult += num;
                }
                if (!TextUtils.isEmpty(groupName) && !groupName.equals("null")) {
                    rusult += "\n(回線: " + groupName + ")";
                } else if (!TextUtils.isEmpty(groupNum) && !groupNum.equals("null")) {
                    rusult += "\n(回線: " + groupNum  + ")";
                }
            }

            rusult = rusult.replace("anonymous", "非通知");
            return rusult;
        } catch (Exception e) {
            return "";
        }
    }





//    /**
//     * 履歴用JSONの解析
//     *
//     * @param json 解析するJSON
//     * @return 履歴リスト
//     * @throws org.json.JSONException 解析中の例外
//     */
//    public ArrayList<HistoryItem> parseHistory(String json) throws JSONException {
//        // サーバからの取得データが正常でない場合は、例外とする
//        BaseJsonData baseData = new BaseJsonData(json);
//        if (!baseData.isDataOk) {
//            throw new RuntimeException("取得データ異常");
//        }
//
//        // メインデータを解析
//        Type collectionType = new TypeToken<ArrayList<HistoryItem>>() { }.getType();
//        return new Gson().fromJson(baseData.data, collectionType);
//    }
//
//
//
//
//
//    /**
//     * 保留用JSONの解析
//     *
//     * @param json 解析するJSON
//     * @return 保留リスト
//     * @throws org.json.JSONException 解析中の例外
//     */
//    public ArrayList<HoldItem> parseHold(String json) throws JSONException {
//        // サーバからの取得データが正常でない場合は、例外とする
//        BaseJsonData baseData = new BaseJsonData(json);
//        if (!baseData.isDataOk) {
//            throw new RuntimeException("取得データ異常");
//        }
//
//        // メインデータを解析
//        Type collectionType = new TypeToken<ArrayList<HoldItem>>() { }.getType();
//        return new Gson().fromJson(baseData.data, collectionType);
//    }





    /**
     * パルスでのJSONの基本階層
     *
     *  ※ 問題なくデータが取得できた場合は、isDataOkがtrueとなる
     */
    private class BaseJsonData {

        /**
         * データが正しい値で有るか
         */
        public boolean isDataOk;

        /**
         * 実データ
         */
        public String data;



        /**
         * コンストラクタ
         */
        public BaseJsonData(String json) throws JSONException {
            JSONObject jo = new JSONObject(json);
            this.isDataOk = jo.getBoolean("result");

            // falseの場合は例外とする
            if (!isDataOk) {
                throw new RuntimeException(jo.getString("error"));
            }

            this.data = jo.getString("data");
        }
    }
}



