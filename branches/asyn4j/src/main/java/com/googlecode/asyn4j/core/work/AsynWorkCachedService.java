package com.googlecode.asyn4j.core.work;

/**
 * 
 * @author pan_java
 *
 */
public interface AsynWorkCachedService {

	/**
	 * add anyc Work
	 * 
	 * @param anycWork
	 */
	public void addWork(AsynWork anycWork);

	/**
	 * get anyc Work
	 * 
	 * @return
	 */
	public AsynWork getWork();
	
	
	/**
	 * get asyn work total
	 * @return
	 */
	public int getTotalWork();

}
