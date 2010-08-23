package com.googlecode.asyn4j.core.callback;

public abstract class AsynCallBack implements Runnable {

	protected Object methodResult;

	@Override
	public void run() {
		doNotify();
	}

	public final void setInokeResult(Object object) {
		// set method inoke result
		this.methodResult = object;
	}

	/**
	 * execute callback
	 */
	public abstract void doNotify();

}
