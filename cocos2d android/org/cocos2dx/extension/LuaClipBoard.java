package org.cocos2dx.extension;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lua.AppActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class LuaClipBoard {

	public static boolean setClipBoardContent(final String str)
	{
		final Activity context = (Activity) Cocos2dxActivity.getContext();
		context.runOnUiThread(new Runnable() {
			
			@SuppressLint("NewApi") @Override
			public void run() {
				System.out.println("run setClipboard");
				ClipboardManager manager = (ClipboardManager) (context.getSystemService(Context.CLIPBOARD_SERVICE));
				manager.setPrimaryClip(ClipData.newPlainText(null, str));
			}
		});
		return true;
	}
}
