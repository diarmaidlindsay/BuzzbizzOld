package com.vax.dev.lib;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.os.Handler;
import android.os.Looper;

public class SocketSIP
{
	private static final int MAX_UDP_DATAGRAM_LEN = 2048;
	DatagramSocket m_objDgramSocket = null;

	/** ライブラリー用ハンドラー */
	public final Handler m_objHandler = new Handler(Looper.myLooper());

	final VaxSIPUserAgent m_objVaxSIPUserAgent;

	Thread m_objSockRecvThread = null;

	public String GetMyIP() {
		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();

					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}


	public SocketSIP(VaxSIPUserAgent objVaxSIPUserAgent)
	{
		m_objVaxSIPUserAgent = objVaxSIPUserAgent;
	}

	public boolean OpenSocket(String sListenIP, int nListenPort)
	{
		CloseSocket();

		try
		{
			if(sListenIP == null || sListenIP.length() == 0)
			{
				m_objDgramSocket = new DatagramSocket(nListenPort);

			}
			else
			{
				InetAddress objListenIP = InetAddress.getByName(sListenIP);
				m_objDgramSocket = new DatagramSocket(nListenPort, objListenIP);

			}
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			android.util.Log.e("SocketSIP", e.toString());
			return false;
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			android.util.Log.e("SocketSIP", e.toString());
			return false;
		}

		m_objSockRecvThread = new Thread(new RunableSockRecvThread());
		m_objSockRecvThread.start();

		return true;

	}

	class RunableSockRecvThread implements Runnable
	{
		public void run()
		{
			String sFromIP = null;
			int nFromPort = 0;

			while(true)
			{
				byte[] aData = new byte[MAX_UDP_DATAGRAM_LEN];
		    	DatagramPacket objDgramPacket = new DatagramPacket(aData, aData.length);

				try
				{
					m_objDgramSocket.receive(objDgramPacket);

					sFromIP = objDgramPacket.getAddress().toString();
					nFromPort = objDgramPacket.getPort();

				}
				catch (SocketException e)
				{
					e.printStackTrace();
					android.util.Log.e("SocketSIP", e.toString());
					break;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					android.util.Log.e("SocketSIP", e.toString());
					break;
				}

				PostSocketRecv(aData, objDgramPacket.getLength(), sFromIP, nFromPort);
				//m_objVaxSIPUserAgent.PostSocketRecvSIP(aData, objDgramPacket.getLength(), sFromIP, nFromPort);
			}
		}
	};

	public void CloseSocket()
	{
		if(m_objDgramSocket == null)
			return;

		m_objDgramSocket.close();
		Sleep(50);

		m_objDgramSocket = null;
		m_objSockRecvThread = null;
	}

 	class RunableSocketRecv implements Runnable
 	{
 	    byte[] m_sRecvData = null;
 	    int m_nRecvDataSize = 0;

 	    String m_sRecvIP = null;
 	    int m_nRecvPort = -1;

 	    RunableSocketRecv(byte[] aData, int nDataSize, String sRecvIP, int nRecvPort)
 	    {
 	    	m_sRecvData = new byte[nDataSize];
 	    	System.arraycopy(aData, 0, m_sRecvData, 0, nDataSize);

 	    	m_nRecvDataSize = nDataSize;

 	    	m_sRecvIP = sRecvIP;
 	    	m_nRecvPort = nRecvPort;
 	    }

 	    public void run()
 	    {
 	    	OnSocketRecv(m_sRecvData, m_nRecvDataSize, m_sRecvIP, m_nRecvPort);
 	    }
 	}

 	public void OnSocketRecv(byte[] aData, int nDataSize, String sFromIP, int nFromPort)
    {
 		m_objVaxSIPUserAgent.PostSocketRecvSIP(aData, nDataSize, sFromIP, nFromPort);
    }

	public void PostSocketRecv(byte[] aData, int nDataSize, String sFromIP, int nFromPort)
	{

		RunableSocketRecv rRun = new RunableSocketRecv(aData, nDataSize, sFromIP, nFromPort);
		m_objHandler.post(rRun);

	}

	public void SendData(final byte[] sData, final int nDataSize, final String sToIP, final int nToPort)
	{
		if(m_objDgramSocket == null) {
			return;
		}

		// スレッド化
		new Thread(new Runnable() {
			@Override
			public void run() {
				DatagramPacket dp = null;
				try {
					InetAddress IP = InetAddress.getByName(sToIP);
					dp = new DatagramPacket(sData, nDataSize, IP, nToPort);
					m_objDgramSocket.send(dp);
				} catch (IOException e) {
					e.printStackTrace();
					android.util.Log.e("SocketSIP", e.toString());
				}
			}
		}).start();
	}

	public boolean IsAvailablePort(int nListenPort)
	{
		try
		{
			DatagramSocket objDgramSocket = new DatagramSocket(nListenPort);

			if(objDgramSocket.isClosed())
			{
				return false;
			}

			objDgramSocket.close();
			objDgramSocket = null;
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			android.util.Log.e("SocketSIP", e.toString());
			return false;
		}

		return true;
	}

	private void Sleep(int nMilliSec)
	{
		try
		{
			Thread.sleep(nMilliSec);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			android.util.Log.e("SocketSIP", e.toString());
		}
	}
}