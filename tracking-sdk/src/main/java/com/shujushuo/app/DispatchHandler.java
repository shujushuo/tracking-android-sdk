package com.shujushuo.app;

import android.os.Handler;
import android.os.Looper;

public class DispatchHandler {
	private static final DispatchHandler mInstance = new DispatchHandler();

	public static DispatchHandler getInstance() {
		return mInstance;
	}

	private Thread pThread;
	private Handler pInternalHandler;

	public DispatchHandler() {
		initTheadAndHandler();
	}

	private synchronized void initTheadAndHandler () {
		pThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				pInternalHandler = new Handler(Looper.myLooper());
				Looper.loop();
			}
		};
		pThread.start();
	}


	public void submit(Runnable r) {
		if (pInternalHandler == null) {
			initTheadAndHandler();
		}

		pInternalHandler.post(r);
	}
}
