package com.googlecode.asyn4j.core.work;

import java.util.concurrent.Callable;

import com.googlecode.asyn4j.core.result.AsynResult;

public interface AsynWork extends Callable<AsynResult> {
	
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
	public AsynResult getAnycResult();
	
	/**
	 * get this thread work name
	 * @return
	 */
    public String getThreadName();

}
