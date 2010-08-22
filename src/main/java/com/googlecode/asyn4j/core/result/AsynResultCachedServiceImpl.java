package com.googlecode.asyn4j.core.result;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.CachedService;
import com.googlecode.asyn4j.core.work.AsynWorkCachedServiceImpl;

public class AsynResultCachedServiceImpl implements AsynResultCacheService,
		Runnable {

	private static final Log log = LogFactory
			.getLog(AsynWorkCachedServiceImpl.class);

	private   ExecutorService executor = null;

	private BlockingQueue<AsynResult> anycResultQueue = null;

	// result call back number
	private static int resultBack = 0;

	public AsynResultCachedServiceImpl(
			BlockingQueue<AsynResult> anycResultQueue,ExecutorService executor) {
		this.anycResultQueue = anycResultQueue;
		this.executor = executor;
	}

	@Override
	public void run() {
		while (true) {
			try {
				//executor result work
				AsynResult asynResult = anycResultQueue.take();
                executor.execute(asynResult);
				resultBack++;
			} catch (Exception e) {
				log.error(e);
			}
		}

	}

	public static int getResultBack() {
		return resultBack;
	}

}
