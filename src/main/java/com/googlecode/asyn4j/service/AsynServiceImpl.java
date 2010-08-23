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

	// asyn work default work weight
	private static final int DEFAULT_WORK_WEIGHT = 5;

	private final static int CPU_NUMBER = Runtime.getRuntime()
			.availableProcessors();

	private static ExecutorService workExecutor = null;

	private static ExecutorService callBackExecutor = null;

	// service run flag
	private static boolean run = false;

	// call back block queue
	private static BlockingQueue<AsynResult> resultBlockingQueue = null;

	// status map
	private static Map<String, Integer> statMap = new HashMap<String, Integer>(
			3);

	// status info stringbuffer
	private static StringBuilder infoSb = new StringBuilder();

	private AsynWorkExecute asynWorkExecute = null;

	private AsynResultCachedServiceImpl asynResultCacheService = null;

	private WorkQueueFullHandler workQueueFullHandler = null;

	// default work queue cache size
	private static int maxCacheWork = 300;

	// default add work wait time
	private static long addWorkWaitTime = Long.MAX_VALUE;

	// work thread pool size
	private static int work_thread_num = (CPU_NUMBER / 2) + 1;

	// callback thread pool size
	private static int callback_thread_num = CPU_NUMBER / 2;

	public AsynServiceImpl() {
		this(maxCacheWork, addWorkWaitTime, work_thread_num,
				callback_thread_num);
	}

	public AsynServiceImpl(int maxCacheWork, long addWorkWaitTime,
			int workThreadNum, int callBackThreadNum) {
		this.maxCacheWork = maxCacheWork;
		this.addWorkWaitTime = addWorkWaitTime;
		this.work_thread_num = workThreadNum;
		this.callback_thread_num = callBackThreadNum;
	}

	/**
	 * init Asyn Service
	 */
	@Override
	public void init() {

		if (!run) {
			workExecutor = Executors.newFixedThreadPool(work_thread_num);

			callBackExecutor = Executors
					.newFixedThreadPool(callback_thread_num);

			// init work execute server
			anycWorkCachedService = new AsynWorkCachedServiceImpl(maxCacheWork,
					addWorkWaitTime, this.workQueueFullHandler);

			// init work execute queue
			resultBlockingQueue = new LinkedBlockingQueue<AsynResult>();

			// start work thread
			asynWorkExecute = new AsynWorkExecute(anycWorkCachedService,
					workExecutor, resultBlockingQueue);
			new Thread(asynWorkExecute).start();

			// start callback thread
			asynResultCacheService = new AsynResultCachedServiceImpl(
					resultBlockingQueue, callBackExecutor);

			new Thread(asynResultCacheService).start();

			if (workQueueFullHandler != null) {
				workQueueFullHandler.process();
			}

			run = true;
		}

	}

	@Override
	public void addWork(Object[] params, Class clzss, String method,
			AsynResult anycResult) {

		this.addWork(params, clzss, method, anycResult, DEFAULT_WORK_WEIGHT);

	}

	@Override
	public void addWork(Object[] params, Object tagerObject, String method,
			AsynResult anycResult) {
		this.addWork(params, tagerObject, method, anycResult,
				DEFAULT_WORK_WEIGHT);
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
	public void addWork(Object[] params, Object tagerObject, String method,
			AsynResult anycResult, int weight) {
		if (tagerObject == null) {
			throw new IllegalArgumentException(
					"tager object is null");
		}
		AsynWork anycWork = new AsynWorkEntity(tagerObject, method, params,
				anycResult);

		anycWork.setWeight(weight);

		addAsynWork(anycWork);

	}

	@Override
	public void addWorkWithSpring(Object[] params, String target,
			String method, AsynResult anycResult, int weight) {

		if (target == null || method == null || weight < 0) {
			throw new IllegalArgumentException(
					"target name is null or  target method name is null or weight less 0");
		}
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
		if (asynWork == null) {
			throw new IllegalArgumentException("asynWork is null");
		}
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

	@Override
	public void setWorkQueueFullHandler(
			WorkQueueFullHandler workQueueFullHandler) {
		this.workQueueFullHandler = workQueueFullHandler;
		this.workQueueFullHandler.setAsynService(this);
	}

}
