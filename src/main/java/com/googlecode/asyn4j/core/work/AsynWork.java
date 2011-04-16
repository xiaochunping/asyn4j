package com.googlecode.asyn4j.core.work;

import com.googlecode.asyn4j.core.callback.AsynCallBack;

/**
 * 
 * @author pan_java
 *
 */
public interface AsynWork {
	
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
    
    /**
     * call target method
     * @return
     */
    public AsynCallBack call() throws Exception;
    
    /**
     * get asyn work weight
     * @return
     */
    public int getWeight();

}
