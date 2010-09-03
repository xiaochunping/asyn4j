package com.googlecode.asyn4j.service;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.AsynWorkExecute;
import com.googlecode.asyn4j.core.WorkWeight;
import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.callback.AsynCallBackService;
import com.googlecode.asyn4j.core.callback.AsynCallBackServiceImpl;
import com.googlecode.asyn4j.core.handler.AsynServiceCloseHandler;
import com.googlecode.asyn4j.core.handler.ErrorAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.WorkQueueFullHandler;
import com.googlecode.asyn4j.core.work.AsynWork;
import com.googlecode.asyn4j.core.work.AsynWorkCachedService;
import com.googlecode.asyn4j.core.work.AsynWorkCachedServiceImpl;
import com.googlecode.asyn4j.core.work.AsynWorkEntity;
import com.googlecode.asyn4j.exception.Asyn4jException;
import com.googlecode.asyn4j.spring.AsynSpringUtil;

@SuppressWarnings("unchecked")
public class AsynServiceImpl implements AsynService {

    private static Log                         log                    = LogFactory.getLog(AsynServiceImpl.class);

    private static AsynWorkCachedService       anycWorkCachedService  = null;

    // asyn work default work weight
    private static final WorkWeight            DEFAULT_WORK_WEIGHT    = WorkWeight.MIDDLE;

    private final static int                   CPU_NUMBER             = Runtime.getRuntime().availableProcessors();

    private static ExecutorService             workExecutor           = null;

    private static ExecutorService             callBackExecutor       = null;

    // service run flag
    private static boolean                     run                    = false;

    // call back block queue
    private static BlockingQueue<AsynCallBack> resultBlockingQueue    = null;

    // work queue
    protected static BlockingQueue<AsynWork>   workQueue              = null;

    // status map
    private static Map<String, Integer>        statMap                = new HashMap<String, Integer>(3);

    // status info stringbuffer
    private static StringBuilder               infoSb                 = new StringBuilder();

    private AsynWorkExecute                    asynWorkExecute        = null;

    private AsynCallBackServiceImpl            asynResultCacheService = null;

    private WorkQueueFullHandler               workQueueFullHandler   = null;

    private AsynServiceCloseHandler            closeHander            = null;
    private ErrorAsynWorkHandler               errorAsynWorkHandler   = null;

    // default work queue cache size
    private static int                         maxCacheWork           = 300;

    // default add work wait time
    private static long                        addWorkWaitTime        = Long.MAX_VALUE;

    // work thread pool size
    private static int                         work_thread_num        = (CPU_NUMBER / 2) + 1;

    // callback thread pool size
    private static int                         callback_thread_num    = CPU_NUMBER / 2;

    private static AsynServiceImpl             instance               = null;

    private AsynServiceImpl() {
        this(maxCacheWork, addWorkWaitTime, work_thread_num, callback_thread_num);
    }

    private AsynServiceImpl(int maxCacheWork, long addWorkWaitTime, int workThreadNum, int callBackThreadNum) {
        this.maxCacheWork = maxCacheWork;
        this.addWorkWaitTime = addWorkWaitTime;
        this.work_thread_num = workThreadNum;
        this.callback_thread_num = callBackThreadNum;
    }

    public static AsynServiceImpl getService() {
        if (instance == null) {
            instance = new AsynServiceImpl();
        }
        return instance;
    }

    public static AsynServiceImpl getService(int maxCacheWork, long addWorkWaitTime, int workThreadNum,
                                             int callBackThreadNum) {
        if (instance == null) {
            instance = new AsynServiceImpl(maxCacheWork, addWorkWaitTime, workThreadNum, callBackThreadNum);
        }
        return instance;
    }

    /**
     * init Asyn Service
     */
    @Override
    public void init() {

        if (!run) {
            workExecutor = Executors.newFixedThreadPool(work_thread_num);

            callBackExecutor = Executors.newFixedThreadPool(callback_thread_num);

            workQueue = new PriorityBlockingQueue<AsynWork>();

            // init work execute server
            anycWorkCachedService = new AsynWorkCachedServiceImpl(maxCacheWork, addWorkWaitTime,
                    this.workQueueFullHandler, workQueue);

            // init work execute queue
            resultBlockingQueue = new LinkedBlockingQueue<AsynCallBack>();

            // start work thread
            asynWorkExecute = new AsynWorkExecute(anycWorkCachedService, workExecutor, resultBlockingQueue,
                    errorAsynWorkHandler);
            new Thread(asynWorkExecute).start();

            // start callback thread
            asynResultCacheService = new AsynCallBackServiceImpl(resultBlockingQueue, callBackExecutor);

            new Thread(asynResultCacheService).start();

            if (workQueueFullHandler != null) {
                workQueueFullHandler.process();
            }

            run = true;
        }

        //jvm close run
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                close();
                if (closeHander != null) {
                    closeHander.setAsynWorkQueue(workQueue);
                    closeHander.setCallBackQueue(resultBlockingQueue);
                    closeHander.process();
                }
            }
        });

    }

    @Override
    public void close() {
        if (!run) {
            run = false;
            workExecutor.shutdown();
            callBackExecutor.shutdown();
        }
    }

    @Override
    public void addWork(Object[] params, Class clzss, String method) {
        this.addWork(params, clzss, method, null);

    }

    @Override
    public void addWork(Object[] params, Object tagerObject, String method) {
        this.addWork(params, tagerObject, method, null);

    }

    @Override
    public void addWorkWithSpring(Object[] params, String target, String method) {
        this.addWorkWithSpring(params, target, method, null);

    }

    @Override
    public void addWork(Object[] params, Class clzss, String method, AsynCallBack asynCallBack) {

        this.addWork(params, clzss, method, asynCallBack, DEFAULT_WORK_WEIGHT);

    }

    @Override
    public void addWork(Object[] params, Object tagerObject, String method, AsynCallBack asynCallBack) {
        this.addWork(params, tagerObject, method, asynCallBack, DEFAULT_WORK_WEIGHT);
    }

    @Override
    public void addWorkWithSpring(Object[] params, String target, String method, AsynCallBack asynCallBack) {
        this.addWorkWithSpring(params, target, method, asynCallBack, DEFAULT_WORK_WEIGHT);

    }

    @Override
    public void addWork(Object[] params, Class clzss, String method, AsynCallBack asynCallBack, WorkWeight weight) {
        Object target = null;

        try {
            Constructor constructor = clzss.getConstructor();
            if (constructor == null) {
                throw new IllegalArgumentException("target not have default constructor function");
            }
            // Instance target object
            target = clzss.newInstance();
        } catch (Exception e) {
            log.error(e);
            return;
        }

        AsynWork anycWork = new AsynWorkEntity(target, method, params, asynCallBack);

        anycWork.setWeight(weight.getValue());

        addAsynWork(anycWork);

    }

    @Override
    public void addWork(Object[] params, Object tagerObject, String method, AsynCallBack asynCallBack, WorkWeight weight) {
        if (tagerObject == null) {
            throw new IllegalArgumentException("tager object is null");
        }
        AsynWork anycWork = new AsynWorkEntity(tagerObject, method, params, asynCallBack);

        anycWork.setWeight(weight.getValue());

        addAsynWork(anycWork);

    }

    @Override
    public void addWorkWithSpring(Object[] params, String target, String method, AsynCallBack asynCallBack,
                                  WorkWeight weight) {

        if (target == null || method == null) {
            throw new IllegalArgumentException("target name is null or  target method name is null or weight less 0");
        }
        // get spring bean
        Object bean = AsynSpringUtil.getBean(target);

        if (bean == null)
            throw new IllegalArgumentException("spring bean is null");

        AsynWork anycWork = new AsynWorkEntity(bean, method, params, asynCallBack);

        anycWork.setWeight(weight.getValue());

        addAsynWork(anycWork);

    }

    /**
     * add asyn work
     * 
     * @param asynWork
     * @throws Asyn4jException
     */
    public void addAsynWork(AsynWork asynWork) {
        if (!run) {//if asyn service is stop or no start!
            throw new Asyn4jException("asyn service is stop or no start!");
        }
        if (asynWork == null) {
            throw new IllegalArgumentException("asynWork is null");
        }
        if (asynWork.getWeight() <= 0) {
            asynWork.setWeight(DEFAULT_WORK_WEIGHT.getValue());
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
            infoSb.append("total asyn work:").append(anycWorkCachedService.getTotalWork()).append("\t");
            infoSb.append(",excute asyn work:").append(asynWorkExecute.getExecuteWork()).append("\t");
            infoSb.append(",callback asyn result:").append(asynResultCacheService.getResultBack()).append("\t");
        }
        return infoSb.toString();
    }

    @Override
    public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler) {
        if (run)
            throw new IllegalArgumentException("asyn running");
        if (workQueueFullHandler == null)
            throw new IllegalArgumentException("workQueueFullHandler is null");
        this.workQueueFullHandler = workQueueFullHandler;
        this.workQueueFullHandler.setAsynService(this);
    }

    @Override
    public void setCloseHander(AsynServiceCloseHandler closeHander) {
        if (run)
            throw new IllegalArgumentException("asyn running");
        if (closeHander == null)
            throw new IllegalArgumentException("closeHander is null");
        this.closeHander = closeHander;
    }

    @Override
    public void setErrorAsynWorkHandler(ErrorAsynWorkHandler errorAsynWorkHandler) {
        if (run)
            throw new IllegalArgumentException("asyn running");
        if (errorAsynWorkHandler == null)
            throw new IllegalArgumentException("errorAsynWorkHandler is null");
        this.errorAsynWorkHandler = errorAsynWorkHandler;
    }

}
