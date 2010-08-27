package com.googlecode.asyn4j.spring;

import org.springframework.beans.factory.FactoryBean;

import com.googlecode.asyn4j.core.handler.WorkQueueFullHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

public class AsynServiceFactoryBean implements FactoryBean {

	private final static int CPU_NUMBER = Runtime.getRuntime()
			.availableProcessors();

	// default work queue cache size
	private int maxCacheWork = 300;

	// default add work wait time
	private long addWorkWaitTime = Long.MAX_VALUE;

	// work thread pool size
	private int workThreadNum = (CPU_NUMBER / 2) + 1;

	// callback thread pool size
	private int callbackThreadNum = CPU_NUMBER / 2;
	
	private WorkQueueFullHandler workQueueFullHandler;
	
	

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
	
	public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler) {
		this.workQueueFullHandler = workQueueFullHandler;
	}

	@Override
	public Object getObject() throws Exception {
		AsynService asynService = AsynServiceImpl.getService(maxCacheWork, addWorkWaitTime,
				workThreadNum, callbackThreadNum);
		if(workQueueFullHandler!=null){
			asynService.setWorkQueueFullHandler(workQueueFullHandler);
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

}
