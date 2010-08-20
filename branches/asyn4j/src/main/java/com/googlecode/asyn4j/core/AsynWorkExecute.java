package com.googlecode.asyn4j.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.result.AsynResult;
import com.googlecode.asyn4j.core.work.AsynWork;
import com.googlecode.asyn4j.core.work.AsynWorkCachedService;

public class AsynWorkExecute implements Runnable {
	private static final Log log = LogFactory.getLog(AsynWorkExecute.class);

	private AsynWorkCachedService anycWorkCachedService = null;

	private BlockingQueue<AsynResult> resultBlockingQueue = null;

	private ExecutorService executorservice = null;

	// execute asyn work number
	public static AtomicInteger executeWork = new AtomicInteger();;

	public AsynWorkExecute(AsynWorkCachedService anycWorkCachedService,
			ExecutorService executorservice,
			BlockingQueue<AsynResult> resultBlockingQueue) {
		this.anycWorkCachedService = anycWorkCachedService;
		this.executorservice = executorservice;
		this.resultBlockingQueue = resultBlockingQueue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				// get work form work queue
				AsynWork anycWork = anycWorkCachedService.getWork();
				// execute anyc work
				executorservice.execute(new WorkProcessor(anycWork, anycWork
						.getThreadName(), resultBlockingQueue));
			} catch (Exception e) {
				log.error(e);
			}

		}
	}

	public static int getExecuteWork() {
		return executeWork.intValue();
	}

	public static int incrementExecuteWork() {
		return executeWork.incrementAndGet();
	}

}
