package com.vax.dev.lib;

import jp.pulseanddecibels.buzbiz_onpre.models.LibEventListener;

public class VaxSIPUserAgent extends VaxSIPUserAgentSO
{
	public static final int TOTAL_LINE_COUNT = 5;

	public static final int G711U_CodecNo = 0;
	public static final int G711A_CodecNo = 1;
	public static final int GSM_CodecNo = 2;
	public static final int iLBC_CodecNo = 3;
	public static final int Opus_CodecNo = 4;

	final SocketSIP m_objSocketSIP = new SocketSIP(this);
	final SocketRTP m_objSocketRTP = new SocketRTP(this);

	final MediaMic m_objMediaMic = new MediaMic(this);
	final MediaSPK m_objMediaSpk = new MediaSPK(this);

	final TimerTick m_objTimerTick = new TimerTick(this);
	SocketRTP[] m_aSocketRTP = null;

	boolean m_bOnline = false;
	boolean m_bMuteMic = false;
	boolean m_bMuteSpk = false;

	boolean m_bDonotDisturb = false;
	boolean m_bIsSprakerOn = false;

	final boolean m_aBusyStatus[] = new boolean[TOTAL_LINE_COUNT]; //// the default value is false

	int m_nLocalPortSIP = 5060;
//	int m_nLocalPortRTP = 7000;
	int m_nLocalPortRTP = 5004;

	final LibEventListener listener;

	public VaxSIPUserAgent(LibEventListener listener)
	{
		this.listener = listener;
	}

	@Override
	public boolean InitializeEx(boolean bBindToListenIP, String sListenIP, int nListenPort, String sUserName, String sLogin, String sLoginPwd, String sDisplayName, String sDomainRealm, String sSIPProxy,String sSIPOutBoundProxy, int nTotalLine)
	{
		boolean bResult =  super.InitializeEx(bBindToListenIP, sListenIP, nListenPort, sUserName, sLogin, sLoginPwd, sDisplayName, sDomainRealm, sSIPProxy, sSIPOutBoundProxy, nTotalLine);

		if(bResult)
		{
			m_aSocketRTP = new SocketRTP[nTotalLine];
		}

		return bResult;
	}

	@Override
	public boolean Initialize(boolean bBindToListenIP, String sListenIP, int nListenPort, String sFromURI, String sSIPOutBoundProxy, String sSIPProxy, String sLoginId, String sLoginPwd, int nTotalLine)
	{
		boolean bResult = super.Initialize(bBindToListenIP, sListenIP, nListenPort, sFromURI, sSIPOutBoundProxy, sSIPProxy, sLoginId, sLoginPwd, nTotalLine);

		if(bResult)
		{
			m_aSocketRTP = new SocketRTP[nTotalLine];
		}

		return bResult;
	}

	@Override
	public void UnInitialize()
	{
		super.UnInitialize();
	}

	@Override
	public boolean RegisterToProxy(int nExpire)
	{
		return super.RegisterToProxy(nExpire);
	}

	@Override
	public boolean UnRegisterToProxy()
	{
		return super.UnRegisterToProxy();
	}

	@Override
	public boolean DialCall(int nLineNo, String sDialNo, int nInputDeviceId, int nOutputDeviceId)
	{
		if(!super.DialCall(nLineNo, sDialNo, nInputDeviceId, nOutputDeviceId))
			return false;

		OnDialing(nLineNo);
		return true;
	}

	@Override
	public boolean Connect(int nLineNo, String sToURI, int nInputDeviceId,
			int nOutputDeviceId)
	{

		return super.Connect(nLineNo, sToURI, nInputDeviceId, nOutputDeviceId);
	}

	@Override
	public boolean Disconnect(int nLineNo)
	{
		OnEndCall(nLineNo);
		return super.Disconnect(nLineNo);
	}

	@Override
	public boolean AcceptCall(int nLineNo, String sCallId, int nInputDeviceId, int nOutputDeviceId)
	{
		 if(!super.AcceptCall(nLineNo, sCallId, nInputDeviceId, nOutputDeviceId))
			 return false;

		 OnAccepting(nLineNo);
		 return true;
	}

	@Override
	public boolean RejectCall(String sCallId)
	{
		return super.RejectCall(sCallId);
	}

	@Override
	public boolean JoinTwoLine(int nLineNoA, int nLineNoB)
	{
		return super.JoinTwoLine(nLineNoA, nLineNoB);
	}

	@Override
	public boolean TransferCallEx(int nLineNo, String sToUserName)
	{
		return super.TransferCallEx(nLineNo, sToUserName);
	}

	@Override
	public boolean TransferCall(int nLineNo, String sToURI)
	{
		return super.TransferCall(nLineNo, sToURI);
	}

	@Override
	public boolean HoldLine(int nLineNo)
	{
		return super.HoldLine(nLineNo);
	}

	@Override
	public boolean UnHoldLine(int nLineNo)
	{

		return super.UnHoldLine(nLineNo);
	}

	@Override
	public boolean IsLineConnected(int nLineNo)
	{
		return super.IsLineConnected(nLineNo);
	}

	@Override
	public boolean IsLineOpen(int nLineNo)
	{
		return super.IsLineOpen(nLineNo);
	}

	@Override
	public boolean IsLineBusy(int nLineNo)
	{
		 if(nLineNo == -1)
			 return false;

		 return m_aBusyStatus[nLineNo];
	}

	@Override
	public boolean IsLineHold(int nLineNo)
	{
		return super.IsLineHold(nLineNo);
	}

	@Override
	public boolean OpenLine(int nLineNo, boolean bBindToRTPRxIP, String sRTPRxIP, int nRTPRxPort)
	{
		return super.OpenLine(nLineNo, bBindToRTPRxIP, sRTPRxIP, nRTPRxPort);
	}

	@Override
	public boolean DigitDTMF(int nLineNo, int nDigit)
	{
		return super.DigitDTMF(nLineNo, nDigit);
	}

	@Override
	public void DeselectAllVoiceCodec()
	{
		super.DeselectAllVoiceCodec();
	}

	@Override
	public void SelectAllVoiceCodec()
	{
		super.SelectAllVoiceCodec();
	}

	@Override
	public boolean DeselectVoiceCodec(int nCodecNo)
	{
		return super.DeselectVoiceCodec(nCodecNo);
	}

	@Override
	public boolean SelectVoiceCodec(int nCodecNo)
	{
		return super.SelectVoiceCodec(nCodecNo);
	}

	@Override
	public boolean EnableKeepAlive(int nSeconds)
	{
		return super.EnableKeepAlive(nSeconds);
	}

	@Override
	public void DisableKeepAlive()
	{
		super.DisableKeepAlive();
	}

	@Override
	public boolean SetLicenceKey(String sLicenceKey)
	{
		return super.SetLicenceKey(sLicenceKey);
	}

	@Override
	public String GetMyIP()
	{
		return super.GetMyIP();
	}

	@Override
	public int GetVaxObjectError()
	{
		return super.GetVaxObjectError();
	}

	@Override
	public boolean MuteLineMIC(int nLineNo, boolean bEnable)
	{
		return super.MuteLineMIC(nLineNo, bEnable);
	}

	@Override
	public boolean MuteLineSPK(int nLineNo, boolean bEnable)
	{
		return super.MuteLineSPK(nLineNo, bEnable);
	}

	@Override
	public boolean MicSetSoftBoost(boolean bEnable)
	{
		return super.MicSetSoftBoost(bEnable);
	}

	@Override
	public boolean MicGetSoftBoost()
	{
		return super.MicGetSoftBoost();
	}

	@Override
	public boolean SpkSetSoftBoost(boolean bEnable)
	{
		return super.SpkSetSoftBoost(bEnable);
	}

	@Override
	public boolean SpkGetSoftBoost()
	{
		return super.SpkGetSoftBoost();
	}

	@Override
	public boolean MicSetAutoGain(int nValue)
	{
		return super.MicSetAutoGain(nValue);
	}

	@Override
	public int MicGetAutoGain()
	{
		return super.MicGetAutoGain();
	}

	@Override
	public boolean SpkSetAutoGain(int nValue)
	{
		return super.SpkSetAutoGain(nValue);
	}

	@Override
	public int SpkGetAutoGain()
	{
		return super.SpkGetAutoGain();
	}

	 /* @Override
	public void MuteMic(boolean bEnable)
	{

		super.MuteMic(bEnable);
	}
	 @Override
	public void MuteSpk(boolean bEnable)
	{

		super.MuteSpk(bEnable);
	}*/

	@Override
	public boolean VoiceChanger(int nPitch)
	{
		return super.VoiceChanger(nPitch);
	}

	@Override
	public boolean SetEchoCancellation(boolean bEnable)
	{
		return super.SetEchoCancellation(bEnable);
	}

	@Override
	public boolean GetEchoCancellation()
	{
		return super.GetEchoCancellation();
	}

	@Override
	public boolean DonotDisturb(boolean bEnable)
	{
		 m_bDonotDisturb = bEnable;
		return super.DonotDisturb(bEnable);
	}

	@Override
	public boolean SpeakerPhone(boolean bEnable)
	{
		return super.SpeakerPhone(bEnable);
	}


	 @Override
	public boolean DiagnosticLog(boolean bEnable)
	{
		return super.DiagnosticLog(bEnable);
	}

	@Override
	public void PostOneSecondTick()
	{
		super.PostOneSecondTick();
	}

	@Override
	public void PostSocketRecvSIP(byte[] sData, int nDataSize, String sFromIP, int nFromPort)
	{
		super.PostSocketRecvSIP(sData, nDataSize, sFromIP, nFromPort);
	}

	@Override
	public void PostSocketRecvRTP(int nLineNo, byte[] sData, int nDataSize, String sFromIP, int nFromPort)
	{

		super.PostSocketRecvRTP(nLineNo, sData, nDataSize, sFromIP, nFromPort);
	}

	@Override
	public boolean PostMicDataPCM(byte[] aDataPCM, int nSizePCM)
	{

		return super.PostMicDataPCM(aDataPCM, nSizePCM);
	}

	public void VaxUnInit()
	{

		m_objTimerTick.StopTimer();
		m_bOnline = false;
		UnInitialize();
	}

	public boolean VaxInit(String sUserName, String sDisplayName, String nAuthLogin, String sAuthPass, String sDomian, String sSIPProxy, boolean bRegister, int nTotalLines)
	{
		boolean bResult = true;
		String sMyIP = m_objSocketSIP.GetMyIP().toString();

		for(int nListenPortSIP = m_nLocalPortSIP; nListenPortSIP < 7000; nListenPortSIP++)
        {
			if(!m_objSocketSIP.IsAvailablePort(nListenPortSIP))
			{
				continue;
			}

			bResult = InitializeEx(false, sMyIP, nListenPortSIP, sUserName, nAuthLogin, sAuthPass, sDisplayName, sDomian+":56131", sSIPProxy+":56131", "", nTotalLines);
			break;
        }


		if(bResult == false)
		{
			return false;
		}

		bResult = OpenLines(sMyIP, nTotalLines);
		if(bResult == false) return false;

		if(bRegister)
			RegisterToProxy(1800);

		m_objTimerTick.StartTimer(1000);
		m_bOnline = true;
		return true;
	}

	// TODO 追加
	public boolean reLogin(){
		return RegisterToProxy(1800);
	}


	private Boolean OpenLines(String sMyIP, int nTotalLines)
    {
        Boolean bResult = true;
        int nListenPortRTP = m_nLocalPortRTP;  // RTP port must be an even number (According to the SDP RFC).

         for(int nLineNo = 0; nLineNo < nTotalLines; nLineNo++)
         {
        	if(!SocketRTP.IsAvailablePort(nListenPortRTP))
 			{
        		nListenPortRTP += 2; //Increament by 2, RTP port must be an even number (According to the SDP RFC)
        		nLineNo = nLineNo - 1;
        		continue;
 			}

        	bResult = OpenLine(nLineNo, false, sMyIP, nListenPortRTP);
        	if(bResult == false) break;

        	nListenPortRTP += 2; //Increament by 2, RTP port must be an even number (According to the SDP RFC)
         }

          if(bResult == false)
          {
        	  // Display Message Box.
        	  return false;
          }

          return true;
    }

	public boolean VaxIsOnline()
	{
		return m_bOnline;

	}

	public void SetLocalPortSIP(int nPortSIP)
	{
		m_nLocalPortSIP = nPortSIP;

	}

	public void SetLocalPortRTP(int nPortRTP)
	{
		m_nLocalPortRTP = nPortRTP;

	}

	public void SetVoiceCodec(boolean bEnable, int nCodecNo)
	{
		if(bEnable)
		{
			SelectVoiceCodec(nCodecNo);
		}
		else
		{
			DeselectVoiceCodec(nCodecNo);
		}

	}

	public int GetFreeLine()
	{
		int nLineNo;

		for (nLineNo = 0 ; nLineNo < TOTAL_LINE_COUNT; nLineNo ++)
		{
			if(!IsLineBusy(nLineNo ))
				break;
		}

		return nLineNo;

	}
	public void SetSpeakerState(boolean bSpeakerState)
	{
		m_bIsSprakerOn = bSpeakerState;

	}

	public boolean IsSpeakerOn()
	{
		return m_bIsSprakerOn;
	}

	public void MuteMic(boolean bMute)
	{
		m_bMuteMic = bMute;
		m_objMediaMic.Mute(bMute);
	}

	public void CloseMic()
	{
		m_objMediaMic.CloseMic();
	}

	public boolean IsMuteMic()
	{
		return m_bMuteMic;
	}

	public boolean IsDonotDisturb()
	{
		return m_bDonotDisturb;
	}

	public void MuteSpk(boolean bMute)
	{
		m_bMuteSpk = bMute;
		m_objMediaSpk.Mute(bMute);
	}

	public boolean IsMuteSpk()
	{
		return m_bMuteSpk;
	}

	public void IgnoreCall()
	{

	}

///////////////////////////////////////////    EVENTS    ////////////////////////////////////////////////////////////////////////////////////

	public void OnSuccessToRegister()
	{
		listener.OnSuccessToRegister();
	}

 	public void OnSuccessToReRegister()
	{
 		listener.OnSuccessToReRegister();
	}
	public void OnSuccessToUnRegister()
	{
		listener.OnSuccessToUnRegister();
	}

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////

	public void OnTryingToRegister()
	{
		listener.OnTryingToRegister();
	}
	public void OnTryingToReRegister()
	{
		listener.OnTryingToReRegister();
	}
	public void OnTryingToUnRegister()
	{
		listener.OnTryingToUnRegister();
	}

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////


	public void OnFailToRegister(int nStatusCode, String sReasonPhrase)
	{
		listener.OnFailToRegister(nStatusCode, sReasonPhrase);
	}
	public void OnFailToReRegister(int nStatusCode, String sReasonPhrase)
	{
		listener.OnFailToReRegister(nStatusCode, sReasonPhrase);
	}
	public void OnFailToUnRegister(int nStatusCode, String sReasonPhrase)
	{
		listener.OnFailToUnRegister(nStatusCode, sReasonPhrase);
	}

	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	public void OnDialing(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = true;

		listener.OnDialing(nLineNo);
	}

	public void OnAccepting(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = true;

		listener.OnAccepting(nLineNo);
	}

	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	public void OnEndCall(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = false;

		listener.OnEndCall(nLineNo);

	}

	public void OnConnecting(int nLineNo)
	{
		listener.OnConnecting(nLineNo);
	}

	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	public void OnTryingToHold(int nLineNo)
	{
		listener.OnTryingToHold(nLineNo);
	}
	public void OnTryingToUnHold(int nLineNo)
	{
		listener.OnTryingToUnHold(nLineNo);
	}
	public void OnFailToHold(int nLineNo)
	{
		listener.OnFailToHold(nLineNo);
	}
	public void OnFailToUnHold(int nLineNo)
	{
		listener.OnFailToUnHold(nLineNo);
	}
	public void OnSuccessToHold(int nLineNo)
	{
		listener.OnSuccessToHold(nLineNo);
	}

	public void OnSuccessToUnHold(int nLineNo)
	{
		listener.OnSuccessToUnHold(nLineNo);
	}

	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////


	public void OnFailToConnect(int nLineNo)
	{
		listener.OnFailToConnect(nLineNo);
	}

	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	public void OnIncomingCall(String sCallId, String sDisplayName, String sUserName, String sFromURI, String sToURI)
	{
		listener.OnIncomingCall(sCallId, sDisplayName, sUserName, sFromURI, sToURI);

	}

	public void OnIncomingCallRingingStart(String sCallId)
	{
		listener.OnIncomingCallRingingStart(sCallId);
	}

	public void OnIncomingCallRingingStop(String sCallId)
	{
		listener.OnIncomingCallRingingStop(sCallId);

	}

	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	public void OnConnected(int nLineNo, String sTxRTPIP, int nTxRTPPort, String sCallId)
	{
		listener.OnConnected(nLineNo, sTxRTPIP, nTxRTPPort, sCallId);
	}

	public void OnProvisionalResponse(int nLineNo, int nStatusCode, String sReasonPharase)
	{
		listener.OnProvisionalResponse(nLineNo, nStatusCode, sReasonPharase);
	}
	public void OnFailureResponse(int nLineNo, int nStatusCode, String sReasonPharase)
	{
		listener.OnFailureResponse(nLineNo, nStatusCode, sReasonPharase);
	}

	public void OnRedirectResponse(int nLineNo, int nStatusCode, String sReasonPharase, String sContact)
	{
		listener.OnRedirectResponse(nLineNo, nStatusCode, sReasonPharase, sContact);
	}

	public void OnDisconnectCall(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = false;

		listener.OnDisconnectCall(nLineNo);
	}

	public void OnCallTransferAccepted(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = false;

		listener.OnCallTransferAccepted(nLineNo);
	}

	public void OnFailToTransfer(int nLineNo, int nStatusCode, String sReasonPharase)
	{
		listener.OnFailToTransfer(nLineNo, nStatusCode, sReasonPharase);
	}

	public void OnIncomingDiagnostic(String sMsgSIP, String sFromIP, int nFromPort)
	{
		listener.OnIncomingDiagnostic(sMsgSIP, sFromIP, nFromPort);
	}
	public void OnOutgoingDiagnostic(String sMsgSIP, String sToIP, int nToPort)
	{
		listener.OnOutgoingDiagnostic(sMsgSIP, sToIP, nToPort);
	}


	///////////////////  SIP  /////////////////////////

	public void OnSocketOpenSIP(String sListenIP, int nListenPort)
	{
//		android.util.Log.e("OnSocketOpenSIP", "OnSocketOpenSIP");
		m_objSocketSIP.OpenSocket(sListenIP, nListenPort);
	}

	public void OnSocketCloseSIP()
	{
//		android.util.Log.e("OnSocketCloseSIP", "OnSocketCloseSIP");
		m_objSocketSIP.CloseSocket();
	}

	public void OnSocketSendSIP(byte[] objData, int nDataSize, String sToIP, int nToPort)
	{
//		android.util.Log.e("OnSocketSendSIP", "OnSocketSendSIP");
		m_objSocketSIP.SendData(objData, nDataSize, sToIP, nToPort);
	}

	///////////////////////  RTP  ///////////////////////////////////

	public void OnSocketOpenRTP(int nLineNo, String sListenIP, int nListenPort)
	{
//		android.util.Log.e("OnSocketOpenRTP", "OnSocketOpenRTP");
		m_aSocketRTP[nLineNo] = new SocketRTP(this);
		m_aSocketRTP[nLineNo].OpenSocket(sListenIP, nListenPort);
		m_aSocketRTP[nLineNo].SetLineNo(nLineNo);
	}
	public void OnSocketCloseRTP(int nLineNo)
	{
//		android.util.Log.e("OnSocketCloseRTP", "OnSocketCloseRTP");
		m_aSocketRTP[nLineNo].CloseSocket();
	}
	public void OnSocketSendRTP(int nLineNo, byte[] aData, int nDataSize, String sToIP, int nToPort)
	{
//		android.util.Log.e("OnSocketSendRTP", "OnSocketSendRTP");
		m_aSocketRTP[nLineNo].SendData(aData, nDataSize, sToIP, nToPort);
	}

	////////////////////// Media ///////////////////////////////

	public void OnMicData(byte[] aData, int nDataSize)
	{
//		android.util.Log.e("OnMicData", "OnMicData");
		PostMicDataPCM(aData, nDataSize);
	}

	public void OnOpenMediaDevice(int nLineNo, int nDeviceMIC, int nDeviceSPK)
	{
//		android.util.Log.e("OnOpenMediaDevice", "OnOpenMediaDevice");
		m_objMediaSpk.OpenSpk();
		m_objMediaMic.OpenMic();
	}

	public void OnCloseMediaDevice(int nLineNo)
	{
//		android.util.Log.e("OnCloseMediaDevice", "OnCloseMediaDevice");
		m_objMediaSpk.CloseSpk();
		m_objMediaMic.CloseMic();
	}

	@Override
	public void OnSpkDataPCM(byte[] aData, int nDataSize)
	{
//		android.util.Log.e("OnSpkDataPCM", "OnSpkDataPCM");
		m_objMediaSpk.PlaySpk(aData, nDataSize);
	}
}