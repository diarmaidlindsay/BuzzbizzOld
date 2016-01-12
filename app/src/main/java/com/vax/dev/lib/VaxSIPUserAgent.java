package com.vax.dev.lib;

import android.util.Log;

import jp.pulseanddecibels.buzbiz.models.LibEventListener;

//Vaxの制御クラスを継承、リスナー
public class VaxSIPUserAgent extends VaxSIPUserAgentSO
{
	final LibEventListener buzbiz_listener;

	// TODO 追加
	public boolean reLogin(){
		return RegisterToProxy(1800);
	}

	public void CloseMic()
	{
		m_objMediaMic.CloseMic();
	}

	public static final int TOTAL_LINE_COUNT = 5;

	public static final int G711U_CodecNo = 0;
	public static final int G711A_CodecNo = 1;
	public static final int GSM_CodecNo = 2;
	public static final int iLBC_CodecNo = 3;
	public static final int Opus_CodecNo = 4;

	SocketSIP m_objSocketSIP = new SocketSIP(this);

	MediaMic m_objMediaMic = new MediaMic(this);
	MediaSPK m_objMediaSpk = new MediaSPK();

//	TimerTick m_objTimerTick = new TimerTick(this);
	SocketRTP[] m_aSocketRTP = null;

	boolean m_bOnline = false;
	boolean m_bMuteMic = false;
	boolean m_bMuteSpk = false;

	boolean m_bDonotDisturb = false;
	boolean m_bIsSprakerOn = false;

	boolean m_aBusyStatus[] = new boolean[TOTAL_LINE_COUNT]; // the default value is false

	int m_nLocalPortSIP = 5060;
//int m_nLocalPortSIP = 56131;
	int m_nLocalPortRTP = 4000;
	//rtp.conf->rtpstart=5000 rtpend=20000
	//ビジホの声だけスマホに届く場合は20秒で切られる
	//2038はビジホの声だけスマホに届く
	//5004はビジホの声だけスマホに届く
	//7000だと双方聞こえない->furunowifが原因?
	//9000はビジホの声だけスマホに届く
	//1100はビジホの声だけスマホに届く
	//19000はビジホの声だけスマホに届く
	// iphonが1100のため変更20151126
//	int m_nLocalPortRTP = 5004;//元祖
//int m_nLocalPortRTP = 10010;

public VaxSIPUserAgent(LibEventListener objVaxThread)
	{
		buzbiz_listener = objVaxThread;
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
		if(!super.RegisterToProxy(nExpire))
		{
			return false;
		}

		OnTryingToRegister();
		return true;
	}

	@Override
	public boolean UnRegisterToProxy()
	{
		return super.UnRegisterToProxy();
	}

	@Override
	public boolean DialCall(int nLineNo, String sDialNo, int nInputDeviceId, int nOutputDeviceId) {
		if (!super.DialCall(nLineNo, sDialNo, nInputDeviceId, nOutputDeviceId))
			return false;

		OnDialing(nLineNo);
		return true;
	}

	@Override
	public boolean Connect(int nLineNo, String sToURI, int nInputDeviceId, int nOutputDeviceId)
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
	public boolean ForceInbandDTMF(int nLineNo, boolean bEnable) {
		return super.ForceInbandDTMF(nLineNo, bEnable);
	}

	@Override
	public boolean DigitDTMF(int nLineNo, int nDigit)
	{
		return super.DigitDTMF(nLineNo, nDigit);
	}

	@Override
	public void DeselectAllVoiceCodec() {
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
	public boolean SetLicenceKey(String sLicenceKey) {
		return super.SetLicenceKey(sLicenceKey);
	}

	@Override
	public String GetMyIP()
	{
		return super.GetMyIP();
	}

	@Override
	public int GetVaxObjectError() {
		return super.GetVaxObjectError();
	}

	@Override
	public boolean MuteLineMIC(int nLineNo, boolean bEnable) {
		return super.MuteLineMIC(nLineNo, bEnable);
	}

	@Override
	public boolean MuteLineSPK(int nLineNo, boolean bEnable) {
		return super.MuteLineSPK(nLineNo, bEnable);
	}

	@Override
	public boolean MicSetSoftBoost(boolean bEnable) {
		return super.MicSetSoftBoost(bEnable);
	}

	@Override
	public boolean MicGetSoftBoost() {
		return super.MicGetSoftBoost();
	}

	@Override
	public boolean SpkSetSoftBoost(boolean bEnable) {
		return super.SpkSetSoftBoost(bEnable);
	}

	@Override
	public boolean SpkGetSoftBoost() {
		return super.SpkGetSoftBoost();
	}

	@Override
	public boolean MicSetAutoGain(int nValue) {
		return super.MicSetAutoGain(nValue);
	}

	@Override
	public int MicGetAutoGain() {
		return super.MicGetAutoGain();
	}

	@Override
	public boolean SpkSetAutoGain(int nValue) {
		return super.SpkSetAutoGain(nValue);
	}

	@Override
	public int SpkGetAutoGain() {
		return super.SpkGetAutoGain();
	}

	@Override
	public boolean VoiceChanger(int nPitch) {
		return super.VoiceChanger(nPitch);
	}

	@Override
	public boolean SetEchoCancellation(boolean bEnable) {
		return super.SetEchoCancellation(bEnable);
	}

	@Override
	public boolean GetEchoCancellation() {
		return super.GetEchoCancellation();
	}

	@Override
	public boolean DonotDisturb(boolean bEnable)
	{
		 m_bDonotDisturb = bEnable;
		return super.DonotDisturb(bEnable);
	}

	@Override
	public boolean SpeakerPhone(boolean bEnable) {
		return super.SpeakerPhone(bEnable);
	}

	@Override
	public boolean DiagnosticLog(boolean bEnable) {
		return super.DiagnosticLog(bEnable);
	}

	@Override
	public void PostOneSecondTick()
	{
		super.PostOneSecondTick();
	}

	@Override
	public void PostSocketRecvSIP(byte[] sData, int nDataSize, String sFromIP, int nFromPort) {
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
//		m_objTimerTick.StopTimer();
		m_bOnline = false;
		UnInitialize();
	}

	public boolean VaxInit(String sUserName, String sDisplayName, String nAuthLogin, String sAuthPass, String sDomian, String sSIPProxy, boolean bRegister, int nTotalLines)
	{

		boolean bResult = true;
		String sMyIP = m_objSocketSIP.GetMyIP().toString();

		//TODO 20151128変更 700 ->20000
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
			return false;

		bResult = OpenLines(sMyIP, nTotalLines);
		if(bResult == false) return false;

		if(bRegister) RegisterToProxy(1800);

//		m_objTimerTick.StartTimer(1000);
		m_bOnline = true;
		return true;

	}

	//空いているRTPポートの検索（２づつ）
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

        	ForceInbandDTMF(nLineNo, true);

        	nListenPortRTP += 2; //Increament by 2, RTP port must be an even number (According to the SDP RFC)
         }

          if(bResult == false)
          {
        	  return false;
          }

          return true;
    }

	public boolean VaxIsOnline()
	{
		return m_bOnline;
	}

	//???
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

	//???
	public void MuteMic(boolean bMute)
	{
		m_bMuteMic = bMute;
		m_objMediaMic.Mute(bMute);
	}

	public void MuteSpk(boolean bMute)
	{
		m_bMuteSpk = bMute;
		m_objMediaSpk.Mute(bMute);
	}

	public boolean CryptCOMM(boolean bEnable, String sRemoteIP, int nRemotePort)
	{
		return super.CryptCOMM(bEnable, sRemoteIP, nRemotePort);
	}

///////////////////////////////////////////    EVENTS    ////////////////////////////////////////////////////////////////////////////////////

	public void OnSuccessToRegister()
	{
		buzbiz_listener.OnSuccessToRegister();
	}

 	public void OnSuccessToReRegister()
	{
		buzbiz_listener.OnSuccessToReRegister();
	}
	public void OnSuccessToUnRegister()
	{
		buzbiz_listener.OnSuccessToUnRegister();
	}

	public void OnTryingToRegister()
	{
		buzbiz_listener.OnTryingToRegister();
	}
	public void OnTryingToReRegister()
	{
		buzbiz_listener.OnTryingToReRegister();
	}
	public void OnTryingToUnRegister()
	{
		buzbiz_listener.OnTryingToUnRegister();
	}

	public void OnFailToRegister(int nStatusCode, String sReasonPhrase)
	{
		buzbiz_listener.OnFailToRegister(nStatusCode, sReasonPhrase);
	}
	public void OnFailToReRegister(int nStatusCode, String sReasonPhrase)
	{
		buzbiz_listener.OnFailToReRegister(nStatusCode, sReasonPhrase);
	}
	public void OnFailToUnRegister(int nStatusCode, String sReasonPhrase)
	{
		buzbiz_listener.OnFailToUnRegister(nStatusCode, sReasonPhrase);
	}


	public void OnDialing(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = true;
		buzbiz_listener.OnDialing(nLineNo);
	}

	public void OnAccepting(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = true;
		buzbiz_listener.OnAccepting(nLineNo);
	}

	public void OnEndCall(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = false;
		buzbiz_listener.OnEndCall(nLineNo);
	}

	public void OnConnecting(int nLineNo)
	{
		buzbiz_listener.OnConnecting(nLineNo);
	}

	public void OnTryingToHold(int nLineNo)
	{
		buzbiz_listener.OnTryingToHold(nLineNo);
	}
	public void OnTryingToUnHold(int nLineNo)
	{
		buzbiz_listener.OnTryingToUnHold(nLineNo);
	}
	public void OnFailToHold(int nLineNo)
	{
		buzbiz_listener.OnFailToHold(nLineNo);
	}
	public void OnFailToUnHold(int nLineNo)
	{
		buzbiz_listener.OnFailToUnHold(nLineNo);
	}
	public void OnSuccessToHold(int nLineNo)
	{
		buzbiz_listener.OnSuccessToHold(nLineNo);
	}

	public void OnSuccessToUnHold(int nLineNo)
	{
		buzbiz_listener.OnSuccessToUnHold(nLineNo);
	}

	public void OnFailToConnect(int nLineNo)
	{
		buzbiz_listener.OnFailToConnect(nLineNo);
	}

	public void OnIncomingCall(String sCallId, String sDisplayName, String sUserName, String sFromURI, String sToURI)
	{
		buzbiz_listener.OnIncomingCall(sCallId, sDisplayName, sUserName, sFromURI, sToURI);
	}

	public void OnIncomingCallRingingStart(String sCallId)
	{
		buzbiz_listener.OnIncomingCallRingingStart(sCallId);
	}

	public void OnIncomingCallRingingStop(String sCallId)
	{
		buzbiz_listener.OnIncomingCallRingingStop(sCallId);
	}

	public void OnConnected(int nLineNo, String sTxRTPIP, int nTxRTPPort, String sCallId)
	{
		buzbiz_listener.OnConnected(nLineNo, sTxRTPIP, nTxRTPPort, sCallId);
	}

	public void OnProvisionalResponse(int nLineNo, int nStatusCode, String sReasonPharase)
	{
		buzbiz_listener.OnProvisionalResponse(nLineNo, nStatusCode, sReasonPharase);
	}
	public void OnFailureResponse(int nLineNo, int nStatusCode, String sReasonPharase)
	{
		buzbiz_listener.OnFailureResponse(nLineNo, nStatusCode, sReasonPharase);
	}

	public void OnRedirectResponse(int nLineNo, int nStatusCode, String sReasonPharase, String sContact)
	{
		buzbiz_listener.OnRedirectResponse(nLineNo, nStatusCode, sReasonPharase, sContact);
	}

	public void OnDisconnectCall(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = false;
		buzbiz_listener.OnDisconnectCall(nLineNo);
	}

	public void OnCallTransferAccepted(int nLineNo)
	{
		m_aBusyStatus[nLineNo] = false;
		buzbiz_listener.OnCallTransferAccepted(nLineNo);
	}

	public void OnFailToTransfer(int nLineNo, int nStatusCode, String sReasonPharase)
	{
		buzbiz_listener.OnFailToTransfer(nLineNo, nStatusCode, sReasonPharase);
	}

	public void OnIncomingDiagnostic(String sMsgSIP, String sFromIP, int nFromPort)
	{
		buzbiz_listener.OnIncomingDiagnostic(sMsgSIP, sFromIP, nFromPort);
	}
	public void OnOutgoingDiagnostic(String sMsgSIP, String sToIP, int nToPort)
	{
		buzbiz_listener.OnOutgoingDiagnostic(sMsgSIP, sToIP, nToPort);
	}

	///////////////////  SIP  /////////////////////////

	public void OnSocketOpenSIP(String sListenIP, int nListenPort)
	{
		m_objSocketSIP.OpenSocket(sListenIP, nListenPort);
	}

	public void OnSocketCloseSIP()
	{
		m_objSocketSIP.CloseSocket();
	}

	public void OnSocketSendSIP(byte[] objData, int nDataSize, String sToIP, int nToPort)
	{
		m_objSocketSIP.SendData(objData, nDataSize, sToIP, nToPort);
	}

	///////////////////////  RTP  ///////////////////////////////////

	public void OnSocketOpenRTP(int nLineNo, String sListenIP, int nListenPort)
	{
		m_aSocketRTP[nLineNo] = new SocketRTP(this);
		m_aSocketRTP[nLineNo].OpenSocket(sListenIP, nListenPort);
		m_aSocketRTP[nLineNo].SetLineNo(nLineNo);
	}
	public void OnSocketCloseRTP(int nLineNo)
	{
		m_aSocketRTP[nLineNo].CloseSocket();
	}
	public void OnSocketSendRTP(int nLineNo, byte[] aData, int nDataSize, String sToIP, int nToPort)
	{
		m_aSocketRTP[nLineNo].SendData(aData, nDataSize, sToIP, nToPort);
	}

	////////////////////// Media ///////////////////////////////

	public void OnMicData(byte[] aData, int nDataSize)
	{
		PostMicDataPCM(aData, nDataSize);
	}

	public void OnOpenMediaDevice(int nLineNo, int nDeviceMIC, int nDeviceSPK)
	{
		m_objMediaSpk.OpenSpk();
		m_objMediaMic.OpenMic();
	}

	public void OnCloseMediaDevice(int nLineNo)
	{
		m_objMediaSpk.CloseSpk();
		m_objMediaMic.CloseMic();
	}

	public void OnSpkDataPCM(byte[] aData, int nDataSize)
	{
		m_objMediaSpk.PlaySpk(aData, nDataSize);
	}
}
