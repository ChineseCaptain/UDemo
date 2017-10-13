package com.uu.udemo.locale;

import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uu.udemo.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 需要手动获取权限
 * adb shell pm grant com.uu.udemo android.permission.CHANGE_CONFIGURATION
 */
public class LocaleActivity extends AppCompatActivity {

    @BindView(R.id.tv_content)
    TextView tvContent;

    String[] locales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locale);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        locales = getAssets().getLocales();
        StringBuffer langs = new StringBuffer();
        Log.i("uu", "当前系统支持的语言：");
        for (String loc : locales) {
            Log.i("uu", loc);
            langs.append(loc).append("\n");
        }
        tvContent.setText(langs.toString());
    }

    @OnClick({R.id.btn_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_switch:
                AlertDialog.Builder builder= new AlertDialog.Builder(this);

                builder.setTitle("选择语言")
                        .setItems(locales, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("uu", "选择语言："+locales[which]);
                                String language = locales[which].substring(0, 2);
                                String country = locales[which].length() > 5 ? locales[which].substring(3, 5) : "";
                                switchLanguage(new Locale(language, country));
                            }
                        }).show();
                break;
        }
    }

    private void switchLanguage(Locale locale) {
        try {
            Object objIActMag, objActMagNative;

            Class clzIActMag = Class.forName("android.app.IActivityManager");

            Class clzActMagNative = Class
                    .forName("android.app.ActivityManagerNative");

            //amn = ActivityManagerNative.getDefault();
            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");

            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);

            // objIActMag = amn.getConfiguration();
            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");

            Configuration config = (Configuration) mtdIActMag$getConfiguration
                    .invoke(objIActMag);

            // set the locale to the new value
            config.locale = locale;

            //持久化  config.userSetLocale = true;
            Class clzConfig = Class
                    .forName("android.content.res.Configuration");
            Field userSetLocale = clzConfig
                    .getField("userSetLocale");
            userSetLocale.set(config, true);

            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            Class[] clzParams = {Configuration.class};

            // objIActMag.updateConfiguration(config);
            Method mtdIActMag$updateConfiguration = clzIActMag
                    .getDeclaredMethod("updateConfiguration", clzParams);

            mtdIActMag$updateConfiguration.invoke(objIActMag, config);

            BackupManager.dataChanged("com.android.providers.settings");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
