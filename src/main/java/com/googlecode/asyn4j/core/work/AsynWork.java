package com.googlecode.asyn4j.core.work;

import java.util.concurrent.Callable;

import com.googlecode.asyn4j.core.callback.AsynCallBack;

public interface AsynWork extends Callable<AsynCallBack> {
	
	/**
	 * get asyn work weight
	 * @return
	 */
	public int getWeight();
	
	/**
	 * set asyn work weight
	 * @param weight
	 */
	public void setWeight(int weight);
	
	/**
	 * get asyn work callbakck
	 * @return
	 */
	public AsynCallBack getAnycResult();
	
	/**
	 * get this thread work name
	 * @return
	 */
    public String getThreadName();

}
