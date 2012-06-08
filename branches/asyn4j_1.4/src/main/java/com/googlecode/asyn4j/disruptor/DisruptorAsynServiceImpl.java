package com.googlecode.asyn4j.disruptor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.googlecode.asyn4j.core.work.AsynWork;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

/**
 * Disruptor 
 * @author yuncheng
 *
 */
public class DisruptorAsynServiceImpl extends AsynServiceImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4220625201178331518L;

	private static Lock lock = new ReentrantLock();

	private DisruptorAsynServiceImpl() {
		super();
	}

	private DisruptorAsynServiceImpl(int maxCacheWork, long addWorkWaitTime,
			int workThreadNum, int callBackThreadNum, long closeServiceWaitTime) {
		super(maxCacheWork, addWorkWaitTime, workThreadNum, callBackThreadNum,
				closeServiceWaitTime);
	}

	public static AsynService getService() {
		lock.lock();
		try {
			if (instance == null) {
				instance = new DisruptorAsynServiceImpl();
				Helper.initAndStart();
			}
		} finally {
			lock.unlock();
		}
		return instance;
	}

	public static AsynService getService(int maxCacheWork,
			long addWorkWaitTime, int workThreadNum, int callBackThreadNum,
			long closeServiceWaitTime) {
		lock.lock();
		try {
			if (instance == null) {
				instance = new DisruptorAsynServiceImpl(maxCacheWork,
						addWorkWaitTime, workThreadNum, callBackThreadNum,
						closeServiceWaitTime);
				Helper.initAndStart();
			}
		} finally {
			lock.unlock();
		}
		return instance;

	}

	@Override
	public void addAsynWork(AsynWork asynWork) {
		  Helper.produce(asynWork);
	}
	
	

}
