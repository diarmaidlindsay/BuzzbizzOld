package com.vax.dev.lib;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SocketRTP 
{
//	private static final int MAX_UDP_DATAGRAM_LEN = 2048; //512 ->切れ切れ　、ビジホ->スマホは綺麗
private static final int MAX_UDP_DATAGRAM_LEN = 2048;
	DatagramSocket m_objDgramSocket = null;
	int m_nLineNo = -1;
	
	 Handler m_objHandler = new Handler(Looper.myLooper());
	
	VaxSIPUserAgent m_objVaxSIPUserAgent = null;
	Thread m_objSockRecvThread = null;
	
	public SocketRTP(VaxSIPUserAgent objVaxSIPUserAgent)
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
			return false;
		}
		catch (UnknownHostException e) 
		{
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
			byte[] aData = new byte[MAX_UDP_DATAGRAM_LEN];
	    	DatagramPacket objDgramPacket = new DatagramPacket(aData, aData.length);
			
	    	String sFromIP = null;
			int nFromPort = 0;
									
			while(true)
			{
				try 
				{
					m_objDgramSocket.receive(objDgramPacket);
					
					sFromIP = objDgramPacket.getAddress().toString();
					nFromPort = objDgramPacket.getPort();
				
				} 
				catch (SocketException e) 
				{
					break;
				}
				catch (IOException e) 
				{
					break;
				}
				
				PostSocketRecv(aData, objDgramPacket.getLength(), sFromIP, nFromPort);
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
 		m_objVaxSIPUserAgent.PostSocketRecvRTP(m_nLineNo, aData, nDataSize, sFromIP, nFromPort);
    }
	    	
	public void PostSocketRecv(byte[] aData, int nDataSize, String sFromIP, int nFromPort)
	{
		
		RunableSocketRecv rRun = new RunableSocketRecv(aData, nDataSize, sFromIP, nFromPort);
		
		m_objHandler.post(rRun);
	}

	public void SendData(final byte[] aData, final int nDataSize, final String sToIP, final int nToPort)
	{
		if(m_objDgramSocket == null) return;

		// スレッド化
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InetAddress IP = InetAddress.getByName(sToIP);
					DatagramPacket dp = new DatagramPacket(aData, nDataSize, IP, nToPort);

					m_objDgramSocket.send(dp);
				} catch (IOException e) {
					e.printStackTrace();
					android.util.Log.e("SocketSIP", e.toString());
				}
			}
		}).start();

	}
	
	public void SetLineNo(int nLineNo)
	{
		m_nLineNo = nLineNo;
	}

	public static boolean IsAvailablePort(int nListenPort)
	{
		Log.d("IsAvailablePort","IsAvailablePort");
		try 
		{
			DatagramSocket objDgramSocket = new DatagramSocket(nListenPort);
			
			if(objDgramSocket.isClosed())
			{
				return false;
			}
			objDgramSocket.close();
		}
		catch (SocketException e) 
		{
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
		}
	}
}
