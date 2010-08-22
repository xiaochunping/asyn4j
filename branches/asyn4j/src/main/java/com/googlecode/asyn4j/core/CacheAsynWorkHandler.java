package com.googlecode.asyn4j.core;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.work.AsynWork;

public final class CacheAsynWorkHandler extends WorkQueueFullHandler {
    private final static Log log = LogFactory.getLog(CacheAsynWorkHandler.class);

	private BlockingQueue<AsynWork> cacheLink = null;

	public CacheAsynWorkHandler(int maxLength) {
		cacheLink = new ArrayBlockingQueue<AsynWork>(maxLength);
	}

	@Override
	public void addAsynWork(AsynWork asynWork) {
		boolean result = cacheLink.offer(asynWork);
		if(!result){
			log.warn("asyn work cache queue is full");
		}
	}

	@Override
	public void process() {
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					Map<String, Integer> runstatMap = asynService
							.getRunStatMap();
					if (cacheLink.isEmpty()||runstatMap.get("total") - runstatMap.get("execute") > 300) {
						try {
							log.debug("work queue is full,wait 6s");
							Thread.sleep(6000);
						} catch (InterruptedException e) {

						}
						continue;
					}
					AsynWork asynWork = null;
					try {
						asynWork = cacheLink.take();
						asynService.addAsynWork(asynWork);
						log.debug("execute cache queue task");
					} catch (InterruptedException e) {
					}
				}
			}
		};

		new Thread(runnable).start();

	}

}
