package com.googlecode.asyn4j.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.work.AsynWork;

/**
 * 
 * @author pan_java
 *
 */

public class WorkProcessor implements Runnable {
	private static final Log log = LogFactory.getLog(WorkProcessor.class);
	private AsynWork asynWork;
	private String name;
	private BlockingQueue<AsynCallBack> resultQueue;

	public WorkProcessor(AsynWork asynWork, String name,
			final BlockingQueue<AsynCallBack> resultQueue) {
		this.asynWork = asynWork;
		this.name = name;
		this.resultQueue = resultQueue;
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		if (name != null) {
			setName(currentThread, name);
		}
		try {
			AsynCallBack result = asynWork.call();
			//execute work total increment
			AsynWorkExecute.incrementExecuteWork();
			if (result != null)
				resultQueue.offer(result, 2000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.error(e);
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

}
