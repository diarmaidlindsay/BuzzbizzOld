package jp.pulseanddecibels.buzbiz_onpre.models;



import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.ArrayList;

import jp.pulseanddecibels.buzbiz_onpre.util.Util;





/**
 * Created by 普天間 on 2015/02/16.
 */
public class ContentProvideHelper {


    /**
     * 指定されたURIの電話番号を取得する
     */
    public String[] getTelNumberFromUri(Context context, Uri uri) {
        ArrayList<String> result = new ArrayList<String>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(uri, null, null, null, null);
        if (c.moveToFirst()) {
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

            // 選択された人の電話番号をすべて取得
            Cursor telC = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone._ID + " = '" + id + "'",
                    null,
                    null);

            if (telC.moveToFirst()) {
                do {
                    String tmp = telC.getString(telC.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                    String telNum = tmp.replaceAll("[^0-9]", Util.STRING_EMPTY);
                    result.add(telNum);
                } while (telC.moveToNext());
            }
            telC.close();
        }
        c.close();

        return result.toArray(new String[0]);
    }
}
