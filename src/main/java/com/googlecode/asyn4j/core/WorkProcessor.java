package com.googlecode.asyn4j.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.handler.ErrorAsynWorkHandler;
import com.googlecode.asyn4j.core.work.AsynWork;

/**
 * @author pan_java
 */

public final class WorkProcessor implements Runnable ,Comparable<WorkProcessor>{
    private static final Log     log = LogFactory.getLog(WorkProcessor.class);
    private AsynWork             asynWork;
    private ErrorAsynWorkHandler errorAsynWorkHandler;
    private ExecutorService      callBackExecutor;
    private Semaphore semaphore;

    public WorkProcessor(AsynWork asynWork, ErrorAsynWorkHandler errorAsynWorkHandler,
                         final ExecutorService callBackExecutor,Semaphore semaphore) {
        this.asynWork = asynWork;
        this.errorAsynWorkHandler = errorAsynWorkHandler;
        this.callBackExecutor = callBackExecutor;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        if (asynWork.getThreadName() != null) {
            setName(currentThread, asynWork.getThreadName());
        }
        AsynCallBack result = null;
        try {
            //asyn work execute
            result = asynWork.call();
        } catch (Throwable throwable ) {
            //if execute asyn work is error,errorAsynWorkHandler disposal
            if (errorAsynWorkHandler != null) {
                errorAsynWorkHandler.addErrorWork(asynWork,throwable);
            }
        }finally{
                semaphore.release();
        }
        if (result != null) {//execute callback
            callBackExecutor.execute(result);
        }

    }

    /**
     * set thread name
     * 
     * @param thread
     * @param name
     */
    private void setName(Thread thread, String name) {
        try {
            thread.setName(name);
        } catch (SecurityException se) {
            log.error(se);
        }
    }

    public AsynWork getAsynWork() {
        return asynWork;
    }
    
    @Override
    public int compareTo(WorkProcessor o) {
        return o.getAsynWork().getWeight() - this.asynWork.getWeight();
    }

}
