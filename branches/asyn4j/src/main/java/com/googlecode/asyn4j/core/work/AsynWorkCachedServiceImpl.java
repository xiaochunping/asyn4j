package com.googlecode.asyn4j.core.work;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.AsynWorkExecute;

public class AsynWorkCachedServiceImpl implements AsynWorkCachedService {

	private static final Log log = LogFactory
			.getLog(AsynWorkCachedServiceImpl.class);

	// work queue
	protected static BlockingQueue<AsynWork> workQueue = null;

	private long addWorkWaitTime = 0;

	// total asyn work total
	private static int totalWork = 0;

	public AsynWorkCachedServiceImpl() {
		workQueue = new PriorityBlockingQueue<AsynWork>();
	}

	public AsynWorkCachedServiceImpl(long addWorkWaitTime) {
		workQueue = new PriorityBlockingQueue<AsynWork>();
		this.addWorkWaitTime = addWorkWaitTime;
	}

	public AsynWorkCachedServiceImpl(BlockingQueue<AsynWork> workQueue,
			long addWorkWaitTime) {
		if (workQueue == null) {
			throw new IllegalArgumentException("workQueue is null");
		}
		this.workQueue = workQueue;
		this.addWorkWaitTime = addWorkWaitTime;
	}

	@Override
	public void addWork(AsynWork anycWork) {
		try {
			if(totalWork - AsynWorkExecute.getExecuteWork()>300){
				log.warn("work queue is full");
				return;
			}
			// if work queue full,wait time
			boolean addFlag = workQueue.offer(anycWork, addWorkWaitTime,
					TimeUnit.MILLISECONDS);
			if(!addFlag){
				log.warn("work add fail");
			}else{
				++totalWork;
			}
		} catch (InterruptedException e) {
			log.error(e);
		}
	}

	public AsynWork getWork() {
		try {
			return workQueue.take();
		} catch (InterruptedException e) {
			log.error(e);
			throw new RuntimeException();
		}
	}

	public int getTotalWork() {
		return totalWork;
	}

}
