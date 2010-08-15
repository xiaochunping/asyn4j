package com.googlecode.asyn4j.core.work;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AsynWorkCachedServiceImpl implements AsynWorkCachedService {

	private static final Log log = LogFactory
			.getLog(AsynWorkCachedServiceImpl.class);

	// work queue
	protected static BlockingQueue<AsynWork> workQueue = null;

	private long addWorkWaitTime = 0;

	// total asyn work total
	private int totalWork = 0;

	public AsynWorkCachedServiceImpl(int queueLength) {
		workQueue = new PriorityBlockingQueue<AsynWork>(queueLength);
	}

	public AsynWorkCachedServiceImpl(int queueLength, long addWorkWaitTime) {
		workQueue = new PriorityBlockingQueue<AsynWork>(queueLength);
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
			// if work queue full,wait time
			boolean addFlag = workQueue.offer(anycWork, addWorkWaitTime,
					TimeUnit.MILLISECONDS);
			++totalWork;
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
