package org.cocos2dx.extension;

import org.cocos2dx.lib.Cocos2dxLuaJavaBridge;

import android.content.Context;
import android.widget.Toast;

import com.btgame.onesdk.BtOneSDKManager;
import com.btgame.onesdk.frame.eneity.onesdk.GameRoleInfo;
import com.btgame.onesdk.frame.eneity.onesdk.LoginReusltInfo;
import com.btgame.onesdk.frame.eneity.onesdk.SDKInitInfo;
import com.btgame.onesdk.frame.eneity.onesdk.SDKPaymentInfo;
import com.btgame.onesdk.frame.listener.OnSDKListener;

public class CocosOneSDKManager {
	private static int 				m_loginDoneCallback = -1;
	private static int 			    m_payDoneCallback 	= -1;
	private static int 				m_exitDoneCallback  = -1;
	private static int 				m_logoutCallback  	= -1;
	
	private static int	 			m_sdkInitStatus 	= -1;
	private static Context 			m_ctx 				= null;
	private static LoginReusltInfo  m_loginResultInfo 	= null;
	
	public static void InitSDK(Context ctx){
		m_ctx = ctx;
		/**
		 * SDK��ʼ���ӿ�
		 * @param initInfo  ��ʼ��������
		 * @param sdkListener ȫ����Ϣ�ص���
		 */
		BtOneSDKManager.sdkInit(getInitInfo(ctx), new OnSDKListener() {
			@Override
			public void onPay(int statusCode) {
				DoPayLuaCallback(statusCode);
			}
			@Override
			public void onLogout(int statusCode) {
				DoLogoutLuaCallback(statusCode);
			}
			@Override
			public void onInit(int statusCode) {
				m_sdkInitStatus = statusCode;
			}
			@Override
			public void onExit(int statusCode) {
				DoExitLuaCallback(statusCode);
			}
			
			@Override
			public void onLogin(LoginReusltInfo callBack, int statusCode) {
				m_loginResultInfo = null;
				boolean success = false;
				if (statusCode == 0) {
					m_loginResultInfo = (LoginReusltInfo)callBack;
					success = true;
				} else {
					Toast.makeText(m_ctx, "��¼ʧ��", 1000).show();
				}
				DoLoginLuaCallback(success);
			}
		});
	}
	
	protected static SDKInitInfo getInitInfo(Context ctx) {
		 SDKInitInfo info = new SDKInitInfo();
        //������
		 info.setmCtx(ctx);
    	//����Ϸ��֧����Ϸ���л��˻� ��ò�����������Ϊtrue
		//�������Ϊtrue ��Ϸ����ע����5S֮���������� 
		//��Ϸ���յ�ע���ɹ�����ϢӦ�������������ݱ���
		info.setSupportReStart(false);		 
        return info;
	}
	
	////////////////////////////////////////////
	// Init 
	public static int luaGetSdkInitStatus()
	{
		return m_sdkInitStatus;
	}
	
	////////////////////////////////////////////
	// Login
	public static String luaGetLoginSessionId()
	{
		String sessionId = "";
		if( m_loginResultInfo != null)
			sessionId =  m_loginResultInfo.btSessionId;
		return sessionId;
	}
	
	public static String luaGetLoginDesc()
	{
		String desc = "";
		if( m_loginResultInfo != null)
			desc =  m_loginResultInfo.desc;
		
		return desc;
	}
	
	public static int luaGetLoginPlatfromId()
	{
		int platfromId = -1;
		if( m_loginResultInfo != null)
			platfromId =  m_loginResultInfo.platfromId;
		
		return platfromId;
	}
	
	public static void DoLoginLuaCallback(boolean success)
	{
		if(m_loginDoneCallback == -1) return;
		
		Cocos2dxLuaJavaBridge.callLuaFunctionWithString(m_loginDoneCallback, success ? "success" : "failed");
		Cocos2dxLuaJavaBridge.releaseLuaFunction(m_loginDoneCallback);
		m_loginDoneCallback =  -1;
	}
	
	public static void luaSdkLogin(final int luaFunc)
	{
		m_loginDoneCallback = luaFunc;
		m_loginResultInfo = null;
		BtOneSDKManager.getInstance().sdkLogin();
	}
	////////////////////////////////////////////
	// Pay
	public static void DoPayLuaCallback(final int statusCode)
	{
		if(m_payDoneCallback == -1) return;
	
		boolean success = statusCode == 0;
		Cocos2dxLuaJavaBridge.callLuaFunctionWithString(m_payDoneCallback, success ? "success" : ("" + statusCode) );
		Cocos2dxLuaJavaBridge.releaseLuaFunction(m_payDoneCallback);
		m_payDoneCallback =  -1;
	}
	
	public static void luaSdkPay(final int payDoneCallback, final int payType, final String roleId, final int money, final int rate, final String productName, final String gameGold, final String callbackStr, final String exStr)
	{
		m_payDoneCallback = payDoneCallback;
		SDKPaymentInfo paymentInfo = new SDKPaymentInfo();
		paymentInfo.setRoleId(roleId); // �ش�
		paymentInfo.setCallBackStr(callbackStr); // �ɴ�
		paymentInfo.setMoney(money); // �ش�
		paymentInfo.setPayType(payType); // �ش�
		paymentInfo.setProductName(productName); // �ش�
		paymentInfo.setRate(rate); // �ش�
		paymentInfo.setGameGold(gameGold); // �ش�
		paymentInfo.setExStr(exStr); // �ɴ�
		
		BtOneSDKManager.getInstance().sdkPay(paymentInfo);
	}
	
	////////////////////////////////////////////
	// Exit
	public static void DoExitLuaCallback(final int statusCode)
	{
		boolean success = statusCode == 0;
		if(success)
		{
			BtOneSDKManager.getInstance().sdkDestroy();
		}
		
		if(m_exitDoneCallback == -1) return;
		
		Cocos2dxLuaJavaBridge.callLuaFunctionWithString(m_exitDoneCallback, success ? "success" : "failed" );
		Cocos2dxLuaJavaBridge.releaseLuaFunction(m_exitDoneCallback);
		m_exitDoneCallback =  -1;
	}
	
	public static void luaExitPlatfrom(final int exitCallback)
	{
		m_exitDoneCallback = exitCallback;
		BtOneSDKManager.getInstance().sdkExit();
	}
	
	////////////////////////////////////////////
	// logout
	public static void DoLogoutLuaCallback(final int statusCode)
	{
		boolean success = statusCode == 0;
		
		if(m_exitDoneCallback == -1) return;
		
		Cocos2dxLuaJavaBridge.callLuaFunctionWithString(m_logoutCallback, success ? "success" : "failed" );
		Cocos2dxLuaJavaBridge.releaseLuaFunction(m_logoutCallback);
		m_logoutCallback =  -1;
	}
	
	public static void luaLogoutPlatfrom(final int logoutCallback)
	{
		m_logoutCallback = logoutCallback;
		BtOneSDKManager.getInstance().sdkLogout();
	}
	
	////////////////////////////////////////////
	// create Role 
	public static void luaCreateRole(final int vipLevel, final String roleName, final String roleId, final int roleLevel, final String serverName, final String serverId)
	{
		GameRoleInfo gameInfo = new GameRoleInfo();
		gameInfo.setVipLevel(vipLevel);
		gameInfo.setRoleName(roleName);
		gameInfo.setRoleId(roleId);
		gameInfo.setRoleLevel(roleLevel);
		gameInfo.setServerName(serverName);
		gameInfo.setServerId(serverId);
		BtOneSDKManager.getInstance().createRole(gameInfo);
	}
	
	////////////////////////////////////////////
	// Role enter game 
	public static void luaRoleEnterGame(final int vipLevel, final String roleName, final String roleId, final int roleLevel, final String serverName, final String serverId)
	{
		GameRoleInfo gameInfo = new GameRoleInfo();
		gameInfo.setVipLevel(vipLevel);
		gameInfo.setRoleName(roleName);
		gameInfo.setRoleId(roleId);
		gameInfo.setRoleLevel(roleLevel);
		gameInfo.setServerName(serverName);
		gameInfo.setServerId(serverId);
		BtOneSDKManager.getInstance().createRole(gameInfo);
	}
	
	////////////////////////////////////////////
	// Role update 
	public static void luaRoleUpdate(final int vipLevel, final String roleName, final String roleId, final int roleLevel, final String serverName, final String serverId)
	{
		GameRoleInfo gameInfo = new GameRoleInfo();
		gameInfo.setVipLevel(vipLevel);
		gameInfo.setRoleName(roleName);
		gameInfo.setRoleId(roleId);
		gameInfo.setRoleLevel(roleLevel);
		gameInfo.setServerName(serverName);
		gameInfo.setServerId(serverId);
		BtOneSDKManager.getInstance().createRole(gameInfo);
	}
	////////////////////////////////////////////
	// showUserCenter
	public static void luaShowUserCenter()
	{
		BtOneSDKManager.getInstance().showUserCenter();
	}
}
