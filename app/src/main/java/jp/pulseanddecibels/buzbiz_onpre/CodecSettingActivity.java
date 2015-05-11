package jp.pulseanddecibels.buzbiz_onpre;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import jp.pulseanddecibels.buzbiz_onpre.models.Setting;





public class CodecSettingActivity extends Activity {

    @InjectView(R.id.tb_g711u)
    ToggleButton tbG711u;

//    @InjectView(R.id.tb_g711a)
//    ToggleButton tbG711a;

    @InjectView(R.id.tb_opus)
    ToggleButton tbOpus;

    @InjectView(R.id.tb_ilbc)
    ToggleButton tbIlbc;

    @InjectView(R.id.tb_gsm)
    ToggleButton tbGsm;

    @InjectView(R.id.tb_ssb)
    ToggleButton tbSsb;

    @InjectView(R.id.tb_msb)
    ToggleButton tbMsb;

    @InjectView(R.id.sb_sag)
    SeekBar sbSag;

    @InjectView(R.id.sb_mag)
    SeekBar sbMag;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_codec_setting);
        ButterKnife.inject(this);

        loadSetting();
    }





    /**
     * Saveボタン押下時の処理
     */
    public void clickSave(View view) {
        view.setEnabled(false);

        saveSetting();

        String msg = "設定の反映が完了しました。";
        try {
            MainService.LIB_OP.initVax(getApplicationContext());
        } catch (UnsatisfiedLinkError ex) {
            msg = "設定の反映に失敗しました。";
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

        view.setEnabled(true);
    }






    private void loadSetting() {
        Setting setting = new Setting();
        Context context = getApplicationContext();

        tbG711u.setChecked(setting.loadG711U(context));
        tbOpus .setChecked(setting.loadOpus(context));
        tbIlbc .setChecked(setting.loadIlbc(context));
        tbGsm  .setChecked(setting.loadGSM(context));

        tbSsb.setChecked(setting.loadSpkSoftBoost(context));
        tbMsb.setChecked(setting.loadMicSoftBoost(context));

        sbSag.setProgress(setting.loadSpkAutoGain(context));
        sbMag.setProgress(setting.loadMicAutoGain(context));
    }





    private void saveSetting() {
        Setting setting = new Setting();
        Context context = getApplicationContext();

        setting.saveG711U(context, tbG711u.isChecked());
        setting.saveOpus(context, tbOpus.isChecked());
        setting.saveIlbc(context, tbIlbc.isChecked());
        setting.saveGSM(context, tbGsm.isChecked());

        setting.saveSpkSoftBoost(context, tbSsb.isChecked());
        setting.saveMicSoftBoost(context, tbMsb.isChecked());

        setting.saveSpkAutoGain(context, sbSag.getProgress());
        setting.saveMicAutoGain(context, sbMag.getProgress());
    }
}