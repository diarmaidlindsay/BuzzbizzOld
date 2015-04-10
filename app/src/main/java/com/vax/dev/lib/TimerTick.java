package com.vax.dev.lib;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;

public class TimerTick
{
	final VaxSIPUserAgent m_objVaxSIPUserAgent;

	/** ライブラリー用ハンドラー */
	public final Handler m_objHandler = new Handler(Looper.myLooper());

	TimerTask m_objTimerTask;
	Timer m_objTimerTick;

	TimerTick(VaxSIPUserAgent objVaxSIPUserAgent)
	{
		m_objVaxSIPUserAgent = objVaxSIPUserAgent;
	}

	class RunableTimerTick implements Runnable
	{
		public void run()
		{
			m_objVaxSIPUserAgent.PostOneSecondTick();
		}
	};

	class TimerTaskEx extends TimerTask
	{
		@Override
		public void run()
		{
			m_objHandler.post(new RunableTimerTick());
		}
	};

	public void StartTimer(int nMillisec)
	{
		m_objTimerTask = new TimerTaskEx();

		m_objTimerTick = new Timer();
		m_objTimerTick.schedule(m_objTimerTask, nMillisec, nMillisec);
	}

	public void StopTimer()
	{
		if(m_objTimerTask != null){
			m_objTimerTask.cancel();
		}

		if(m_objTimerTick != null){
			m_objTimerTick.cancel();
		}
	}
}
