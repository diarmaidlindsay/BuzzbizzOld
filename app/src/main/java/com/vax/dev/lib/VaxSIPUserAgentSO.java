package com.vax.dev.lib;

import android.util.Log;

//Vaxの制御クラス
public class VaxSIPUserAgentSO 
{
	static 
	{
		try
		{
			System.loadLibrary("DevVaxSIPUserAgent");
		}
		catch (UnsatisfiedLinkError e) 
		{
			System.err.println("public native code library failed to load.\n" + e);
			
		}
		
	}
	
	public native boolean InitializeEx(boolean bBindToListenIP, String sListenIP, int nListenPort, String sUserName, String sLogin, String sLoginPwd, String sDisplayName, String sDomainRealm, String sSIPProxy, String sSIPOutBoundProxy, int nTotalLine);
	public native boolean Initialize(boolean bBindToListenIP, String sListenIP, int nListenPort, String sFromURI, String sSIPOutBoundProxy, String sSIPProxy, String sLoginId, String sLoginPwd, int nTotalLine);

	public native void  UnInitialize();

	public native boolean RegisterToProxy(int nExpire);
	public native boolean UnRegisterToProxy();

	public native boolean DialCall(int nLineNo,String sDialNo, int nInputDeviceId, int nOutputDeviceId);
	public native boolean Connect(int nLineNo, String sToURI, int nInputDeviceId, int nOutputDeviceId);

	public native boolean Disconnect(int nLineNo);

	public native boolean AcceptCall(int nLineNo, String sCallId, int nInputDeviceId, int nOutputDeviceId);
	public native boolean RejectCall(String sCallId);

	public native boolean JoinTwoLine(int nLineNoA, int nLineNoB);
	public native boolean TransferCallEx(int nLineNo, String sToUserName);
	public native boolean TransferCall(int nLineNo, String sToURI);

	public native boolean HoldLine(int nLineNo);
	public native boolean UnHoldLine(int nLineNo);

	public native boolean IsLineConnected(int nLineNo);
	public native boolean IsLineBusy(int nLineNo);
	public native boolean IsLineOpen(int nLineNo);
	public native boolean IsLineHold(int nLineNo);

	public native boolean OpenLine(int nLineNo, boolean bBindToRTPRxIP, String sRTPRxIP, int nRTPRxPort);
	
	public native boolean ForceInbandDTMF(int nLineNo, boolean bEnable);
	public native boolean DigitDTMF(int nLineNo, int nDigit);

	public native void DeselectAllVoiceCodec();
	public native void SelectAllVoiceCodec();
	public native boolean SelectVoiceCodec(int nCodecNo);
	public native boolean DeselectVoiceCodec(int nCodecNo);

	public native boolean EnableKeepAlive(int nSeconds);
	public native void DisableKeepAlive();

	public native boolean SetLicenceKey(String sLicenceKey);
	public native String GetMyIP();

	public native int GetVaxObjectError();

	public native boolean MuteLineSPK(int nLineNo, boolean bEnable);
	public native boolean MuteLineMIC(int nLineNo, boolean bEnable);

	public native boolean MicSetSoftBoost(boolean bEnable);
	public native boolean MicGetSoftBoost();

	public native boolean SpkSetSoftBoost(boolean bEnable);
	public native boolean SpkGetSoftBoost();

	public native boolean MicSetAutoGain(int nValue);
	public native int MicGetAutoGain();

	public native boolean SpkSetAutoGain(int nValue);
	public native int SpkGetAutoGain();

	public native void MuteMic(boolean bEnable);
	public native void MuteSpk(boolean bEnable);

	public native boolean VoiceChanger(int nPitch);

	public native boolean SetEchoCancellation(boolean bEnable);
	public native boolean GetEchoCancellation();

	public native boolean DonotDisturb(boolean bEnable);
	public native boolean SpeakerPhone(boolean bEnable);
	
	public native boolean DiagnosticLog(boolean bEnable);

	public native void ApplicationEnterForeground();
	public native void ApplicationEnterBackground();
	
	public native int GetJitterCountPacketTotal(int nLineNo);
	public native int GetJitterCountPacketLost(int nLineNo);
	public native int GetJitterSizeBuffer(int nLineNo);
	
	public native boolean CryptCOMM(boolean bEnable, String sRemoteIP, int nRemotePort);
	public native void SetUUID(String sUUID);
	
	public native boolean SetOpusEncodeBitRate(int nLineNo, int nBitRate);  // Value: 1 to 500 otherwise  -1 = Auto, -2 = Maximum BitRate
	public native boolean SetOpusEncodeInbandFEC(int nLineNo, boolean bInbandFEC);
	
	public native boolean RecordStart(String sFileName);
	public native boolean RecordStop();
	public native boolean RecordPause(boolean bEnable);
		
	///////////////////////////////////////////
	
	public native void PostOneSecondTick();
	
	public native void PostSocketRecvSIP(byte sData[], int nDataSize, String sFromIP, int nFromPort);     
	public native void PostSocketRecvRTP(int nLineNo, byte[] sData, int nDataSize, String sFromIP, int nFromPort);

	public native boolean PostMicDataPCM(byte[] aDataPCM, int nSizePCM);
		
	////////////////EVENTS //////////////////////

	public void OnSuccessToRegister()
	{
	}
	public  void OnSuccessToReRegister() 
	{
	}
	public  void OnSuccessToUnRegister() 
	{
	}

	public  void OnTryingToRegister() 
	{
	}
	public  void OnTryingToReRegister()
	{
	}
	public  void OnTryingToUnRegister() 
	{
	}
	
	public  void OnFailToRegister(int nStatusCode, String sReasonPhrase) 
	{
	}
	public  void OnFailToReRegister(int nStatusCode, String sReasonPhrase)
	{
	}
	public  void OnFailToUnRegister(int nStatusCode, String sReasonPhrase) 
	{
	}
	
	public  void OnConnecting(int nLineNo)
	{
	}
	public  void OnTryingToHold(int nLineNo)
	{
	}
	public  void OnTryingToUnHold(int nLineNo)
	{
	}
	public  void OnFailToHold(int nLineNo)
	{
	}
	public  void OnFailToUnHold(int nLineNo)
	{
	}
	public  void OnSuccessToHold(int nLineNo) 
	{
	}
	
	public  void OnSuccessToUnHold(int nLineNo) 
	{
	}
	public  void OnFailToConnect(int nLineNo) 
	{
	}
	public  void OnIncomingCall(String sCallId, String sDisplayName, String sUserName, String sFromURI, String sToURI) 
	{
	}
	
	public  void OnIncomingCallRingingStart(String sCallId) 
	{
	}
	public  void OnIncomingCallRingingStop(String sCallId)
	{
	}
	
	public  void OnConnected(int nLineNo, String sTxRTPIP, int nTxRTPPort, String sCallId)
	{
	}
	
	public  void OnProvisionalResponse(int nLineNo, int nStatusCode, String sReasonPharase) 
	{
	}
	public  void OnFailureResponse(int nLineNo, int nStatusCode, String sReasonPharase) 
	{
	}
	
	public  void OnRedirectResponse(int nLineNo, int nStatusCode, String sReasonPharase, String sContact) 
	{
	}
	public  void OnDisconnectCall(int nLineNo)
	{
	}
	
	public  void OnCallTransferAccepted(int nLineNo) 
	{
	}
	public  void OnFailToTransfer(int nLineNo, int nStatusCode, String sReasonPharase)
	{
	}
	
	public  void OnIncomingDiagnostic(String sMsgSIP, String sFromIP, int nFromPort)
	{
	}
	public  void OnOutgoingDiagnostic(String sMsgSIP, String sToIP, int nToPort)
	{
	}
	
	public void OnHoldCall(int nLineNo)
	{
	}
	public void OnUnHoldCall(int nLineNo)
	{
	}
	
	///////////////////  SIP  /////////////////////////
	
	public void OnSocketOpenSIP(String sListenIP, int nListenPort)
	{
		
	}
	public  void OnSocketCloseSIP() 
	{
	}
	public  void OnSocketSendSIP(byte[] objData, int nDataSize, String sToIP, int nToPort)
	{
	}
	
	///////////////////////  RTP  ///////////////////////////////////
	
	public  void OnSocketOpenRTP(int nLineNo, String sListenIP, int nListenPort)
	{
	}
	public  void OnSocketCloseRTP(int nLineNo) 
	{
	}
	public  void OnSocketSendRTP(int nLineNo, byte[] sData, int nDataSize, String sToIP, int nToPort) 
	{
	}
	
	////////////////////// Media ///////////////////////////////
	
	public  void OnOpenMediaDevice(int nLineNo, int nDeviceMIC, int nDeviceSPK) 
	{
	}
	public  void OnCloseMediaDevice(int nLineNo) 
	{
		
	}
	
	public void OnSpkDataPCM(byte[] aData, int nDataSize)
	{
	
	}
		
}