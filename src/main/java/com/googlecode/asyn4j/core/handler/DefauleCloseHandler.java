package com.googlecode.asyn4j.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * TODO Comment of DefauleCloseHandler
 * 
 * @author pan_java
 * @version DefauleCloseHandler.java 2010-8-27 下午07:41:03
 */
public class DefauleCloseHandler extends AsynServiceCloseHandler {

    private final static Log log = LogFactory.getLog(DefauleCloseHandler.class);

    @Override
    public void process() {
        log.warn("asyn work have " + asynWorkQueue.size() + " no run!");
        log.warn("call back have " + callBackQueue.size() + " no run!");
    }

}
