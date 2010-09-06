package com.googlecode.asyn4j.spring;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

import com.googlecode.asyn4j.core.handler.AsynServiceCloseHandler;
import com.googlecode.asyn4j.core.handler.ErrorAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.WorkQueueFullHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

public class AsynServiceFactoryBean implements FactoryBean {

    private final static Log log               = LogFactory.getLog(AsynServiceFactoryBean.class);

    private final static int CPU_NUMBER        = Runtime.getRuntime().availableProcessors();

    // default work queue cache size
    private int              maxCacheWork      = 300;

    // default add work wait time
    private long             addWorkWaitTime   = Long.MAX_VALUE;

    // work thread pool size
    private int              workThreadNum     = (CPU_NUMBER / 2) + 1;

    // callback thread pool size
    private int              callbackThreadNum = CPU_NUMBER / 2;

    private String           workQueueFullHandler;

    private String           errorAsynWorkHandler;

    private String           asynServiceCloseHandler;

    public void setMaxCacheWork(int maxCacheWork) {
        this.maxCacheWork = maxCacheWork;
    }

    public void setAddWorkWaitTime(long addWorkWaitTime) {
        this.addWorkWaitTime = addWorkWaitTime;
    }

    public void setWorkThreadNum(int workThreadNum) {
        this.workThreadNum = workThreadNum;
    }

    public void setCallbackThreadNum(int callbackThreadNum) {
        this.callbackThreadNum = callbackThreadNum;
    }

    public void setWorkQueueFullHandler(String workQueueFullHandler) {
        this.workQueueFullHandler = workQueueFullHandler;
    }
    
    public void setErrorAsynWorkHandler(String errorAsynWorkHandler) {
        this.errorAsynWorkHandler = errorAsynWorkHandler;
    }

    public void setAsynServiceCloseHandler(String asynServiceCloseHandler) {
        this.asynServiceCloseHandler = asynServiceCloseHandler;
    }

    @Override
    public Object getObject() throws Exception {
        AsynService asynService = AsynServiceImpl.getService(maxCacheWork, addWorkWaitTime, workThreadNum,
                callbackThreadNum);
        //set some handler
        if (workQueueFullHandler != null) {
            asynService.setWorkQueueFullHandler((WorkQueueFullHandler) newObject(workQueueFullHandler));
        }
        if (errorAsynWorkHandler != null) {
            asynService.setErrorAsynWorkHandler((ErrorAsynWorkHandler) newObject(errorAsynWorkHandler));
        }
        if (asynServiceCloseHandler != null) {
            asynService.setCloseHander((AsynServiceCloseHandler) newObject(asynServiceCloseHandler));
        }
        asynService.init();
        return asynService;

    }

    @Override
    public Class getObjectType() {
        return AsynServiceImpl.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * init object
     * 
     * @param className
     * @return
     */
    private Object newObject(String className) {
        Class clzss = null;
        try {
            clzss = Class.forName(className);
            Constructor constructor = clzss.getConstructor();
            if (constructor != null) {
                return constructor.newInstance();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

}
