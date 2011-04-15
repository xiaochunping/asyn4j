package com.googlecode.asyn4j.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * TODO Comment of DefauleCloseHandler
 * 
 * @author pan_java
 * @version DefauleCloseHandler.java 2010-8-27 下午07:41:03
 */
public class DefauleAsynServiceHandler extends AsynServiceHandler {

    private final static Log log = LogFactory.getLog(DefauleAsynServiceHandler.class);

	@Override
	public void init() {
		log.info("DefauleCloseHandler  run!");
	}

	@Override
	public void destroy() {
	    log.warn("asyn work have " + asynWorkQueue.size() + " no run!");
        log.warn("call back have " + callBackQueue.size() + " no run!");
    }
    
    

}
