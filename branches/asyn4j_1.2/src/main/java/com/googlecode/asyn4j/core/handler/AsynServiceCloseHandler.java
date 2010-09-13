package com.googlecode.asyn4j.core.handler;

import java.util.concurrent.BlockingQueue;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.work.AsynWork;

/**
 * TODO Comment of AsynServiceCloseHandler
 * 
 * @author pan_java
 * @version AsynServiceCloseHandler.java 2010-8-27 下午07:29:17
 */
public abstract class AsynServiceCloseHandler implements AsynHandler{

    protected BlockingQueue<AsynWork>     asynWorkQueue;

    protected BlockingQueue<AsynCallBack> callBackQueue;

    public void setAsynWorkQueue(BlockingQueue<AsynWork> asynWorkQueue) {
        this.asynWorkQueue = asynWorkQueue;
    }

    public void setCallBackQueue(BlockingQueue<AsynCallBack> callBackQueue) {
        this.callBackQueue = callBackQueue;
    }

    /***
     * do close asyn service if you have set the workQueueFullHandler then
     * workQueueFullHandler a queue ,must save the workQueueFullHandler queue
     */
    public abstract void process();

}
