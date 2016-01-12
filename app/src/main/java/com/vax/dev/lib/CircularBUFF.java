package com.vax.dev.lib;

public class CircularBUFF 
{
	 byte[] m_aBUFF = null;
	 
	 private int m_nTotalSize = 0;	
	 private int m_nAvailableCount = 0;	
	 private int m_nWriteIndex = 0;
	 private int m_nReadIndex = 0;
	 
	 public CircularBUFF() 
	 {		 	
		 m_aBUFF = null;
		 m_nTotalSize = 0;

		 m_nAvailableCount = 0;
		 m_nWriteIndex = 0;
		 m_nReadIndex = 0;

		 SetSizeBUFF(16000);
//		 SetSizeBUFF(2000);//MediaMiの方も2000にしたがアプリが強制終了してしまう
	 }
	 
	 public void ResetBUFF()
	 {
	 	m_nAvailableCount = 0;
	 	m_nWriteIndex = 0;
	 	m_nReadIndex = 0;
	 }
	 
	 public void SetSizeBUFF(int nSize)
	 {
	 	m_aBUFF = null;
	 	m_aBUFF = new byte[nSize];
	 	
	 	m_nTotalSize = nSize;
	 	ResetBUFF();
	 }
	
	 public boolean WriteToBUFF(byte[] aData, int nDataSize)
	 {
	 	if((m_nAvailableCount + nDataSize) > m_nTotalSize)
	 		return false;
	 	
	 	int nWriteAvail = m_nTotalSize - m_nWriteIndex;
	 	
	 	if(nWriteAvail < nDataSize)
	 	{
	 		System.arraycopy(aData, 0, m_aBUFF, m_nWriteIndex, nWriteAvail);
	 		
	 		int nWriteRemain = nDataSize - nWriteAvail;
	 		System.arraycopy(aData, nWriteAvail, m_aBUFF, 0, nWriteRemain);
	 		
	 		m_nWriteIndex = nWriteRemain;
	 	}
	 	else 
	 	{
	 		System.arraycopy(aData, 0, m_aBUFF, m_nWriteIndex, nDataSize);
	 		m_nWriteIndex += nDataSize;
	 	}

	 	if(m_nWriteIndex >= m_nTotalSize)
	 		m_nWriteIndex = 0;
	 	
	 	m_nAvailableCount += nDataSize;
	 	
	 	return true;
	 }
	 
	 public int ReadToBUFF(byte[] aData, int nDataSize)
	 {
	 	if(m_nAvailableCount == 0)
	 		return 0;
	 	
	 	if(m_nAvailableCount < nDataSize)
	 		nDataSize = m_nAvailableCount;
	 	
	 	int nReadAvail = m_nTotalSize - m_nReadIndex;
	 	
	 	if(nReadAvail < nDataSize)
	 	{
	 		System.arraycopy(m_aBUFF, m_nReadIndex, aData, 0, nReadAvail);
	 	
	 		int nReadRemain = nDataSize - nReadAvail;
	 		System.arraycopy(m_aBUFF, 0, aData, nReadAvail, nReadRemain);
	 	
	 		m_nReadIndex = nReadRemain;
	 	}
	 	else 
	 	{
	 		System.arraycopy(m_aBUFF, m_nReadIndex, aData, 0, nDataSize);
	 		m_nReadIndex += nDataSize;
	 	}
	 	
	 	m_nAvailableCount -= nDataSize;
	 	
	 	if(m_nReadIndex >= m_nTotalSize)
	 		m_nReadIndex = 0;
	 	
	 	return nDataSize;
	 }
	 
	 public Boolean IsAvailable(int nDataSize)
	 {
	 	if(m_nAvailableCount < nDataSize)
	 		return false;

	 	return true;
	 }
	 
}
