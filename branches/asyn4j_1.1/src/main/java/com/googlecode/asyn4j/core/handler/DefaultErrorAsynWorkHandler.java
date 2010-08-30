/**
 * Project: asyn4j
 * 
 * File Created at 2010-8-27
 * $Id$
 * 
 * Copyright 2008 Alibaba.com Croporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.googlecode.asyn4j.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.work.AsynWork;

/**
 * TODO Comment of DefaultErrorAsynWorkHandler
 *
 * @author yuncheng
 * @version DefaultErrorAsynWorkHandler.java 2010-8-27 下午07:56:56
 *
 */
public class DefaultErrorAsynWorkHandler extends ErrorAsynWorkHandler{
    private final static Log log = LogFactory.getLog(DefaultErrorAsynWorkHandler.class);
    
    @Override
    public void addErrorWork(AsynWork asynWork) {
        log.info(asynWork.getThreadName() + " run is error");
    }

  
    
    

}
