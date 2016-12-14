package com.example.esc.worksigninapplication;

import android.content.Context;
import android.content.SharedPreferences;
 import android.content.SharedPreferences.Editor;

public class SaveToeknPerferenceUtils {
    private static SaveToeknPerferenceUtils preference = null;
    private SharedPreferences sharedPreference;
    private String packageName = "";

    private static final String LOGIN_NAME = "loginName"; //登录名
    private static final String PASSWORD = "password";  //密码

    public static synchronized SaveToeknPerferenceUtils getInstance(Context context){
        if(preference == null)
            preference = new SaveToeknPerferenceUtils(context);
        return preference;
    }


    public SaveToeknPerferenceUtils(Context context){
        packageName = context.getPackageName() + "_preferences";
        sharedPreference = context.getSharedPreferences(
                packageName, context.MODE_PRIVATE);
    }


    public String getLoginName(){
        String loginName = sharedPreference.getString(LOGIN_NAME, "");
        return loginName;
    }


    public void SetLoginName(String loginName){
        Editor editor = sharedPreference.edit();
        editor.putString(LOGIN_NAME, loginName);
        editor.commit();
    }


    public String getPassword(){
        String password = sharedPreference.getString(PASSWORD, "");
        return password;
    }


    public void SetPassword(String password){
        Editor editor = sharedPreference.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
    }

}

