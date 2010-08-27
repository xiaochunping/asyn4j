package com.googlecode.asyn4j.core.callback;

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

/**
 * 
 * @author pan_java
 *
 */
public class AsynCallBackServiceImpl implements AsynCallBackService,
		Runnable {

	private static final Log log = LogFactory
			.getLog(AsynWorkCachedServiceImpl.class);

	private   ExecutorService executor = null;

	private BlockingQueue<AsynCallBack> anycResultQueue = null;

	// result call back number
	private static int resultBack = 0;

	public AsynCallBackServiceImpl(
			BlockingQueue<AsynCallBack> anycResultQueue,ExecutorService executor) {
		this.anycResultQueue = anycResultQueue;
		this.executor = executor;
	}

	@Override
	public void run() {
		while (true) {
			try {
				//executor result work
				AsynCallBack asynResult = anycResultQueue.take();
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
