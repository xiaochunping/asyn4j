package com.googlecode.asyn4j.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.result.AsynResult;
import com.googlecode.asyn4j.core.work.AsynWork;
import com.googlecode.asyn4j.core.work.AsynWorkCachedService;

public class AsynWorkExecute implements Runnable {
	private static final Log log = LogFactory
			.getLog(AsynWorkExecute.class);

	private AsynWorkCachedService anycWorkCachedService = null;

	private BlockingQueue<Future<AsynResult>> resultBlockingQueue = null;

	private CompletionService<AsynResult> completionService = null;

	private ExecutorService executorservice = null;

	// execute asyn work number
	private int executeWork = 0;

	public AsynWorkExecute(AsynWorkCachedService anycWorkCachedService,
			ExecutorService executorservice,
			BlockingQueue<Future<AsynResult>> resultBlockingQueue) {
		this.anycWorkCachedService = anycWorkCachedService;
		this.executorservice = executorservice;
		// init work execute threadfactory
		completionService = new ExecutorCompletionService(executorservice,
				resultBlockingQueue);

	}

	@Override
	public void run() {
		if (completionService == null)
			throw new IllegalArgumentException("completionService is null");
		while (true) {
			try {
				// get work form work queue
				AsynWork anycWork = anycWorkCachedService.getWork();
				// execute anyc work
				if (anycWork.getAnycResult() != null) {// have callback
					completionService.submit(anycWork);
				} else {
					executorservice.submit(anycWork);
				}
				++executeWork;
			} catch (Exception e) {
				log.error(e);
			}

		}
	}

	public int getExecuteWork() {
		return executeWork;
	}

}
