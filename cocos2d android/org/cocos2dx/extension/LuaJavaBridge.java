package org.cocos2dx.extension;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lua.AppActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class LuaJavaBridge {

	public static String getPhoneModel()
	{
		Log.i("luaJavaBridge:getPhoneModel: ", android.os.Build.PRODUCT);
		return android.os.Build.PRODUCT;
	}
	
	public static String getPhoneResolution()
	{
		final Activity context = (Activity)Cocos2dxActivity.getContext();
		DisplayMetrics dm = new DisplayMetrics();  
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);  
	      
	    float width=dm.widthPixels*dm.density;   
	    float height=dm.heightPixels*dm.density;   
        
	    String ret = width+"*"+height ;
	    Log.i("luaJavaBridge:getPhoneResolution: ", ret);
        return ret;
	}
	
	public static String getPhoneIMEI()
	{
		String strIMEI = "unKnow IMEI";
		try{
			final Context context = Cocos2dxActivity.getContext();
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if(tm.getDeviceId() != null)
				strIMEI = tm.getDeviceId();
		}catch(Exception e){
			
		}
		Log.i("luaJavaBridge:getPhoneIMEI: ", strIMEI);
		return strIMEI;
	}
	
	public static String getPhoneBrand()
	{
		Log.i("luaJavaBridge:getPhoneBrand: ", android.os.Build.MODEL);
		return android.os.Build.MODEL;
	}
	
	public static void androidLogInfo(String log)
	{
		Log.i("cocos Lua Log:", log);
	}
}
