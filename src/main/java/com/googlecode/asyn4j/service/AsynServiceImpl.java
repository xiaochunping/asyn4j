package com.googlecode.asyn4j.service;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.AsynWorkExecute;
import com.googlecode.asyn4j.core.WorkQueueFullHandler;
import com.googlecode.asyn4j.core.result.AsynResult;
import com.googlecode.asyn4j.core.result.AsynResultCacheService;
import com.googlecode.asyn4j.core.result.AsynResultCachedServiceImpl;
import com.googlecode.asyn4j.core.work.AsynWork;
import com.googlecode.asyn4j.core.work.AsynWorkCachedService;
import com.googlecode.asyn4j.core.work.AsynWorkCachedServiceImpl;
import com.googlecode.asyn4j.core.work.AsynWorkEntity;
import com.googlecode.asyn4j.util.AsynSpringUtil;

@SuppressWarnings("unchecked")
public class AsynServiceImpl implements AsynService {

	private static Log log = LogFactory.getLog(AsynServiceImpl.class);

	private static AsynWorkCachedService anycWorkCachedService = null;

	// default add work wait time
	private static long addWorkWaitTime = Long.MAX_VALUE;

	// default work queue length
	private static int workQueueLength = 300;

	private static final int DEFAULT_WORK_WEIGHT = 5;

	private final static int CPU_NUMBER = Runtime.getRuntime()
			.availableProcessors();
	

	private static ExecutorService workExecutor = null;

	private static ExecutorService callBackExecutor = null;

	private static boolean run = false;

	private static BlockingQueue<AsynResult> resultBlockingQueue = null;

	private static Map<String, Integer> statMap = new HashMap<String, Integer>(
			3);

	private static StringBuilder infoSb = new StringBuilder();

	private AsynWorkExecute asynWorkExecute = null;

	private AsynResultCachedServiceImpl asynResultCacheService = null;
	
	private WorkQueueFullHandler workQueueFullHandler = null;

	public AsynServiceImpl() {
		this(workQueueLength, addWorkWaitTime, ((CPU_NUMBER) / 2)+1,
				(CPU_NUMBER) / 2);
	}

	public AsynServiceImpl(int maxCacheWork, long addWorkWaitTime,
			int workThreadNum, int callBackThreadNum) {
		init(maxCacheWork, addWorkWaitTime, workThreadNum, callBackThreadNum);
	}

	/**
	 * init Asyn Service
	 */
	private void init(int maxCacheWork, long addWorkWaitTime,
			int workThreadNum, int callBackThreadNum) {

		if (!run) {
			workExecutor = Executors.newFixedThreadPool(workThreadNum);

			callBackExecutor = Executors.newFixedThreadPool(callBackThreadNum);

			// init work execute server
			anycWorkCachedService = new AsynWorkCachedServiceImpl(maxCacheWork,addWorkWaitTime);

			// init work execute queue
			resultBlockingQueue = new LinkedBlockingQueue<AsynResult>();

			// start work thread
			asynWorkExecute = new AsynWorkExecute(anycWorkCachedService,
					workExecutor, resultBlockingQueue);
			new Thread(asynWorkExecute).start();

			// start callback thread
			asynResultCacheService = new AsynResultCachedServiceImpl(
					resultBlockingQueue, callBackExecutor);
			callBackExecutor.execute(asynResultCacheService);
			
			new Thread(asynResultCacheService).start();
			
			run = true;
		}

	}

	@Override
	public void addWork(Object[] params, Class clzss, String method,
			AsynResult anycResult) {

		this.addWork(params, clzss, method, anycResult, DEFAULT_WORK_WEIGHT);

	}

	@Override
	public void addWorkWithSpring(Object[] params, String target,
			String method, AsynResult anycResult) {
		this.addWorkWithSpring(params, target, method, anycResult,
				DEFAULT_WORK_WEIGHT);

	}

	@Override
	public void addWork(Object[] params, Class clzss, String method,
			AsynResult anycResult, int weight) {
		Object target = null;
        
		try {
			Constructor constructor = clzss.getConstructor();
			if (constructor == null) {
				throw new IllegalArgumentException(
						"target not have default constructor function");
			}
			// Instance target object
			target = clzss.newInstance();
		} catch (Exception e) {
			log.error(e);
			return;
		}

		AsynWork anycWork = new AsynWorkEntity(target, method, params,
				anycResult);

		anycWork.setWeight(weight);

		addAsynWork(anycWork);

	}

	@Override
	public void addWorkWithSpring(Object[] params, String target,
			String method, AsynResult anycResult, int weight) {
		// get spring bean
		Object bean = AsynSpringUtil.getBean(target);

		if (bean == null)
			throw new IllegalArgumentException("spring bean is null");

		AsynWork anycWork = new AsynWorkEntity(bean, method, params, anycResult);

		anycWork.setWeight(weight);

		addAsynWork(anycWork);

	}

	/**
	 * add asyn work
	 * 
	 * @param asynWork
	 */
	public void addAsynWork(AsynWork asynWork) {
		if (asynWork.getWeight() <= 0) {
			asynWork.setWeight(DEFAULT_WORK_WEIGHT);
		}
		anycWorkCachedService.addWork(asynWork);
	}

	@Override
	public Map<String, Integer> getRunStatMap() {
		if (run) {
			statMap.clear();
			statMap.put("total", anycWorkCachedService.getTotalWork());
			statMap.put("execute", asynWorkExecute.getExecuteWork());
			statMap.put("callback", asynResultCacheService.getResultBack());
		}
		return statMap;
	}

	@Override
	public String getRunStatInfo() {
		if (run) {
			infoSb.delete(0, infoSb.length());
			infoSb.append("total asyn work:").append(
					anycWorkCachedService.getTotalWork()).append("\t");
			infoSb.append(",excute asyn work:").append(
					asynWorkExecute.getExecuteWork()).append("\t");
			infoSb.append(",callback asyn result:").append(
					asynResultCacheService.getResultBack()).append("\t");
		}
		return infoSb.toString();
	}

	public WorkQueueFullHandler getWorkQueueFullHandler() {
		return workQueueFullHandler;
	}

	public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler) {
		this.workQueueFullHandler = workQueueFullHandler;
		this.workQueueFullHandler.setAsynService(this);
	}
	
	

}
