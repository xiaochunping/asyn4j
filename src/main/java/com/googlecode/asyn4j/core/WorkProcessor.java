package com.googlecode.asyn4j.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.result.AsynResult;
import com.googlecode.asyn4j.core.work.AsynWork;

public class WorkProcessor implements Runnable {
	private static final Log log = LogFactory.getLog(WorkProcessor.class);
	private AsynWork asynWork;
	private String name;
	private BlockingQueue<AsynResult> resultQueue;

	public WorkProcessor(AsynWork asynWork, String name,
			final BlockingQueue<AsynResult> resultQueue) {
		this.asynWork = asynWork;
		this.name = name;
		this.resultQueue = resultQueue;
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		String oldName = currentThread.getName();

		if (name != null) {
			setName(currentThread, name);
		}
		try {
			AsynResult result = asynWork.call();
			resultQueue.offer(result, 2000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.error(e);
		}

	}

	/**
	 * set thread name
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

}
