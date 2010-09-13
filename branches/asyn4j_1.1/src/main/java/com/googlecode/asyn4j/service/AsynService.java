package com.googlecode.asyn4j.service;

import java.util.Map;

import com.googlecode.asyn4j.core.WorkWeight;
import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.handler.AsynServiceCloseHandler;
import com.googlecode.asyn4j.core.handler.ErrorAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.WorkQueueFullHandler;
import com.googlecode.asyn4j.core.work.AsynWork;



/**
 * @author pan_java
 */
public interface AsynService {

    /**
     * add asyn work
     * 
     * @param params －－ params
     * @param clzss －－ traget ClASS
     * @param method －－ target method name
     */
    public void addWork(Object[] params, Class clzss, String method);

    /**
     *  add asyn work
     * 
     * @param params －－ params
     * @param clzss －－ traget ClASS
     * @param method －－target method name
     * @param anycResult -- callback method
     */
    public void addWork(Object[] params, Class clzss, String method, AsynCallBack asynCallBack);

    /**
     * add asyn work
     * 
     * @param params －－ params
     * @param clzss －－ traget ClASS
     * @param method －－target method name
     */
    public void addWork(Object[] params, Object tagerObject, String method);

    /**
     * add asyn work
     * 
     * @param params －－ params
     * @param clzss －－ traget ClASS
     * @param method －－ target method name
     * @param asynCallBack --callback method
     */
    public void addWork(Object[] params, Object tagerObject, String method, AsynCallBack asynCallBack);

    /**
     * add asyn work
     * 
     * @param params －－ params
     * @param clzss －－ traget ClASS
     * @param method －－  target method name
     * @param asynCallBack －－ callback method
     */
    public void addWork(Object[] params, Class clzss, String method, AsynCallBack asynCallBack, WorkWeight weight);

    /**
     * add asyn work
     * 
     * @param params －－ params
     * @param clzss －－ traget Object
     * @param method －－ target method name
     * @param asynCallBack －－ callback method
     * @param weight －－ work weight
     */
    public void addWork(Object[] params, Object tagerObject, String method, AsynCallBack asynCallBack, WorkWeight weight);

    /**
     * add asyn work with Spring Bean
     * 
     * @param params －－ params
     * @param target －－ target bean name
     * @param method －－ target method name
     */
    public void addWorkWithSpring(Object[] params, String target, String method);

    /**
     *  add asyn work with Spring Bean
     * 
     * @param params －－ params
     * @param target －－  target bean name
     * @param method －－ target method name
     * @param asynCallBack -- callback object
     */
    public void addWorkWithSpring(Object[] params, String target, String method, AsynCallBack asynCallBack);

    /**
     *  add asyn work with Spring Bean
     * 
     * @param params －－ params
     * @param target －－ target bean name
     * @param method －－ target method name
     * @param asynCallBack －－target method name
     * @param weight －－work weight
     */
    public void addWorkWithSpring(Object[] params, String target, String method, AsynCallBack asynCallBack, WorkWeight weight);

    /**
     * add asyn work
     * 
     * @param asynWork －－ asyn work entity
     */
    public void addAsynWork(AsynWork asynWork);

    /**
     * get run stat map
     * 
     * @return
     */
    public Map<String, Integer> getRunStatMap();

    /**
     * get run stat string
     * 
     * @return
     */
    public String getRunStatInfo();

    /**
     * add work cache work queue
     * 
     * @param workQueueFullHandler
     */
    public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler);

    /**
     * start service
     */
    public void init();

    /**
     * close service
     */
    public void close();

    /**
     * set close service handler
     * 
     * @param closeHander
     */
    public void setCloseHander(AsynServiceCloseHandler closeHander);

    /**
     * set error asyn work handler
     * 
     * @param errorAsynWorkHandler
     */
    public void setErrorAsynWorkHandler(ErrorAsynWorkHandler errorAsynWorkHandler);

}
